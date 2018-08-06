package net.jonathangiles.azure.shorturl.util;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.azure.functions.HttpRequestMessage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods.
 */
public class Util {
    /**
     * Because we have a separate v1 proxy app, we configure that proxy to forward the host and referrer in a jg-* header
     * to the actual v2 function app. This allows for us to determine if the request came in through jogil.es, java.ms, etc
     */
    public static final String HEADER_JG_HOST = "jg-host";
    public static final String HEADER_JG_REFERRER = "jg-referrer";

    public static final String PROCESSING_QUEUE_NAME = "processingQueue";
    public static final String PROCESSING_QUEUE_QUEUE_NAME = "urlclicks";

    // We don't want robots coming to our shortcode app, so we block them
    public static final String REJECT_SHORTCODE_ROBOTS_TXT = "robots.txt";
    public static final String ROBOTS_RESPONSE = "user-agent: *\ndisallow: /";

    // We get a lot of requests for a shortcode of 'wp-login.php', that is, people assuming the server is running
    // wordpress and trying to hack it
    public static final String REJECT_SHORTCODE_WP_LOGIN = "wp-login.php";

    // So that we can get some insights logged into Azure Application Insights, we create a telemetry client here
    // and configure it with the instrumentation key
    public static final TelemetryClient TELEMETRY_CLIENT = new TelemetryClient();

    // a list of domains (as regular expressions) that we want to add Microsoft tracking IDs to
    private static final List<String> MICROSOFT_DOMAINS = Arrays.asList(
            "(.*\\.)?microsoft\\.com$",
            "(.*\\.)?msdn\\.com$",
            "(.*\\.)?visualstudio\\.com$",
            "www.microsoftevents.com");

    /**
     * Checks if the given URL should have a tracking ID added, and if so, it adds it.
     */
    public static String addMicrosoftTracking(String urlString, HttpRequestMessage<?> request) {
        if (urlString.toLowerCase().contains("?wt")) return urlString;

        try {
            URL url = new URL(urlString);
            String host = url.getHost();

            boolean isMicrosoftUrl = MICROSOFT_DOMAINS.stream().anyMatch(host::matches);
            if (!isMicrosoftUrl) return urlString;

            String tracking = getHost(request).getTrackingCode();

            return urlString.contains("?") ? (url + "&" + tracking) : (url + "?" + tracking);
        } catch (MalformedURLException e) {
            return urlString;
        }
    }

    /**
     * Convenience method to log telemetry data into application insights.
     */
    public static <T> T trackDependency(String dependencyName, String commandName, Supplier<T> task, Function<T, Boolean> success) {
        long start = System.currentTimeMillis();
        T result = task.get();
        long end = System.currentTimeMillis();
        Util.TELEMETRY_CLIENT.trackDependency(dependencyName, commandName, new Duration(end - start), success.apply(result));
        return result;
    }

    /**
     * Returns the host that did this http request. This uses the {@link #HEADER_JG_HOST} header key because the proxy
     * is running in a separate function app (due to v2 not supporting proxies at this time).
     */
    public static Hosts getHost(HttpRequestMessage<?> request) {
        return Hosts.lookup(request.getHeaders().get(HEADER_JG_HOST));
    }

    /**
     * Simple algorithm to generate a alphanumeric key of a given length
     */
    public static String generateKey(int length) {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int type = random.nextInt(3);
            switch (type) {
                case 0: key.append((char)(random.nextInt(10) + 48)); break; // 0-9
                case 1: key.append((char)(random.nextInt(26) + 65)); break; // A-Z
                case 2: key.append((char)(random.nextInt(26) + 97)); break; // a-z
            }
        }

        return key.toString();
    }

    /**
     * Our partition key is simply the first character of the shortcode, to have even distribution
     */
    public static String getPartitionKey(String shortCode) {
        return shortCode.substring(0, 1);
    }
}
