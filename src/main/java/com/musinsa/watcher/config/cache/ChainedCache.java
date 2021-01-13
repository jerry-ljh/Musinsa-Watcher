package com.musinsa.watcher.config.cache;

import com.musinsa.watcher.config.cache.hystrix.HystrixClearCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixEvictCommand;
import com.musinsa.watcher.config.cache.hystrix.HystrixGetCommand;
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
    ValueWrapper valueWrapper = localCache.get(key);
    log.info("로컬 캐시 : " + valueWrapper);
    if (valueWrapper != null && valueWrapper.get() != null) {
      log.info("로컬 캐시 조회");
      return valueWrapper;
    } else {
      valueWrapper = new HystrixGetCommand(globalCache, key).execute();
      log.info("글로벌 캐시 : " + valueWrapper);
      if (valueWrapper != null) {
        localCache.put(key, valueWrapper.get());
      }
      return valueWrapper;
    }
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
  public String getName() {
    return localCache.getName();
  }

  @Override
  public Object getNativeCache() {
    return localCache.getNativeCache();
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    return localCache.get(key, type);
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    return localCache.get(key, valueLoader);
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
    if (localValue == null || localValue.get() == null) {
      return true;
    } else if (globalValue == null || globalValue.get() == null) {
      return false;
    } else {
      return localValue.get().equals(globalValue.get());
    }
  }
}
