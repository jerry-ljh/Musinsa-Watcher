package com.musinsa.watcher.config.cache;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;

public class CustomKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append(method.getName());
    if (params.length > 0) {
      keyBuilder.append(";");
      for (Object argument : params) {
        if (argument instanceof Pageable) {
          Pageable pageable = (Pageable) argument;
          keyBuilder.append(pageable.getPageNumber());
          keyBuilder.append(";");
          keyBuilder.append(pageable.getPageSize());
          keyBuilder.append(";");
          keyBuilder.append(pageable.getSort().toString());
          keyBuilder.append(";");
          continue;
        }
        keyBuilder.append(argument);
        keyBuilder.append(";");
      }
    }
    return keyBuilder.toString();
  }
}
