package net.jonathangiles.azure.shorturl.util;

import feign.Param;
import feign.RequestLine;

public interface Slack {

    @RequestLine("POST /services/{webhookUrl}")
    void sendMessage(@Param("webhookUrl") String webhookUrl, @Param("text") String text, @Param("channel") String channel);
}