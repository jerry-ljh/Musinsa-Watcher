package com.musinsa.watcher.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChainedCacheMangerTest {

  @Autowired
  private CacheManager cacheManager;

  @Test
  @DisplayName("custom cachemanger가 사용된다.")
  public void getCustomCacheManger() {
    assertEquals("com.musinsa.watcher.config.cache.ChainedCacheManager",
        cacheManager.getClass().getName());
  }

}