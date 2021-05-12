package com.musinsa.watcher.job;

import static org.junit.Assert.*;

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


  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 같으면 동기화 하지 않는다.")
  public void cacheSynchronized1() {
    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    globalCacheManager.getCache("productCache").put("current date", localDateTime);
    localCacheManager.getCache("productCache").put("current date", localDateTime);
    //when
    localAndGlobalCacheSyncJob.doSynchronize();
    //then
    assertEquals(globalCacheManager.getCache("productCache").get("current date").get(),
        localCacheManager.getCache("productCache").get("current date").get());
  }

  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 달라 동기화 한다.")
  public void cacheSynchronized2() {
    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    globalCacheManager.getCache("productCache").put("current date", localDateTime.plusDays(1));
    localCacheManager.getCache("productCache").put("current date", localDateTime);
    //when
    localAndGlobalCacheSyncJob.doSynchronize();
    //then
    assertNull(localCacheManager.getCache("productCache").get("current date"));
  }
}