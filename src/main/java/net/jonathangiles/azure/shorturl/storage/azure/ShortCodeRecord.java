package net.jonathangiles.azure.shorturl.storage.azure;

import com.microsoft.azure.storage.table.TableServiceEntity;
import net.jonathangiles.azure.shorturl.util.Util;

public class ShortCodeRecord extends TableServiceEntity {

    private String longUrl;

    public ShortCodeRecord() { }

    public ShortCodeRecord(String longUrl, String shortCode) {
        super(Util.getPartitionKey(shortCode), shortCode);
        setLongUrl(longUrl);
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortCode() {
        return rowKey;
    }
}