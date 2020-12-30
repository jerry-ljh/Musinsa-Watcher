package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.product.ProductQueryRepository;
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

  public static final String DATE_KEY = "current date";
  private final CacheManager cacheManager;
  private final ProductQueryRepository productRepository;

  public LocalDate updateCacheByDate() {
    LocalDateTime localDateTime = productRepository.findLastUpdateDate();
    cacheManager.getCache("productCache").putIfAbsent(DATE_KEY, localDateTime);
    LocalDateTime cachedLocalDateTime = (LocalDateTime) cacheManager.getCache("productCache")
        .get(DATE_KEY).get();
    if (localDateTime.isAfter(cachedLocalDateTime)) {
      log.info(localDateTime.toString());
      log.info(cachedLocalDateTime.toString());
      log.info("cache update!");
      cacheManager.getCache("productCache").clear();
      cacheManager.getCache("productCache").put(DATE_KEY, localDateTime);
    }
    return cachedLocalDateTime.toLocalDate();
  }

  public LocalDate getLastUpdatedDate() {
    if (cacheManager.getCache("productCache").get(DATE_KEY) != null) {
      LocalDateTime cachedLocalDateTime = (LocalDateTime) cacheManager.getCache("productCache")
          .get(DATE_KEY).get();
      return cachedLocalDateTime.toLocalDate();
    }
    LocalDateTime localDateTime = productRepository.findLastUpdateDate();
    cacheManager.getCache("productCache").putIfAbsent(DATE_KEY, localDateTime);
    return localDateTime.toLocalDate();
  }
}
