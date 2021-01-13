package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.annotation.Bean;

@Slf4j
public class HystrixGetCommand extends HystrixCommand<ValueWrapper> {
  private final Cache globalCache;
  private final Object key;

  public HystrixGetCommand(Cache globalCache, Object key) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("cacheGroupKey"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("cache-get"))
        .andCommandPropertiesDefaults(
            HystrixCommandProperties.defaultSetter()
                .withExecutionTimeoutInMilliseconds(1000)
                .withCircuitBreakerErrorThresholdPercentage(50)
                .withCircuitBreakerRequestVolumeThreshold(10)
                .withCircuitBreakerSleepWindowInMilliseconds(30000)
                .withMetricsRollingStatisticalWindowInMilliseconds(10000)));
    this.globalCache = globalCache;
    this.key = key;
  }

  @Override
  protected ValueWrapper run() {
    return globalCache.get(key);
  }

  @Override
  protected ValueWrapper getFallback() {
    log.warn("get fallback called, circuit is {}", super.circuitBreaker.isOpen());
    return null;
  }
}
