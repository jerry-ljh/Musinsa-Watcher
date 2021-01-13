package com.musinsa.watcher.config;

import static org.junit.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChainedCacheMangerTest {

  @Autowired
  private CacheManager cacheManager;

  @Test
  @DisplayName("지정한 2차 cache manager를 사용한다")
  public void getGlobalCache() {
    assertEquals("com.musinsa.watcher.config.cache.ChainedCacheManager",
        cacheManager.getClass().getName());
  }

}