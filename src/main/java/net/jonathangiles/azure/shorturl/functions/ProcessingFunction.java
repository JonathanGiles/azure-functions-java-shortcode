package net.jonathangiles.azure.shorturl.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import net.jonathangiles.azure.shorturl.util.SlackUtil;
import net.jonathangiles.azure.shorturl.util.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProcessingFunction {

    @FunctionName("processing")
    public void processUrlClick(
            @QueueTrigger(name = Util.PROCESSING_QUEUE_NAME, queueName = Util.PROCESSING_QUEUE_QUEUE_NAME, connection = "AzureWebJobsStorage") String request,
            final ExecutionContext context) {

        Analytics analytics = Analytics.parse(request);
        context.getLogger().info("Received on queue: " + analytics);

        Util.TELEMETRY_CLIENT.trackPageView(analytics.longUrl);

        // TODO additional analysis

        SlackUtil.sendMessage("Shortlink visited: " + analytics, SlackUtil.CHANNEL_GENERAL);
    }

    private static class Analytics {
        private String shortCode;
        private String longUrl;
        private String host;
        private LocalDateTime dateTime;
        private String referrer;
        private String userAgent;

        private Analytics() {
            // no-op
        }

        private Analytics(String... data) {
            if (data == null || data.length != 6) {
                throw new IllegalArgumentException("Bad analytics data");
            }
            shortCode = data[0];
            longUrl = data[1];
            host = data[2];
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(data[3])), ZoneId.systemDefault());
            referrer = data[4];
            userAgent = data[5];
        }

        public static Analytics parse(String record) {
            return new Analytics(record.split("\\|"));
        }

        @Override
        public String toString() {
            return "Analytics{" +
                    "shortCode='" + shortCode + '\'' +
                    ", longUrl='" + longUrl + '\'' +
                    ", host='" + host + '\'' +
                    ", dateTime=" + dateTime +
                    ", referrer='" + referrer + '\'' +
                    ", userAgent='" + userAgent + '\'' +
                    '}';
        }
    }
}
