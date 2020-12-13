package com.musinsa.watcher.web;

import com.musinsa.watcher.service.CacheService;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CacheController {

  private final CacheService cacheService;

  @GetMapping("/api/v1/product/last-modified")
  public String updateCacheByDate() {
    return cacheService.updateCacheByDate().toString();
  }
}
