package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.HttpRequestMessage;
import com.microsoft.azure.serverless.functions.HttpResponseMessage;
import com.microsoft.azure.serverless.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.HttpTrigger;
import net.jonathangiles.azure.shorturl.storage.DataStoreFactory;
import net.jonathangiles.azure.shorturl.util.Util;

import java.util.*;

/**
 * This class is responsible for converting a shortcode into a long url, and redirecting the user to that location.
 */
public class RedirectFunction {

    @FunctionName("redirect")
    public HttpResponseMessage<String> redirect(
            @HttpTrigger(name = "req", methods = {"get"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String shortCode = request.getQueryParameters().getOrDefault("shortcode", null);
        String url;

        if (shortCode == null || shortCode.isEmpty()) {
            Util.TELEMETRY_CLIENT.trackEvent("No shortcode provided, returning default url instead");
            url = Util.getHost(request).getDefaultURL();
        } else {
            String _shortCode = shortCode.toLowerCase();
            if (_shortCode.equals(Util.ROBOTS_TXT)) {
                context.getLogger().info("Request for robots.txt ignored");
                return request.createResponse(Util.HTTP_STATUS_OK, Util.ROBOTS_RESPONSE);
            }

            url = Util.trackDependency(
                    "AzureTableStorage",
                    "Retrieve",
                    () -> DataStoreFactory.getInstance().getLongUrl(shortCode),
                    proposedUrl -> proposedUrl != null && !proposedUrl.isEmpty());

            if (url == null) {
                url = Util.getHost(request).getDefaultURL();
            }
        }

        HttpResponseMessage response = request.createResponse(Util.HTTP_STATUS_REDIRECT, url);
        response.addHeader("Location", url);
        return response;
    }
}
