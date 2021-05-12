package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
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
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class CacheServiceIntegrationTest {

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private CacheManager cacheManager;

  @Before
  public void clean() {
    cacheManager.getCache("productCache").clear();
    productSlaveRepository.deleteAll();
  }

  @Test
  @DisplayName("데이터의 마지막 업데이트 시간은 캐시된다.")
  public void callCache() {
    //given
    int productId = 1;
    LocalDateTime updatedDate = LocalDateTime.now();
    saveProduct(productId, updatedDate);
    productRepository.findCachedLastUpdatedDateTime();
    //when
    LocalDate localDate = ((LocalDateTime) cacheManager.getCache("productCache")
        .get("current date").get()).toLocalDate();
    //then
    assertEquals(localDate, updatedDate.toLocalDate());
  }

  @Test
  @DisplayName("데이터가 업데이트되면 캐시를 초기화 한다.")
  public void clearCache() {
    //given
    int productId1 = 1;
    int productId2 = 2;
    LocalDateTime localDateTime1 = LocalDateTime.now();
    LocalDateTime localDateTime2 = localDateTime1.plusMinutes(1L);
    saveProduct(productId1, localDateTime1);
    LocalDateTime initialDateTime = productRepository.findCachedLastUpdatedDateTime();
    saveProduct(productId2, localDateTime2);
    LocalDateTime updatedDateTime = productService.getLastUpdatedDateTime();
    //when
    productService.clearCacheIfOld(updatedDateTime, initialDateTime);
    //then
    assertNull(cacheManager.getCache("productCache").get("current date"));
  }

  public void saveProduct(int id, LocalDateTime localDateTime) {
    productSlaveRepository.save(Product.builder()
        .productId(id)
        .productName("고급 신발")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .modifiedDate(localDateTime)
        .build());
  }
}
