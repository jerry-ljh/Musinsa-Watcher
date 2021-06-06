package com.musinsa.watcher.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CacheManagementServiceTest {

  @Mock
  private CacheManager cacheManager;
  @Mock
  private ProductService productService;

  private CacheManagementService cacheManagementService;

  @Before
  public void setUp() {
    cacheManagementService = new CacheManagementService(cacheManager, productService);
  }

  @Test
  @DisplayName("최근 데이터 업데이트 날짜가 갱신되었다면 캐시를 초기화한다.")
  public void clearCache() {
    Cache cache = getLastUpdateDateCache();
    setOldCache();

    cacheManagementService.clearCacheIfOld();

    verify(cache, times(1)).clear();
  }

  @Test
  @DisplayName("데이터 업데이트되지 않았다면 캐시를 초기화하지 않는다.")
  public void doNotClearCache() {
    Cache cache = getLastUpdateDateCache();
    setAlreadyUpdatedCache();

    cacheManagementService.clearCacheIfOld();

    verify(cache, never()).clear();
  }

  private Cache getLastUpdateDateCache() {
    Cache cache = mock(Cache.class);
    when(cacheManager.getCache(anyString())).thenReturn(cache);
    when(cache.get(any())).thenReturn(mock(ValueWrapper.class));
    return cache;
  }

  private void setAlreadyUpdatedCache() {
    LocalDateTime cachedDateTime = LocalDateTime.now();
    LocalDateTime findDateTime = cachedDateTime;
    when(productService.getLastUpdatedDateTime()).thenReturn(findDateTime);
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(cachedDateTime);
  }

  private void setOldCache() {
    LocalDateTime findDateTime = LocalDateTime.now();
    LocalDateTime cachedDateTime = findDateTime.minusDays(1);
    when(productService.getLastUpdatedDateTime()).thenReturn(findDateTime);
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(cachedDateTime);
  }
}