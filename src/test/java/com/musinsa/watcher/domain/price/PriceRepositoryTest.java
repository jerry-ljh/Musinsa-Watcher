package com.musinsa.watcher.domain.price;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductRepository;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PriceRepositoryTest {

  @Autowired
  private PriceRepository priceRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("가격이 조회 된다.")
  public void 가격이_조회된다() {
    String productName = "고급 신발";
    String brand = "고급 브랜드";
    String category = "001";
    String img = "http://cdn.img?id=10000_125.jpg";
    Product product = Product.builder()
        .productId(1)
        .productName(productName)
        .brand(brand)
        .category(category)
        .img(img)
        .modifiedDate(LocalDateTime.now())
        .build();
    productRepository.save(product);
    priceRepository.save(Price.builder()
        .product(product)
        .delPrice(10000)
        .price(1000)
        .coupon(-200)
        .rank(1)
        .rating(11)
        .ratingCount(120)
        .build());

    List<Price> priceList = priceRepository.findAll();
    Price price = priceList.get(0);
    Product product1 = price.getProduct();
    assertEquals(price.getPrice(), 1000);
    assertEquals(price.getDelPrice(), 10000);
    assertEquals(price.getRank(), 1);
    assertEquals(Double.compare(price.getRating(), (double)11), 0);
    assertEquals(price.getRatingCount(), 120);
    assertEquals(price.getCoupon(), -200);
    assertEquals(product1.getProductName(), product.getProductName());
  }
}