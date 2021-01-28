package com.musinsa.watcher.service;

import com.musinsa.watcher.config.cache.ChainedCache;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

  private static final String CACHE_NAME = "productCache";
  private static final String DATE_KEY = "current date";
  private final CacheManager cacheManager;
  private final ProductQuerySlaveRepository productRepository;

  public LocalDate updateCacheByDate() {
    LocalDateTime localDateTime = productRepository.findLastUpdatedDate();
    cacheManager.getCache(CACHE_NAME).putIfAbsent(DATE_KEY, localDateTime);
    LocalDateTime cachedLocalDateTime = (LocalDateTime) cacheManager.getCache("productCache")
        .get(DATE_KEY).get();
    if (localDateTime.isAfter(cachedLocalDateTime)) {
      log.info(localDateTime.toString());
      log.info(cachedLocalDateTime.toString());
      log.info("cache update!");
      cacheManager.getCache(CACHE_NAME).clear();
      cacheManager.getCache(CACHE_NAME).put(DATE_KEY, localDateTime);
    }
    return cachedLocalDateTime.toLocalDate();
  }

  public LocalDate getLastUpdatedDate() {
    if (cacheManager.getCache(CACHE_NAME).get(DATE_KEY) != null) {
      LocalDateTime cachedLocalDateTime = (LocalDateTime) cacheManager.getCache(CACHE_NAME)
          .get(DATE_KEY).get();
      return cachedLocalDateTime.toLocalDate();
    }
    LocalDateTime localDateTime = productRepository.findLastUpdatedDate();
    cacheManager.getCache(CACHE_NAME).putIfAbsent(DATE_KEY, localDateTime);
    return localDateTime.toLocalDate();
  }

  public boolean doSynchronize() {
    ChainedCache cache = (ChainedCache) cacheManager.getCache(CACHE_NAME);
    if (cache.isSynchronized(DATE_KEY)) {
      return false;
    } else {
      cache.clearLocalCache();
      log.info("동기화를 위해 로컬 캐시 초기화가 되었습니다.");
      return true;
    }
  }

}
