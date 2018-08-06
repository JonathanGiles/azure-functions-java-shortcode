package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.QueueOutput;
import net.jonathangiles.azure.shorturl.storage.DataStoreFactory;
import net.jonathangiles.azure.shorturl.util.Hosts;
import net.jonathangiles.azure.shorturl.util.Util;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is responsible for converting a shortcode into a long url, and redirecting the user to that location.
 */
public class RedirectFunction {

    @FunctionName("redirect")
    public HttpResponseMessage redirect(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @QueueOutput(name = Util.PROCESSING_QUEUE_NAME, queueName = Util.PROCESSING_QUEUE_QUEUE_NAME, connection = "AzureWebJobsStorage") OutputBinding<String> queue,
            final ExecutionContext context) {

        Hosts host = Util.getHost(request);
        String shortCode = request.getQueryParameters().getOrDefault("shortcode", null);
        String url;

        if (shortCode == null || shortCode.isEmpty()) {
            Util.TELEMETRY_CLIENT.trackEvent("No shortcode provided, returning default url instead");
            url = host.getDefaultURL();
        } else {
            String _shortCode = shortCode.toLowerCase();
            switch (_shortCode) {
                case Util.REJECT_SHORTCODE_ROBOTS_TXT: return request.createResponseBuilder(HttpStatus.OK).body(Util.ROBOTS_RESPONSE).build();
                case Util.REJECT_SHORTCODE_WP_LOGIN: return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
            }

            url = Util.trackDependency(
                    "AzureTableStorage",
                    "Retrieve",
                    () -> DataStoreFactory.getInstance().getLongUrl(shortCode),
                    proposedUrl -> proposedUrl != null && !proposedUrl.isEmpty());

            if (url == null) {
                url = host.getDefaultURL();
            }
        }

        // we don't want to process the request details now, so we put the data into the processing queue and have
        // a separate function deal with that. This enables this function to return more quickly and the user get
        // to their intended destination.
        String referrer = request.getHeaders().getOrDefault(Util.HEADER_JG_REFERRER, "Unknown");
        String userAgent = request.getHeaders().getOrDefault("user-agent", "Unknown");

        String payload = Stream.of(
                    shortCode == null ? "null" : shortCode,
                    url,
                    host.getHost(),
                    System.currentTimeMillis() + "",
                    referrer,
                    userAgent)
                .map(str -> str.replace("|", "^"))
                .collect(Collectors.joining("|"));

        queue.setValue(payload);

        return request.createResponseBuilder(HttpStatus.TEMPORARY_REDIRECT)
                .body(url)
                .header("Location", url)
                .build();
    }
}

