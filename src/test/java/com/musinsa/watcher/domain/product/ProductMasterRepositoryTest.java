package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.product.master.ProductMasterRepository;
import com.musinsa.watcher.domain.product.master.ProductQueryMasterRepository;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.After;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductMasterRepositoryTest {

  @Autowired
  private ProductQueryMasterRepository productQueryMasterRepository;

  @Autowired
  private ProductMasterRepository productMasterRepository;

  @Autowired
  private ProductQuerySlaveRepository productQuerySlaveRepository;

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @After
  public void clear() {
    productSlaveRepository.deleteAll();
    productMasterRepository.deleteAll();
  }

  @Test
  @DisplayName("master와 slave의 repository는 분리된다.")
  public void separate() {
    for (int i = 0; i < 10; i++) {
      productMasterRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    assertEquals(productMasterRepository.findAll().size(), 10);
    assertEquals(productSlaveRepository.findAll().size(), 20);

  }

  @Test
  @DisplayName("master와 slave의 queryDsl은 분리된다.")
  public void separateQueryDsl() {
    Initial initial = InitialWord.type1.getInitials();
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productMasterRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("보통 브랜드")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productMasterRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    List<Object[]> masterList = productQueryMasterRepository
        .findBrandByInitial(initial.getSTART(), initial.getEND());
    List<Object[]> slaveList = productQuerySlaveRepository
        .findBrandByInitial(initial.getSTART(), initial.getEND());

    assertEquals(productSlaveRepository.findAll().size(), 10);
    assertEquals(productMasterRepository.findAll().size(), 20);
    assertEquals(masterList.size(), 0);
    assertEquals(slaveList.get(0)[1], 10L);

  }

  @Test
  @DisplayName("큰 이미지 url를 만든다")
  public void mkBigImageUrl() {
    Product product = Product.builder()
        .productId(10000)
        .productName("셔츠")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .rank(1).build();
    String bigImgUrl = product.convertToBigImgUrl();
    assertEquals(bigImgUrl, "http://cdn.img?id=10000_500.jpg");
  }


}