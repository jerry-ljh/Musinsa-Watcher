package com.musinsa.watcher.config.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ChainedCacheTest {

  @Mock
  private Cache localCache;

  @Mock
  private Cache globalCache;

  private ChainedCache chainedCache;

  @Before
  public void setUp() {
    List<Cache> caches = new ArrayList<>();
    caches.add(localCache);
    caches.add(globalCache);
    this.chainedCache = new ChainedCache(caches);
  }

  @Test
  @DisplayName("로컬 캐시가 없다면 글로벌 캐시를 조회한다")
  public void useGlobalCache() {
    ValueWrapper globalCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(null);
    setGlobalCacheValue(globalCacheValue);

    ValueWrapper result = chainedCache.get(anyString());

    assertEquals(result, globalCacheValue);
  }

  @Test
  @DisplayName("로컬, 글로벌 캐시가 모두 없다면 null을 반환한다.")
  public void nullCache() {
    setLocalCacheValue(null);
    setGlobalCacheValue(null);

    ValueWrapper result = chainedCache.get(anyString());

    assertNull(result);
  }

  @Test
  @DisplayName("로컬 캐시가 있다면 로컬 캐시를 우선 사용한다.")
  public void useLocalCache() {
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(localCacheValue);
    setGlobalCacheValue(null);

    ValueWrapper result = chainedCache.get(anyString());

    assertEquals(result, localCacheValue);
  }

  @Test
  @DisplayName("글로벌 캐시에 장애가 발생하면 fallback메서드를 사용한다.")
  public void useFallback() {
    setLocalCacheValue(null);
    setThrowExceptionGlobalCache();

    ValueWrapper result = chainedCache.get(anyString());

    assertNull(result);
  }

  @Test
  @DisplayName("글로벌 캐시에서 조회한 값은 로컬 캐시에 저장된다.")
  public void putCache() {
    ValueWrapper globalCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(null);
    setGlobalCacheValue(globalCacheValue);

    ValueWrapper result = chainedCache.get(anyString());

    assertEquals(result, globalCacheValue);
    verify(localCache, times(1)).put(any(), any());
  }

  @Test
  @DisplayName("캐시를 초기화하면 로컬, 글로벌 캐시 둘 다 초기화 된다.")
  public void clearCache() {
    chainedCache.clear();

    verify(localCache, times(1)).clear();
    verify(globalCache, times(1)).clear();
  }

  @Test
  @DisplayName("글로벌 캐시에 값이 없으면 로컬 캐시를 초기화하지 않는다.")
  public void clearLocalCache2() {
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(localCacheValue);
    setGlobalCacheValue(null);

    chainedCache.clearLocalCache();

    verify(localCache, never()).put(any(), any());
  }

  @Test
  @DisplayName("로컬 캐시에 값이 없으면 동기화되지 않는다.")
  public void cacheSynchronized1() {
    setLocalCacheValue(null);

    boolean result = chainedCache.isSynchronized(anyString());

    assertTrue(result);
  }

  @Test
  @DisplayName("로컬 캐시가 있고 글로벌 캐시가 없으면 동기화된다.")
  public void cacheSynchronized2() {
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(localCacheValue);
    setGlobalCacheValue(null);

    boolean result = chainedCache.isSynchronized(any());

    assertFalse(result);
  }

  @Test
  @DisplayName("로컬 캐시와 글로벌 캐시가 같으면 동기화되지 않는다.")
  public void cacheSynchronized3() {
    ValueWrapper cacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(cacheValue);
    setGlobalCacheValue(cacheValue);
    when(cacheValue.get()).thenReturn(LocalDateTime.now());

    boolean result = chainedCache.isSynchronized(anyString());

    assertTrue(result);
  }

  @Test
  @DisplayName("로컬 캐시와 글로벌 캐시의 값이 다르면 동기화된다.")
  public void cacheSynchronized4() {
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    ValueWrapper globalCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(localCacheValue);
    setGlobalCacheValue(globalCacheValue);
    when(localCacheValue.get()).thenReturn(LocalDateTime.now());
    when(globalCacheValue.get()).thenReturn(LocalDateTime.now().minusDays(1));

    boolean result = chainedCache.isSynchronized(anyString());

    assertFalse(result);
  }

  @Test
  @DisplayName("글로벌 캐시에 장애가 발생하면 동기화하지 않는다.")
  public void cacheSynchronized5() {
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    setLocalCacheValue(localCacheValue);
    when(localCacheValue.get()).thenReturn(LocalDateTime.now());
    setThrowExceptionGlobalCache();

    boolean result = chainedCache.isSynchronized(anyString());

    assertTrue(result);
  }

  private void setLocalCacheValue(ValueWrapper valueWrapper) {
    if (valueWrapper == null) {
      when(localCache.get(any())).thenReturn(null);
      return;
    }
    when(localCache.get(any())).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(LocalDateTime.now());
  }

  private void setGlobalCacheValue(ValueWrapper valueWrapper) {
    if (valueWrapper == null) {
      when(globalCache.get(any())).thenReturn(null);
      return;
    }
    when(globalCache.get(any())).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(LocalDateTime.now());
  }

  private void setThrowExceptionGlobalCache() {
    when(globalCache.get(any())).thenThrow(new RuntimeException());
  }
}