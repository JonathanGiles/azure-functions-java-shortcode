package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.HttpRequestMessage;
import com.microsoft.azure.serverless.functions.HttpResponseMessage;
import com.microsoft.azure.serverless.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.HttpTrigger;
import net.jonathangiles.azure.shorturl.db.DataStoreFactory;

import java.util.*;

public class RedirectFunction {
    @FunctionName("redirect")
    public HttpResponseMessage<String> redirect(
            @HttpTrigger(name = "req", methods = {"get"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String shortCode = request.getQueryParameters().getOrDefault("shortcode", null);

        String url = DataStoreFactory.getInstance().getLongUrl(shortCode);

        if (url == null) {
            url = "http://www.jonathangiles.net";
        }

        final int status = 302;
        HttpResponseMessage response = request.createResponse(status, url);
        response.addHeader("Location", url);
        return response;
    }
}
