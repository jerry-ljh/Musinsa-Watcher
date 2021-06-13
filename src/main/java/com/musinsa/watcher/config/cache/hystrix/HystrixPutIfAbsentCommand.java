package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

@Slf4j
public class HystrixPutIfAbsentCommand extends HystrixCommand<ValueWrapper> {

  private final Cache globalCache;
  private final Cache localCache;
  private final Object key;
  private final Object value;

  public HystrixPutIfAbsentCommand(Cache localCache, Cache globalCache, Object key, Object value) {
    super(HystrixKey.getKey("putIfAbsent"));
    this.globalCache = globalCache;
    this.localCache = localCache;
    this.key = key;
    this.value = value;
  }

  @Override
  protected ValueWrapper run() {
    localCache.putIfAbsent(key, value);
    globalCache.putIfAbsent(key, value);
    return null;
  }

  @Override
  protected ValueWrapper getFallback() {
    log.warn("putIfAbsent fallback called, circuit is {}", super.circuitBreaker.isOpen());
    localCache.putIfAbsent(key, value);
    return null;
  }
}
