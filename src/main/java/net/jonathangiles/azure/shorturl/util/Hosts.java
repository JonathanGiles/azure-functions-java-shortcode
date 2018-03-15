package net.jonathangiles.azure.shorturl.util;

/**
 * Because this shortcode function supports two short urls - jogil.es and java.ms - I've extracted them into this
 * enum so that they can be centrally managed.
 */
public enum Hosts {

    JOGILES("jogil.es", "http://jonathangiles.net", "WT.mc_id=link-jogil.es-jogiles"),
    JAVA_MS("java.ms", "https://docs.microsoft.com/en-us/java/azure/?WT.mc_id=link-java.ms-jogiles", "WT.mc_id=link-java.ms-jogiles");

    private String host;
    private String trackingCode;
    private String defaultURL;

    Hosts(String host, String defaultURL, String trackingCode) {
        this.host = host;
        this.defaultURL = defaultURL;
        this.trackingCode = trackingCode;
    }

    /**
     * Returns the host name for the enum value, e.g. 'jogil.es' or 'java.ms'
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the tracking code to append to Microsoft-owned properties
     */
    public String getTrackingCode() {
        return trackingCode;
    }

    /**
     * Returns the default URL to use if a requested shortcode is not found (or if not shortcode is provided)
     */
    public String getDefaultURL() {
        return defaultURL;
    }

    /**
     * Attempts to find the enum for a given host value. If this fails, the default JOGILES value is returned.
     */
    public static Hosts lookup(String host) {
        if (host == null || host.isEmpty()) return JOGILES;
        for (Hosts h : values()) {
            if (h.getHost().equalsIgnoreCase(host)) return h;
        }
        return JOGILES;
    }
}
