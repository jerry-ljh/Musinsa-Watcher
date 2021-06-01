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
    Cache cache = cacheManager.getCache(CacheName.PRODUCT_CACHE.getName());
    if (cache == null || cache.get(CacheName.LAST_UPDATE_DATE_KEY.getName()) == null) {
      return;
    }
    LocalDateTime cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime();
    LocalDateTime lastUpdatedDate = productService.getLastUpdatedDateTime();
    if (lastUpdatedDate.isAfter(cachedLastUpdatedDate)) {
      cache.clear();
    }
  }
}
