package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.PriceRepository;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

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
  @DisplayName("특정 검색어를 상품 수를 카운트한다")
  public void countFindByTopic() {
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
    long count = productQueryRepository.countSearchItems(searchText);
    assertEquals(count, 20);
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
        .searchItems(searchText, PageRequest.of(0, 20));
    assertEquals(productPage.getTotalElements(), 20);
    List<ProductResponseDto> productList = productPage.getContent();
    productList.stream().forEach(i -> assertTrue(
        i.getProductName().contains(searchText) || i.getBrand().contains(searchText)));
  }

  @Test
  @DisplayName("잘못된 검색어를 조회한다.")
  public void findByValidTopic() {
    String searchText = "양말";
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
    assertEquals(productPage.getTotalElements(), 0);
    assertEquals(productList.size(), 0);
  }

  @Test
  @DisplayName("특정 카테고리의 상품 수를 카운트한다")
  public void countFindByCategory() {
    String category = "001";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category(category)
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

    long totalElement = productQueryRepository
        .countFindByCategory(category, LocalDateTime.now().toLocalDate());
    assertEquals(totalElement, 10);

  }

  @Test
  @DisplayName("특정 카테고리를 조회한다.")
  public void findByCategory() {
    String category = "001";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category(category)
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
        .findByCategory(category, LocalDateTime.now().toLocalDate(), PageRequest.of(0, 10));
    List<ProductResponseDto> productList = productPage.getContent();
    assertEquals(productPage.getTotalElements(), 10);
    productList.stream().forEach(i -> assertTrue(i.getCategory().equals(category)));
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
  public void findLastModifiedDate() throws Exception {
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
    for (Product product : productList) {
      lastModifiedDate = product.getModifiedDate().isAfter(lastModifiedDate)
          ? product.getModifiedDate()
          : lastModifiedDate;
    }
    assertEquals(result, lastModifiedDate);
  }

  @Test
  @DisplayName("특정 브랜드를  조회한다.")
  public void findByBrand() {
    String findBrand = "고급 브랜드";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(findBrand)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(findBrand)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    Page<ProductResponseDto> productPage = productQueryRepository
        .findByBrand(findBrand, PageRequest.of(0, 25));
    List<ProductResponseDto> productList = productPage.getContent();
    assertEquals(productPage.getTotalElements(), 20);
    productList.stream().forEach(i -> assertEquals(i.getBrand(), findBrand));
  }

  @Test
  @DisplayName("특정 상품 수를  조회한다.")
  public void countFindByBrand() {
    String findBrand = "고급 브랜드";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(findBrand)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(findBrand)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }
    long count = productQueryRepository.countFindByBrand(findBrand);
    assertEquals(count, 20);
  }

  @Test
  @DisplayName("브랜드 리스트를 이니셜로 조회한다.")
  public void 이니셜로_조회() {
    Initial initial = InitialWord.type1.getInitials();
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("보통 브랜드")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    List<Object[]> brandList = productQueryRepository
        .findBrandByInitial(initial.getSTART(), initial.getEND());

    assertEquals(brandList.size(), 1);
    assertEquals(brandList.get(0)[0], "고급 브랜드");
    assertEquals(brandList.get(0)[1], 10L);
  }
}