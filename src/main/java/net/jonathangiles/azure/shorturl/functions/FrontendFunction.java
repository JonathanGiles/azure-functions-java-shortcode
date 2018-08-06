package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

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
    public HttpResponseMessage frontend(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Admin url requested");

        // Current path: D:\home\site\wwwroot\.
        // need to add www\shortener.html
//        context.getLogger().info("Current path: " + new File(".").getAbsolutePath());

        Path path = Paths.get("www/shortener.html");

        if (!Files.exists(path)) {
            context.getLogger().info("Cannot find frontend files at path " + path);
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Cannot find frontend files at path " + path).build();
        }

        try {
            String html = new String(Files.readAllBytes(path));

            context.getLogger().info("Html follows: \n" + html);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "text/html")
                    .body(html)
                    .build();
        } catch (IOException e) {
            context.getLogger().info("Cannot read html from file at path " + path);
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Cannot read html from file at path " + path)
                    .build();
        }
    }
}
