package com.musinsa.watcher.job;

import static org.junit.Assert.*;

import com.musinsa.watcher.config.cache.CacheName;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LocalAndGlobalCacheSyncJobTest {

  @Autowired
  private RedisCacheManager globalCacheManager;

  @Autowired
  private EhCacheCacheManager localCacheManager;

  @Autowired
  private LocalAndGlobalCacheSyncJob localAndGlobalCacheSyncJob;

  private final String cacheName = CacheName.PRODUCT_CACHE.getName();
  private final String cacheKey = CacheName.LAST_UPDATE_DATE_KEY.getName();

  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 같으면 동기화 하지 않는다.")
  public void cacheSynchronized1() {
    LocalDateTime localDateTime = LocalDateTime.now();
    globalCacheManager.getCache(cacheName).put(cacheKey, localDateTime);
    localCacheManager.getCache(cacheName).put(cacheKey, localDateTime);

    localAndGlobalCacheSyncJob.doSynchronize();

    assertEquals(globalCacheManager.getCache(cacheName).get(cacheKey).get(),
        localCacheManager.getCache(cacheName).get(cacheKey).get());
  }

  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 달라 동기화 한다.")
  public void cacheSynchronized2() {
    LocalDateTime localDateTime = LocalDateTime.now();
    globalCacheManager.getCache(cacheName).put(cacheKey, localDateTime.plusDays(1));
    localCacheManager.getCache(cacheName).put(cacheKey, localDateTime);

    localAndGlobalCacheSyncJob.doSynchronize();

    assertNull(localCacheManager.getCache(cacheName).get(cacheKey));
  }
}