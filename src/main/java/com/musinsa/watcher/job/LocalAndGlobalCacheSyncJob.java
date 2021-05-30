package com.musinsa.watcher.job;

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
  private static final String CACHE_NAME = "productCache";
  private static final String DATE_KEY = "current date";

  @Scheduled(fixedRate = 5000)
  public void doSynchronize() {
    ChainedCache cache = (ChainedCache) cacheManager.getCache(CACHE_NAME);
    if (cache == null || cache.isSynchronized(DATE_KEY)) {
      return;
    }
    cache.clearLocalCache();
    log.debug("clear local cache for sync");
  }

}
