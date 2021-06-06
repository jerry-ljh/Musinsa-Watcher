package com.musinsa.watcher.service;

import com.musinsa.watcher.config.cache.CacheName;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CacheManagementService {

  private final CacheManager cacheManager;
  private final ProductService productService;

  public void clearCacheIfOld() {
    LocalDateTime lastUpdatedDate = productService.getLastUpdatedDateTime();
    LocalDateTime cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime();
    if (lastUpdatedDate.isAfter(cachedLastUpdatedDate)) {
      Cache cache = cacheManager.getCache(CacheName.PRODUCT_CACHE.getName());
      cache.clear();
    }
  }
}
