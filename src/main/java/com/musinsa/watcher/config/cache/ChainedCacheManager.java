package com.musinsa.watcher.config.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;

public class ChainedCacheManager implements CacheManager {

  private final List<CacheManager> cacheManagers;
  private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

  public ChainedCacheManager(@NonNull CacheManager... cacheManagers) {
    if (cacheManagers.length < 1) {
      throw new IllegalArgumentException();
    }
    this.cacheManagers = List.of(cacheManagers);
  }

  @Override
  public Cache getCache(String name) {
    return cacheMap.computeIfAbsent(name, key -> new ChainedCache(getCaches(key)));
  }

  private List<Cache> getCaches(String name) {
    return cacheManagers.stream().map(manager -> manager.getCache(name))
        .collect(Collectors.toList());
  }

  @Override
  public Collection<String> getCacheNames() {
    return cacheManagers.stream()
        .flatMap(manager -> manager.getCacheNames().stream())
        .collect(Collectors.toSet());
  }
}
