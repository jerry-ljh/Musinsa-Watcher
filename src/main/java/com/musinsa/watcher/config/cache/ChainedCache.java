package com.musinsa.watcher.config.cache;

import com.musinsa.watcher.config.cache.hystrix.HystrixClearCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixEvictCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixGetCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixGetNameCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixPutCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixPutIfAbsentCommand;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

@Slf4j
public class ChainedCache implements Cache {

  private final Cache localCache;
  private final Cache globalCache;

  public ChainedCache(List<Cache> caches) {
    this.localCache = caches.get(0);
    this.globalCache = caches.get(1);
  }

  @Override
  public ValueWrapper get(Object key) {
    ValueWrapper cacheValue = localCache.get(key);
    if (!isEmpty(cacheValue)) {
      return cacheValue;
    }
    cacheValue = new HystrixGetCommand(globalCache, key).execute();
    if (!isEmpty(cacheValue)) {
      localCache.put(key, cacheValue.get());
    }
    return cacheValue;
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    return new HystrixPutIfAbsentCommand(localCache, globalCache, key, value).execute();
  }

  @Override
  public boolean evictIfPresent(Object key) {
    return localCache.evictIfPresent(key);
  }

  @Override
  public boolean invalidate() {
    return localCache.invalidate();
  }

  @Override
  public void put(Object key, Object value) {
    new HystrixPutCommand(localCache, globalCache, key, value).execute();
  }

  @Override
  public void evict(Object key) {
    new HystrixEvictCommand(localCache, globalCache, key);
  }

  @Override
  public void clear() {
    new HystrixClearCommand(localCache, globalCache).execute();
  }

  @Override
  public String getName() {
    if (!localCache.getName().isEmpty()) {
      return localCache.getName();
    }
    return new HystrixGetNameCommand(globalCache).execute();
  }

  @Override
  public Object getNativeCache() {
    throw new RuntimeException("지원하지 않는 연산입니다.");
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    throw new RuntimeException("지원하지 않는 연산입니다.");
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    throw new RuntimeException("지원하지 않는 연산입니다.");
  }

  public void clearLocalCache() {
    localCache.clear();
  }

  public boolean isSynchronized(Object key) {
    ValueWrapper localValue = localCache.get(key);
    ValueWrapper globalValue = new HystrixGetCommand(globalCache, key) {
      @Override
      protected ValueWrapper getFallback() {
        log.warn("Synchronize get fallback called, circuit is {}", super.circuitBreaker.isOpen());
        return localValue;
      }
    }.execute();
    if (isEmpty(localValue)) {
      return true;
    } else if (isEmpty(globalValue)) {
      return false;
    }
    return localValue.get().equals(globalValue.get());
  }

  private boolean isEmpty(ValueWrapper valueWrapper) {
    return valueWrapper == null;
  }
}
