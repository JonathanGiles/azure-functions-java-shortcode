package net.jonathangiles.azure.shorturl.storage;

import net.jonathangiles.azure.shorturl.storage.azure.AzureTableStore;

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
            dataStore = new AzureTableStore();
        }

        return dataStore;
    }
}
