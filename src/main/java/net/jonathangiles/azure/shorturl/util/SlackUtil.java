package net.jonathangiles.azure.shorturl.util;

import feign.Feign;
import feign.jackson.JacksonEncoder;

public class SlackUtil {

    public static final String CHANNEL_GENERAL = "#general";

    private static final String webhookUrl = System.getenv("slack-webhook-url");

    private static final Slack slackClient;
    static {
        slackClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .target(Slack.class, "https://hooks.slack.com");
    }

    public static void sendMessage(String text, String channel) {
        slackClient.sendMessage(webhookUrl, text, channel);
    }
}