package net.jonathangiles.azure.shorturl.db.impls;

import net.jonathangiles.azure.shorturl.db.DataStore;
import net.jonathangiles.azure.shorturl.model.ShortCode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Properties;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JPADatabase implements DataStore {

//    private static final String PERSISTENCE_XML_TEST = "META-INF/persistence-test.xml";
//    private static final String PERSISTENCE_XML_PRODUCTION = "META-INF/persistence-production.xml";
    private static final String PERSISTENCE_UNIT_NAME = "shortcodes";

//    private final Properties pros;
    private final EntityManagerFactory factory;
    private final EntityManager em;

    public JPADatabase() {
//        System.out.println("Static 1");
//        String env = System.getenv("shortcode.service.production");
//        String file = env == null || env.isEmpty() ? PERSISTENCE_XML_TEST : PERSISTENCE_XML_PRODUCTION;
//
//        System.out.println("Static 3, env: " + env);
//        pros = new Properties();
//        pros.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, file);

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }

    public static void main(String[] args) {
        new JPADatabase().initDatabase();
    }

    public void initDatabase() {
        em.createQuery("select t from ShortCode t");
    }

    @Override
    public String getLongUrl(String shortCode) {
        // TODO store so we don't create every time
        Query q = em.createQuery("SELECT u.longUrl FROM ShortCode u WHERE u.shortCode = :shortCode");
        q.setParameter("shortCode", shortCode);

        try {
            String longUrl = (String) q.getSingleResult();

            // TODO add the visit

            return longUrl;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String getShortCode(String longUrl) {
        // TODO store so we don't create every time
        Query q = em.createQuery("SELECT u.shortCode FROM ShortCode u WHERE u.longUrl = :longUrl");
        q.setParameter("longUrl", longUrl);

        try {
            return(String) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean persistShortCode(String longUrl, String shortCode) {
        try {
            em.getTransaction().begin();
            em.persist(new ShortCode(longUrl, shortCode));
            em.getTransaction().commit();
            System.out.println("Successfully stored " + longUrl + " as shortcode '" + shortCode + "'");
            return true;
        } catch (DatabaseException e) {
            return false;
        }
    }
}
