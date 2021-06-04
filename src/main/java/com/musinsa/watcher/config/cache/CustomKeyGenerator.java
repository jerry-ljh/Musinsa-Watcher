package com.musinsa.watcher.config.cache;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

public class CustomKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append(method.getName());
    keyBuilder.append(SimpleKeyGenerator.generateKey(params));
    return keyBuilder.toString();
  }
}
