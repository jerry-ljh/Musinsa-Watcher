package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

@Slf4j
public class HystrixEvictCommand extends HystrixCommand {

  private final Cache globalCache;
  private final Cache localCache;
  private final Object key;

  public HystrixEvictCommand(Cache localCache, Cache globalCache, Object key) {
    super(HystrixKey.getKey("evict"));
    this.globalCache = globalCache;
    this.localCache = localCache;
    this.key = key;
  }

  @Override
  protected Object run() {
    localCache.evict(key);
    globalCache.evict(key);
    return null;
  }

  @Override
  protected ValueWrapper getFallback() {
    log.warn("cache evict fallback called, circuit is {}", super.circuitBreaker.isOpen());
    localCache.evict(key);
    return null;
  }
}
