package com.musinsa.watcher.domain.notify;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public enum SlackTarget {

  CH_BOT("https://hooks.slack.com/services/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");

  private final String webHookUrl;

  SlackTarget(String webHookUrl) {
    this.webHookUrl = webHookUrl;
  }

  public static void send(String message) {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> request = new HashMap<>();
    request.put("username", "알림봇");
    request.put("text", message);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request);
    restTemplate.exchange(CH_BOT.webHookUrl, HttpMethod.POST, entity, String.class);
  }
}
