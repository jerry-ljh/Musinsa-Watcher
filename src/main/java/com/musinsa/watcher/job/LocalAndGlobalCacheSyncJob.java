package com.musinsa.watcher.job;

import com.musinsa.watcher.config.cache.CacheName;
import com.musinsa.watcher.config.cache.ChainedCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LocalAndGlobalCacheSyncJob {

  private final CacheManager cacheManager;

  @Scheduled(fixedRate = 5000)
  public void doSynchronize() {
    ChainedCache cache = (ChainedCache) cacheManager.getCache(CacheName.PRODUCT_CACHE.getName());
    if (cache == null || cache.isSynchronized(CacheName.LAST_UPDATE_DATE_KEY.getName())) {
      return;
    }
    cache.clearLocalCache();
    log.info("clear local cache for sync");
  }

}
