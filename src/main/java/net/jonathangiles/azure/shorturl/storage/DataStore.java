package net.jonathangiles.azure.shorturl.storage;

/**
 * Abstraction class over the means through which short code mappings are persisted.
 *
 * @see DataStoreFactory
 */
public interface DataStore {

    /**
     * Given a shortcode, attempt to return the mapped url.
     *
     * @param shortCode The shortcode that was assigned to the given url
     * @return The long url represented by the shortcode, if it exists, or null if there is no mapping.
     */
    String getLongUrl(String shortCode);

    /**
     * Given a url, attempt to return the mapped short code.
     *
     * @param longUrl The url that has previously been given a short code.
     * @return The short code represented by the url, if it exists, or null if there is no mapping.
     */
    String getShortCode(String longUrl);

    /**
     * Method attempts to register the given long URL as the given short code, and returns the resulting
     * shortCode, which may be null if there was an error (e.g. the requested short code is already in use), the given
     * short code if the long URL is not already registered, or a different shortCode if the long URL is already
     * registered (and checkForDupes is true).
     *
     * @param longUrl The url for which we want to use the given short code for future reference
     * @param shortCode The short code to use to refer to the given url
     * @param checkForDupes If true, we will look to see if the long URL is already in the data store, and reuse its
     *                      existing short code instead. If false, we will duplicate the long URL with the new
     *                      shortCode, assuming the shortCode is unique.
     * @return The short code that was actually used - which may differ from the requested short code.
     */
    default String saveShortCode(String longUrl, String shortCode, boolean checkForDupes) {
        if (longUrl == null || longUrl.isEmpty() || shortCode == null || shortCode.isEmpty()) {
            throw new IllegalArgumentException("longUrl and shortCode must be non-null and non-empty");
        }

        longUrl = longUrl.toLowerCase();

        // check if longUrl is already known in the database
        if (checkForDupes) {
            String existingShortCode = getShortCode(longUrl);
            if (existingShortCode != null && !existingShortCode.isEmpty()) {
                return existingShortCode;
            }
        }

        return persistShortCode(longUrl, shortCode) ? shortCode : null;
    }

    /**
     * This method should not be called directly - instead {@see #saveShortCode} should be called as it handles
     * error handling. This method should be implemented by implementations of {@code DataStore} to do the actual
     * persisting of the url -> shortCode mapping into storage.
     *
     * @param longUrl The url for which we want to use the given short code for future reference
     * @param shortCode The short code to use to refer to the given url
     * @return true if successfully persisted, false if there was a failure
     */
    boolean persistShortCode(String longUrl, String shortCode);
}
