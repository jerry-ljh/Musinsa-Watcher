package com.musinsa.watcher.web;

import com.musinsa.watcher.service.CacheManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CacheManagementController {

  private final CacheManagementService cacheManagementService;

  @DeleteMapping("/api/v1/product/cache")
  public void findLastProductUpdatedDate() {
    cacheManagementService.clearCacheIfOld();
  }
}
