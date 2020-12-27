package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.PriceRepository;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductQueryRepositoryTest {

  @Autowired
  private ProductQueryRepository productQueryRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private PriceRepository priceRepository;

  @After
  public void clear() {
    productRepository.deleteAll();
  }


  @Test
  @DisplayName("특정 검색어를 조회한다.")
  public void findByTopic() {
    String searchText = "신발";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    Page<ProductResponseDto> productPage = productQueryRepository
        .searchItems(searchText, PageRequest.of(0, 10));
    List<ProductResponseDto> productList = productPage.getContent();
    assertEquals(productPage.getTotalElements(), 20);
    productList.stream().forEach(i -> assertTrue(
        i.getProductName().contains(searchText) ||
            i.getBrand().contains(searchText)
    ));
  }

  @Test
  @DisplayName("가격과 상품을 같이 조회한다")
  public void findProductWithPrice() throws Exception {
    int productId = 1;
    String productName = "고급 신발";
    String brand = "고급 브랜드";
    String category = "001";
    String img = "http://cdn.img?id=10000_125.jpg";
    Product product = Product.builder()
        .productId(productId)
        .productName(productName)
        .brand(brand)
        .category(category)
        .img(img)
        .modifiedDate(LocalDateTime.now())
        .build();
    productRepository.save(product);
    for (int i = 0; i < 5; i++) {
      priceRepository.save(Price.builder()
          .product(product)
          .delPrice(10000)
          .price(1000)
          .coupon(-200)
          .rank(1)
          .rating(11)
          .ratingCount(120)
          .build());
      Thread.sleep(1000);
    }
    Product product1 = productQueryRepository.findProductWithPrice(productId);
    assertEquals(product1.getProductName(), productName);
    assertEquals(product1.getCategory(), category);
    assertEquals(product1.getProductId(), productId);
    assertEquals(product1.getBrand(), brand);
    assertEquals(product1.getImg(), img);
    System.out.println(product1.getPrices().size());
    for (int i = 0; i < 4; i++) {
      assertTrue(product1.getPrices().get(i).getCreatedDate().
          isAfter(product1.getPrices().get(i + 1).getCreatedDate()));
    }
  }

  @Test
  @DisplayName("마지막 업데이트된 시간을 조회한다")
  public void findLastModifiedDate() throws Exception{
    String productName = "고급 신발";
    String brand = "고급 브랜드";
    String category = "001";
    String img = "http://cdn.img?id=10000_125.jpg";
    for (int i = 0; i < 5; i++) {
      Product product = Product.builder()
          .productId(i)
          .productName(productName)
          .brand(brand)
          .category(category)
          .img(img)
          .modifiedDate(LocalDateTime.now())
          .build();
      productRepository.save(product);
      Thread.sleep(1000);
    }
    LocalDateTime result = productQueryRepository.findLastUpdateDate();
    List<Product> productList = productRepository.findAll();
    LocalDateTime lastModifiedDate = productList.get(0).getModifiedDate();
    for(Product product : productList){
      lastModifiedDate = product.getModifiedDate().isAfter(lastModifiedDate)
          ? product.getModifiedDate()
          : lastModifiedDate;
    }
    assertEquals(result, lastModifiedDate);
  }

}