package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

@Slf4j
public class HystrixClearCommand extends HystrixCommand {

  private final Cache globalCache;
  private final Cache localCache;

  public HystrixClearCommand(Cache localCache, Cache globalCache) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("cacheGroupKey"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("cache-clear"))
        .andCommandPropertiesDefaults(
            HystrixCommandProperties.defaultSetter()
                .withExecutionTimeoutInMilliseconds(1000)
                .withCircuitBreakerErrorThresholdPercentage(50)
                .withCircuitBreakerRequestVolumeThreshold(10)
                .withCircuitBreakerSleepWindowInMilliseconds(30000)
                .withMetricsRollingStatisticalWindowInMilliseconds(10000)));
    this.globalCache = globalCache;
    this.localCache = localCache;
  }

  @Override
  protected Object run() {
    localCache.clear();
    globalCache.clear();
    log.info("로컬 clear");
    log.info("글로벌 clear");
    return null;
  }

  @Override
  protected ValueWrapper getFallback() {
    log.warn("cache clear fallback called, circuit is {}", super.circuitBreaker.isOpen());
    localCache.clear();
    log.info("로컬 clear");
    return null;
  }

}
