package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.HttpRequestMessage;
import com.microsoft.azure.serverless.functions.HttpResponseMessage;
import com.microsoft.azure.serverless.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.HttpTrigger;
import net.jonathangiles.azure.shorturl.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This class will, in time, support returning an HTML page that allows for short codes to be registered. For now it
 * does not work.
 */
public class FrontendFunction {

    @FunctionName("frontend")
    public HttpResponseMessage<String> frontend(
            @HttpTrigger(name = "req", methods = {"get"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Admin url requested");

        // Current path: D:\home\site\wwwroot\.
        // need to add www\shortener.html
//        context.getLogger().info("Current path: " + new File(".").getAbsolutePath());

        Path path = Paths.get("www/shortener.html");

        if (!Files.exists(path)) {
            context.getLogger().info("Cannot find frontend files at path " + path);
            return request.createResponse(Util.HTTP_STATUS_NOT_FOUND, "Cannot find frontend files at path " + path);
        }

        try {
            String html = new String(Files.readAllBytes(path));

            context.getLogger().info("Html follows: \n" + html);

            HttpResponseMessage<String> response = request.createResponse(200, html);
            response.addHeader("Content-Type", "text/html");
            return response;
        } catch (IOException e) {
            context.getLogger().info("Cannot read html from file at path " + path);
            return request.createResponse(Util.HTTP_STATUS_NOT_FOUND, "Cannot read html from file at path " + path);
        }
    }
}
