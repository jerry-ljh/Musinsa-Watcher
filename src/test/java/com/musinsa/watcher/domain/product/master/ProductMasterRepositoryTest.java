package com.musinsa.watcher.domain.product.master;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import org.junit.Before;
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
  private ProductMasterRepository productMasterRepository;

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @Before
  public void setUp(){
    productMasterRepository.deleteAllInBatch();
    productSlaveRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("master db와 slave db는 분리된다.")
  public void separate() {
    Product product = Product.builder()
        .productId(1)
        .build();

    saveMasterRepository(product);

    assertEquals(productMasterRepository.findAll().size(), 1);
    assertEquals(productSlaveRepository.findAll().size(), 0);
  }

  @Test
  @DisplayName("큰 이미지 url를 만든다")
  public void mkBigImageUrl() {
    Product product = Product.builder()
        .img("http://cdn.img?id=10000_125.jpg")
        .build();

    String bigImgUrl = product.convertToBigImgUrl();

    assertEquals(bigImgUrl, "http://cdn.img?id=10000_500.jpg");
  }

  private void saveMasterRepository(Product product) {
    productMasterRepository.save(product);
  }

}