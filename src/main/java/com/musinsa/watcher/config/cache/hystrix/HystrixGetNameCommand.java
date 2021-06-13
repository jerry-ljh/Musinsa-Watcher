package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

@Slf4j
public class HystrixGetNameCommand extends HystrixCommand<String> {

  private final Cache globalCache;

  public HystrixGetNameCommand(Cache globalCache) {
    super(HystrixKey.getKey("getName"));
    this.globalCache = globalCache;
  }

  @Override
  protected String run() {
    return globalCache.getName();
  }

  @Override
  protected String getFallback() {
    log.warn("getName fallback called, circuit is {}", super.circuitBreaker.isOpen());
    return null;
  }
}
