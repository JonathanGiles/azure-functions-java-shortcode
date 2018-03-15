package net.jonathangiles.azure.shorturl.storage.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.*;
import net.jonathangiles.azure.shorturl.storage.DataStore;
import net.jonathangiles.azure.shorturl.util.Util;

public class AzureTableStore implements DataStore {

    private static final String TABLE_NAME = "shortcodes";
    private static final String storageConnectionString = System.getenv("AzureWebJobsStorage");

    private CloudTable cloudTable;

    @Override
    public synchronized String getLongUrl(String shortCode) {
        try {
            TableOperation lookupLongUrl = TableOperation.retrieve(Util.getPartitionKey(shortCode), shortCode, ShortCodeRecord.class);
            TableResult result = getTable().execute(lookupLongUrl);

            if (result == null) return null;

            ShortCodeRecord record = result.getResultAsType();
            return record.getLongUrl();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized String getShortCode(String longUrl) {
        TableQuery<ShortCodeRecord> query = TableQuery
                .from(ShortCodeRecord.class)
                .where("LongUrl eq '" + longUrl + "'");

        try {
            Iterable<ShortCodeRecord> results = getTable().execute(query);
            for (ShortCodeRecord record : results) {
                return record.getShortCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized boolean persistShortCode(String longUrl, String shortCode) {
        try {
            getTable().execute(TableOperation.insertOrReplace(new ShortCodeRecord(longUrl, shortCode)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private CloudTable getTable() throws Exception {
        if (cloudTable == null) {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            cloudTable = tableClient.getTableReference(TABLE_NAME);
            cloudTable.createIfNotExists();
        }
        return cloudTable;
    }
}
