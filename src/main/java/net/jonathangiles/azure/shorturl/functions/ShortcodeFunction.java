package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.HttpRequestMessage;
import com.microsoft.azure.serverless.functions.HttpResponseMessage;
import com.microsoft.azure.serverless.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.serverless.functions.annotation.BindingName;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.HttpTrigger;
import net.jonathangiles.azure.shorturl.db.DataStore;
import net.jonathangiles.azure.shorturl.db.DataStoreFactory;
import net.jonathangiles.azure.shorturl.util.Utils;

import java.util.Optional;

// Some code derived from https://gist.github.com/rakeshsingh/64918583972dd5a08012
public class ShortcodeFunction {

    private static final String INITIAL_URL = "http://jogil.es/";
    private static final int KEY_LENGTH = 5;

    @FunctionName("shortcode")
    public HttpResponseMessage<String> shortcode(
            @HttpTrigger(name = "req", methods = {"post"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("url") String url,
            final ExecutionContext context) {

        DataStore dataStore = DataStoreFactory.getInstance();

        String shortCode = "";
        while (shortCode == null || shortCode.isEmpty()) {
            shortCode = dataStore.saveShortCode(url, Utils.generateKey(KEY_LENGTH));
        }

        String shortUrl = INITIAL_URL + shortCode;

        return request.createResponse(200, shortUrl);
    }
}
