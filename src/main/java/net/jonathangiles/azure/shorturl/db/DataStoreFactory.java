package net.jonathangiles.azure.shorturl.db;

import net.jonathangiles.azure.shorturl.db.impls.JPADatabase;
import net.jonathangiles.azure.shorturl.db.impls.SQLDatabase;

/**
 * Factory that returns the relevant instance of a {@link DataStore} that can be used to persist short code mappings.
 */
public class DataStoreFactory {

    private static DataStore dataStore;

    /**
     * Returns the correct {@link DataStore} instance.
     * @returne The {@link DataStore} instance to be used at runtime.
     */
    public static DataStore getInstance() {
        if (dataStore == null) {
//            dataStore = new SQLDatabase();
            dataStore = new JPADatabase();
        }

        return dataStore;
    }
}
