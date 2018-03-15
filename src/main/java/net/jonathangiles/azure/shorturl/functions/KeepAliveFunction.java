package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.TimerTrigger;

/**
 * A trigger function to keep the functions warm...
 */
public class KeepAliveFunction {
    @FunctionName("keepAlive")
    public void keepAlive(@TimerTrigger(name = "keepAliveTrigger", schedule = "0 */4 * * * *") String timerInfo,
                          final ExecutionContext context) {
        context.getLogger().info("keep-alive triggered");
    }
}
