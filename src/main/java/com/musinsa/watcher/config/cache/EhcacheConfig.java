package com.musinsa.watcher.config.cache;

import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class EhcacheConfig {

  @Bean
  public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
    EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    ehCacheManagerFactoryBean.setConfigLocation(
        new ClassPathResource("ehcache.xml"));
    ehCacheManagerFactoryBean.setShared(true);
    return ehCacheManagerFactoryBean;
  }
}
