package com.musinsa.watcher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final Environment env;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    if (isLocalMode()) {
      registry.addMapping("/**")
          .allowedOrigins("*")
          .allowedMethods("*");
    } else {
      registry.addMapping("/**")
          .allowedOrigins("https://www.musinsa.info", "http://www.musinsa.info")
          .allowedMethods("*");
    }

  }

  private boolean isLocalMode() {
    String profile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";
    return profile.equals("local");
  }
}
