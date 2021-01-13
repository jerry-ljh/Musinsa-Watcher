package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class CacheServiceIntegratationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CacheService cacheService;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private RedisCacheManager redisCacheManager;

  @Autowired
  private EhCacheCacheManager ehCacheCacheManager;

  @Before
  public void clean() {
    cacheManager.getCache("productCache").clear();
    productRepository.deleteAll();
  }

  @Test
  @DisplayName("캐시를 사용하여 호출한다.")
  public void callCache() {
    LocalDateTime localDateTime1 = LocalDateTime.now();
    LocalDateTime localDateTime2 = localDateTime1.plusDays(3);
    productRepository.save(Product.builder()
        .productId(1)
        .productName("고급 신발")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .modifiedDate(localDateTime1)
        .build());
    cacheService.updateCacheByDate();
    productRepository.save(Product.builder()
        .productId(2)
        .productName("고급 신발")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .modifiedDate(localDateTime2)
        .build());

    cacheService.getLastUpdatedDate();
    LocalDate localDate = cacheService.getLastUpdatedDate();

    assertEquals(localDate, localDateTime1.toLocalDate());
  }

  @Test
  @DisplayName("캐시를 초기화 한다.")
  public void clearCache() {
    //given
    LocalDateTime localDateTime1 = LocalDateTime.now();
    LocalDateTime localDateTime2 = localDateTime1.plusMinutes(1L);
    productRepository.save(Product.builder()
        .productId(1)
        .productName("고급 신발")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .modifiedDate(localDateTime1)
        .build());
    //when
    cacheService.updateCacheByDate();
    //given
    productRepository.save(Product.builder()
        .productId(2)
        .productName("고급 신발")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .modifiedDate(localDateTime2)
        .build());
    //when
    cacheService.updateCacheByDate();
    LocalDateTime cachedLocalDateTime = (LocalDateTime) cacheManager.getCache("productCache")
        .get("current date").get();

    //then
    assertEquals(cachedLocalDateTime, localDateTime2);
  }

  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 같아 동기화 하지 않는다.")
  public void cacheSynchronized1() {
    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    redisCacheManager.getCache("productCache").put("current date", localDateTime);
    ehCacheCacheManager.getCache("productCache").put("current date", localDateTime);
    //when
    cacheService.doSynchronize();
    //then
    assertEquals(redisCacheManager.getCache("productCache").get("current date").get(),
        ehCacheCacheManager.getCache("productCache").get("current date").get());
  }

  @Test
  @DisplayName("글로벌 캐시와 로컬 캐시가 달라 동기화 한다.")
  public void cacheSynchronized2() {
    //given
    LocalDateTime localDateTime = LocalDateTime.now();
    redisCacheManager.getCache("productCache").put("current date", localDateTime.plusDays(1));
    ehCacheCacheManager.getCache("productCache").put("current date", localDateTime);
    //when
    cacheService.doSynchronize();
    //then
    assertNull(ehCacheCacheManager.getCache("productCache").get("current date"));
  }
}
