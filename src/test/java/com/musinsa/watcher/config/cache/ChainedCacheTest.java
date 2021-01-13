package com.musinsa.watcher.config.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

  @Test
  @DisplayName("local cache가 없다면 global cache 사용한다")
  public void useGlobalCache() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(null);
    when(globalCache.get(eq(key))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(value);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, valueWrapper);
  }

  @Test
  @DisplayName("local cache에 value가 없다면 global cache를 사용한다")
  public void useGlobalCache2() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper lovalValueWrapper = mock(ValueWrapper.class);
    ValueWrapper globalValueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(lovalValueWrapper);
    when(globalCache.get(eq(key))).thenReturn(globalValueWrapper);
    when(globalValueWrapper.get()).thenReturn(value);
    when(lovalValueWrapper.get()).thenReturn(null);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, globalValueWrapper);
  }

  @Test
  @DisplayName("둘 다 cache가 없다면 null을 반환한다.")
  public void nullCache() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(null);
    when(globalCache.get(eq(key))).thenReturn(null);
    when(valueWrapper.get()).thenReturn(value);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, null);
  }

  @Test
  @DisplayName("local cache가 있다면 local cache를 사용한다")
  public void useLocalCache() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(value);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, valueWrapper);
  }

  @Test
  @DisplayName("global cache에 오류가 있다면 fallback이 발동된다.")
  public void useFallback() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(null);
    when(globalCache.get(eq(key))).thenThrow(new RuntimeException());
    when(valueWrapper.get()).thenReturn(value);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, null);
  }

  @Test
  @DisplayName("global cache가 존재하면 local cache에 저장한다.")
  public void putCache() {
    //given
    String key = "key1";
    String value = "value1";
    ValueWrapper valueWrapper = mock(ValueWrapper.class);
    List<Cache> caches = mock(List.class);
    when(caches.get(eq(0))).thenReturn(localCache);
    when(caches.get(eq(1))).thenReturn(globalCache);
    ChainedCache cache = new ChainedCache(caches);
    when(localCache.get(eq(key))).thenReturn(null);
    when(globalCache.get(eq(key))).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(value);

    //when
    ValueWrapper result = cache.get(key);

    //then
    assertEquals(result, valueWrapper);
    verify(localCache, times(1)).put(eq(key), eq(value));
  }


}