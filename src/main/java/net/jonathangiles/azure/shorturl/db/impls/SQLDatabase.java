package net.jonathangiles.azure.shorturl.db.impls;

import net.jonathangiles.azure.shorturl.db.DataStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class SQLDatabase implements DataStore {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "shortcodesDB";
    private static final String DATABASE_ARGS = "?useSSL=FALSE";
    private static final String DATABASE_FULL_URL = DATABASE_URL + DATABASE_NAME + DATABASE_ARGS;

    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "secret";
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";

    public SQLDatabase() {
        try {
            Class.forName(DATABASE_DRIVER);

            // test if we should create database
            try (Connection con = DriverManager.getConnection(DATABASE_URL + DATABASE_ARGS, DATABASE_USER, DATABASE_PASSWORD)){
                con.createStatement().execute("CREATE DATABASE " + DATABASE_NAME);
            } catch (SQLException e) {
                // database already exists (or can't connect)
            }

            try (Connection con = DriverManager.getConnection(DATABASE_FULL_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                con.createStatement().executeUpdate("CREATE TABLE short_codes (" +
                        "short_code VARCHAR(20) NOT NULL, " +
                        "long_url VARCHAR(255), " + // TODO urls might be longer than 255 chars
                        "PRIMARY KEY (short_code))");
            } catch (SQLException e) {
                // table already exists (or can't connect)
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public String getLongUrl(String shortCode) {
        return execute("SELECT long_url FROM short_codes s WHERE s.short_code = ?", this::firstResult, shortCode);
    }

    @Override
    public String getShortCode(String longUrl) {
        return execute("SELECT short_code FROM short_codes s WHERE s.long_url = ?", this::firstResult, longUrl);
    }

    @Override
    public boolean persistShortCode(String longUrl, String shortCode) {
        return execute("INSERT INTO short_codes (short_code, long_url) VALUES (?,?)", ps -> {
            try {
                ps.execute();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }, shortCode, longUrl);
    }

    private <T> T execute(String sql, Function<PreparedStatement, T> func, String... args) {
        // TODO connection pooling
        try (Connection con = DriverManager.getConnection(DATABASE_FULL_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setString(i + 1, args[i]);
            }
            return func == null ? null : func.apply(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ResultSet execQuery(PreparedStatement ps) {
        try {
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String firstResult(PreparedStatement ps) {
        try {
            ResultSet rs = execQuery(ps);
            if (rs.next()) {
                // TODO add the visit
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
