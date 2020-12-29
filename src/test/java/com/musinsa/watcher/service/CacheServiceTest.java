package com.musinsa.watcher.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.product.ProductQueryRepository;
import com.musinsa.watcher.domain.product.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CacheServiceTest {

  @Mock
  CacheManager cacheManager;
  @Mock
  ProductQueryRepository productRepository;

  public final String DATE_KEY = "current date";

  @Test
  @DisplayName("데이터가 업데이트되면 cache가 지워진다")
  public void clear_cache() {
    //given
    CacheService cacheService = new CacheService(cacheManager, productRepository);
    LocalDateTime yesterday = LocalDateTime.now().toLocalDate().atStartOfDay();
    LocalDateTime today = LocalDateTime.now();
    Cache cache = mock(Cache.class);
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    when(productRepository.findLastUpdateDate()).thenReturn(today);
    when(cacheManager.getCache(eq("productCache"))).thenReturn(cache);
    when(cache.putIfAbsent(eq(DATE_KEY), eq(today))).thenReturn(valueWrapper);
    when(cache.get(eq(DATE_KEY))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(yesterday);
    doNothing().when(cache).clear();
    doNothing().when(cache).put(eq(DATE_KEY), eq(today));
    //when
    cacheService.updateCacheByDate();
    //then
    verify(cache, times(1)).clear();
    verify(cache, times(1)).put(eq(DATE_KEY), eq(today));
  }

  @Test
  @DisplayName("데이터가 업데이트되지 않으면 cache를 유지한다")
  public void donot_clear_cache() {
    //given
    CacheService cacheService = new CacheService(cacheManager, productRepository);
    LocalDateTime today = LocalDateTime.now();
    Cache cache = mock(Cache.class);
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    when(productRepository.findLastUpdateDate()).thenReturn(today);
    when(cacheManager.getCache(eq("productCache"))).thenReturn(cache);
    when(cache.putIfAbsent(eq(DATE_KEY), eq(today))).thenReturn(valueWrapper);
    when(cache.get(eq(DATE_KEY))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(today);
    doNothing().when(cache).clear();
    doNothing().when(cache).put(eq(DATE_KEY), eq(today));
    //when
    cacheService.updateCacheByDate();
    //then
    verify(cache, never()).clear();
    verify(cache, never()).put(eq(DATE_KEY), eq(today));
  }

  @Test
  @DisplayName("캐시된 업데이트 날짜 데이터를 반환한다")
  public void returnCacheDate() {
    //given
    CacheService cacheService = new CacheService(cacheManager, productRepository);
    LocalDateTime now = LocalDateTime.now();
    Cache cache = mock(Cache.class);
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    when(cacheManager.getCache(eq("productCache"))).thenReturn(cache);
    when(cache.get(eq(DATE_KEY))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(now);
    //when
    LocalDate result = cacheService.getLastUpdatedByCache();
    //then
    assertEquals(result, now.toLocalDate());
  }

  @Test
  @DisplayName("캐시된 데이터가 없으면 업데이트 날짜를 캐시한다.")
  public void saveCacheDate() {
    //given
    CacheService cacheService = new CacheService(cacheManager, productRepository);
    LocalDateTime now = LocalDateTime.now();
    Cache cache = mock(Cache.class);
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    when(cacheManager.getCache(eq("productCache"))).thenReturn(cache);
    when(cache.get(eq(DATE_KEY))).thenReturn(null);
    when(productRepository.findLastUpdateDate()).thenReturn(now);
    when(cache.putIfAbsent(eq(DATE_KEY), eq(now))).thenReturn(valueWrapper);
    //when
    LocalDate result = cacheService.getLastUpdatedByCache();
    //then
    verify(cache, times(1)).putIfAbsent(eq(DATE_KEY), eq(now));
  }
}