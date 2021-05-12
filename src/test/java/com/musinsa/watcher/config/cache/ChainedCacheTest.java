package com.musinsa.watcher.config.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
public class ChainedCacheTest {

  @Mock
  private Cache localCache;

  @Mock
  private Cache globalCache;

  private ChainedCache getCache() {
    List<Cache> caches = new ArrayList<>();
    caches.add(localCache);
    caches.add(globalCache);
    return new ChainedCache(caches);
  }

  @Test
  @DisplayName("로컬 캐시가 없다면 글로벌 캐시를 조회한다")
  public void useGlobalCache() {
    //given
    ChainedCache cache = getCache();
    ValueWrapper cacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(null);
    when(globalCache.get(any())).thenReturn(cacheValue);

    //when
    ValueWrapper result = cache.get(anyString());

    //then
    assertEquals(result, cacheValue);
  }

  @Test
  @DisplayName("로컬, 글로벌 캐시가 모두 없다면 null을 반환한다.")
  public void nullCache() {
    //given
    ChainedCache cache = getCache();
    when(localCache.get(any())).thenReturn(null);
    when(globalCache.get(any())).thenReturn(null);

    //when
    ValueWrapper result = cache.get(anyString());

    //then
    assertNull(result);
  }

  @Test
  @DisplayName("로컬 캐시가 있다면 로컬 캐시를 우선 사용한다.")
  public void useLocalCache() {
    //given
    ChainedCache cache = getCache();
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(localCacheValue);

    //when
    ValueWrapper result = cache.get(anyString());

    //then
    assertEquals(result, localCacheValue);
  }

  @Test
  @DisplayName("글로벌 캐시에 장애가 발생하면 fallback메서드를 사용한다.")
  public void useFallback() {
    //given
    ChainedCache cache = getCache();
    when(localCache.get(any())).thenReturn(null);
    when(globalCache.get(any())).thenThrow(new RuntimeException());

    //when
    ValueWrapper result = cache.get(anyString());

    //then
    assertEquals(result, null);
  }

  @Test
  @DisplayName("글로벌 캐시에서 조회한 값은 로컬 캐시에 저장된다.")
  public void putCache() {
    //given
    ChainedCache cache = getCache();
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(null);
    when(globalCache.get(any())).thenReturn(valueWrapper);

    //when
    ValueWrapper result = cache.get(anyString());

    //then
    verify(localCache, times(1)).put(any(), any());
  }

  @Test
  @DisplayName("캐시를 초기화하면 로컬, 글로벌 캐시 둘 다 초기화 된다.")
  public void clearCache() {
    //given
    ChainedCache cache = getCache();

    //when
    cache.clear();

    //then
    verify(localCache, times(1)).clear();
    verify(globalCache, times(1)).clear();
  }

  @Test
  @DisplayName("글로벌 캐시에 값이 없으면 로컬 캐시를 초기화하지 않는다.")
  public void clearLocalCache2() {
    ///given
    ChainedCache cache = getCache();
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(localCacheValue);
    when(globalCache.get(any())).thenReturn(null);

    //when
    cache.clearLocalCache();

    //then
    verify(localCache, never()).put(any(), any());
  }

  @Test
  @DisplayName("로컬 캐시에 값이 없으면 동기화되지 않는다.")
  public void cacheSynchronized1() {
    ///given
    ChainedCache cache = getCache();
    when(localCache.get(any())).thenReturn(null);

    //when
    boolean result = cache.isSynchronized(anyString());

    //then
    assertTrue(result);
  }

  @Test
  @DisplayName("로컬 캐시가 있고 글로벌 캐시가 없으면 동기화된다.")
  public void cacheSynchronized2() {
    ///given
    ChainedCache cache = getCache();
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(localCacheValue);
    when(globalCache.get(any())).thenReturn(null);

    //when
    boolean result = cache.isSynchronized(any());
    //then
    assertFalse(result);
  }

  @Test
  @DisplayName("로컬 캐시와 글로벌 캐시가 같으면 동기화되지 않는다.")
  public void cacheSynchronized3() {
    ///given
    ChainedCache cache = getCache();
    ValueWrapper cacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(cacheValue);
    when(globalCache.get(any())).thenReturn(cacheValue);
    when(cacheValue.get()).thenReturn(LocalDateTime.now());

    //when
    boolean result = cache.isSynchronized(anyString());
    //then
    assertTrue(result);
  }

  @Test
  @DisplayName("로컬 캐시와 글로벌 캐시의 값이 다르면 동기화된다.")
  public void cacheSynchronized4() {
    ///given
    ChainedCache cache = getCache();
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    ValueWrapper globalCacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(localCacheValue);
    when(globalCache.get(any())).thenReturn(globalCacheValue);
    when(localCacheValue.get()).thenReturn(LocalDateTime.now());
    when(globalCacheValue.get()).thenReturn(LocalDateTime.now().minusDays(1));

    //when
    boolean result = cache.isSynchronized(anyString());
    //then
    assertFalse(result);
  }

  @Test
  @DisplayName("글로벌 캐시에 장애가 발생하면 동기화하지 않는다.")
  public void cacheSynchronized5() {
    ///given
    ChainedCache cache = getCache();
    ValueWrapper localCacheValue = mock(ValueWrapper.class);
    when(localCache.get(any())).thenReturn(localCacheValue);
    when(globalCache.get(any())).thenThrow(new RuntimeException());
    when(localCacheValue.get()).thenReturn(LocalDateTime.now());

    //when
    boolean result = cache.isSynchronized(anyString());

    //then
    assertTrue(result);
  }

}