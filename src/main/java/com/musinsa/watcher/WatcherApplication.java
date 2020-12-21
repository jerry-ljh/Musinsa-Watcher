package com.musinsa.watcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class WatcherApplication {

  public static void main(String[] args) {
    SpringApplication.run(WatcherApplication.class, args);
  }

}
