package com.musinsa.watcher.domain.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.musinsa.watcher.domain.price.dto.ProductCountByBrandDto;
import com.musinsa.watcher.web.dto.Filter;
import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByBrandDto;
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
public class ProductQuerySlaveRepositoryTest {

  @Autowired
  private ProductQuerySlaveRepository productQuerySlaveRepository;

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @Autowired
  private PriceSlaveRepository priceSlaveRepository;

  @After
  public void clear() {
    productSlaveRepository.deleteAll();
  }

  @Test
  @DisplayName("특정 검색어를 상품 수를 카운트한다")
  public void countFindByTopic() {
    String searchText = "신발";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    assertEquals(productSlaveRepository.findAll().size(), 30);
    assertEquals(productQuerySlaveRepository.countSearchItems(searchText, Filter.builder().build()),
        20);
  }

  @Test
  @DisplayName("특정 검색어를 조회한다.")
  public void findByTopic() {
    String searchText = "신발";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }
    assertEquals(productSlaveRepository.findAll().size(), 30);

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .searchItems(searchText, Filter.builder().build(), PageRequest.of(0, 20));
    assertEquals(productList.getContent().size(), 20);
    productList.stream().forEach(i -> assertTrue(
        i.getProductName().contains(searchText) || i.getBrand().contains(searchText)));


  }

  @Test
  @DisplayName("잘못된 검색어를 조회한다.")
  public void findByValidTopic() {
    String searchText = "양말";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }
    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .searchItems(searchText, Filter.builder().build(), PageRequest.of(0, 10));
    assertEquals(productList.toList().size(), 0);
    assertEquals(productList.toList().size(), 0);
  }

  @Test
  @DisplayName("특정 카테고리의 상품 수를 카운트한다")
  public void countFindByCategory() {
    String category = "001";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category(category)
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    long totalElement = productQuerySlaveRepository
        .countByCategoryAndDate(
            Filter.builder().categories(new Category[]{Category.getCategory(category)}).build(),
            LocalDateTime.now().toLocalDate());
    assertEquals(totalElement, 10);
  }

  @Test
  @DisplayName("특정 카테고리를 조회한다.")
  public void findByCategory() {
    String category = "001";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category(category)
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }
    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .findByCategoryAndDate(
            Filter.builder().categories(new Category[]{Category.getCategory(category)}).build(),
            LocalDateTime.now().toLocalDate(), PageRequest.of(0, 10));
    assertEquals(productList.getContent().size(), 10);
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
    productSlaveRepository.save(product);
    for (int i = 0; i < 5; i++) {
      priceSlaveRepository.save(Price.builder()
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
    Product product1 = productQuerySlaveRepository.findByProductIdWithPrice(productId);
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
      productSlaveRepository.save(product);
      Thread.sleep(1000);
    }
    LocalDateTime result = productQuerySlaveRepository.findLastUpdatedDate();
    List<Product> productList = productSlaveRepository.findAll();
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
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(findBrand)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(findBrand)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .findByBrand(Filter.builder().brands(new String[]{findBrand}).build(),
            PageRequest.of(0, 25));
    long size = productQuerySlaveRepository
        .countByBrand(Filter.builder().brands(new String[]{findBrand}).build());
    assertEquals(productList.getContent().size(), 20);
    assertEquals(size, 20);
    productList.stream().forEach(i -> assertEquals(i.getBrand(), findBrand));
  }

  @Test
  @DisplayName("브랜드를 자동 완성으로 검색한다.")
  public void findBrandList() {
    String brand1 = "고급 브랜드";
    String brand2 = "일반 브랜드";
    String brand3 = "최고급 브랜드";
    String searchText = "고급";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(brand1)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(brand2)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand(brand3)
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    List<ProductCountByBrandDto> result = productQuerySlaveRepository.searchBrand(searchText);
    ProductCountMapByBrandDto resultMap = new ProductCountMapByBrandDto(result);

    assertEquals((int) resultMap.getBrandMap().get(brand1), 10);
  }

  @Test
  @DisplayName("특정 브랜드중 특정 카테고리를 조회한다.")
  public void findByBrandAndUpdatedAndCategory() {
    String findBrand = "고급 브랜드";
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(findBrand)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(findBrand)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now().minusDays(3))
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand(findBrand)
          .category("004")
          .img("http://cdn.img?id=10000_125.jpg")
          .modifiedDate(LocalDateTime.now())
          .build());
    }

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .findByBrand(
            Filter.builder().brands(new String[]{findBrand}).categories(
                new Category[]{Category.getCategory("001"), Category.getCategory("003")})
                .build(),
            PageRequest.of(0, 25));
    assertEquals(productList.getContent().size(), 20);
    productList.stream().forEach(i -> assertEquals(i.getBrand(), findBrand));
  }

  @Test
  @DisplayName("브랜드 리스트를 이니셜로 조회한다.")
  public void 이니셜로_조회() {
    Initial initial = InitialWord.type1.getInitials();
    for (int i = 0; i < 10; i++) {
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("보통 브랜드")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productSlaveRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    List<ProductCountByBrandDto> result = productQuerySlaveRepository
        .findBrandByInitial(initial.getSTART(), initial.getEND());
    ProductCountMapByBrandDto resultMap = new ProductCountMapByBrandDto(result);

    assertEquals((int) resultMap.getBrandMap().get("고급 브랜드"), 10);
  }
}