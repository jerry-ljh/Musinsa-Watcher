package com.musinsa.watcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCircuitBreaker
@SpringBootApplication
public class WatcherApplication {

  public static void main(String[] args) {
    SpringApplication.run(WatcherApplication.class, args);
  }

}
