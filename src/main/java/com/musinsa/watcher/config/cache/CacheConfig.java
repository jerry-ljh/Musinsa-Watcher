package com.musinsa.watcher.config.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

  private final RedisConnectionFactory connectionFactory;
  private final EhCacheManagerFactoryBean ehCacheManagerFactoryBean;


  @Bean
  public CacheManager localCacheManager(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
    EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
    ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean.getObject());
    return ehCacheCacheManager;
  }

  @Bean
  public CacheManager globalCacheManager() {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
    RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
        .fromConnectionFactory(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
    return redisCacheManager;
  }

  @Bean
  @Primary
  @Override
  public CacheManager cacheManager() {
    return new ChainedCacheManager(localCacheManager(ehCacheManagerFactoryBean),
        globalCacheManager());
  }
}
