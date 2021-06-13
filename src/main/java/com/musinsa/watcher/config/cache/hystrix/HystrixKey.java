package com.musinsa.watcher.config.cache.hystrix;

import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HystrixKey {

  private HystrixKey() {
  }

  public static Setter getKey(String keyName) {
    return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("cacheGroupKey"))
        .andCommandKey(HystrixCommandKey.Factory.asKey(keyName))
        .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter()
            .withExecutionTimeoutInMilliseconds(1000));
  }
}
