package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.Test;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

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