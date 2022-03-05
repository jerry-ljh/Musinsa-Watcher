package com.musinsa.watcher.domain.product.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductCountByBrandDto;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByBrandDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.hibernate.Hibernate;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductQuerySlaveRepositoryTest {

  @Autowired
  private ProductQuerySlaveRepository productQuerySlaveRepository;

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @Autowired
  private PriceSlaveRepository priceSlaveRepository;

  @Autowired
  private CacheManager cacheManager;

  private int productId;

  @After
  public void clear() {
    productSlaveRepository.deleteAllInBatch();
    priceSlaveRepository.deleteAllInBatch();
    cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    productId = 0;
  }

  @Test
  @DisplayName("특정 검색어를 상품 수를 카운트한다")
  public void countProductBySearchTopic() {
    saveProductByNameAndBrand("신발1", "나이키");
    saveProductByNameAndBrand("신발2", "나이키");
    saveProductByNameAndBrand("신발3", "나이키");
    saveProductByNameAndBrand("신발1", "아디다스");

    long count = productQuerySlaveRepository.countSearchItems("나이키", FilterVo.builder().build());

    assertEquals(count, 3);
  }

  @Test
  @DisplayName("특정 검색어를 조회한다.")
  public void findProductBySearchTopic() {
    saveProductByNameAndBrand("신발1", "나이키");
    saveProductByNameAndBrand("신발2", "나이키");
    saveProductByNameAndBrand("신발3", "언더아머");
    saveProductByNameAndBrand("신발4", "아디다스");

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .searchItems("나이키", FilterVo.builder().build(), PageRequest.of(0, 20));

    assertEquals(productList.getContent().size(), 2);
    productList.forEach(i -> {
      assertTrue(i.getProductName().contains("나이키") || i.getBrand().contains("나이키"));
    });
  }

  @Test
  @DisplayName("특정 검색어로 조회 결과가 없다면 비어있는 Page를 반환한다.")
  public void findProductByInValidTopic() {
    saveProductByNameAndBrand("신발1", "나이키");
    saveProductByNameAndBrand("신발2", "나이키");
    saveProductByNameAndBrand("신발3", "언더아머");
    saveProductByNameAndBrand("신발4", "아디다스");

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .searchItems("유니클로", FilterVo.builder().build(), PageRequest.of(0, 20));

    assertEquals(productList.getContent().size(), 0);
  }

  @Test
  @DisplayName("특정 카테고리의 상품 수를 카운트한다")
  public void countProductByCategory() {
    saveProductByCategory(Category.TOP);
    saveProductByCategory(Category.OUTER);
    saveProductByCategory(Category.OUTER);
    saveProductByCategory(Category.SKIRTS);
    saveProductByCategory(Category.SKIRTS);
    saveProductByCategory(Category.SKIRTS);

    long totalElementForTop = productQuerySlaveRepository.countTodayUpdatedProductByCategory(
        FilterVo.builder().categories(new Category[]{Category.TOP}).build());
    long totalElementForOuter = productQuerySlaveRepository.countTodayUpdatedProductByCategory(
        FilterVo.builder().categories(new Category[]{Category.OUTER}).build());
    long totalElementForSkirts = productQuerySlaveRepository.countTodayUpdatedProductByCategory(
        FilterVo.builder().categories(new Category[]{Category.SKIRTS}).build());

    assertEquals(totalElementForTop, 1);
    assertEquals(totalElementForOuter, 2);
    assertEquals(totalElementForSkirts, 3);
  }

  @Test
  @DisplayName("특정 카테고리를 조회한다.")
  public void findProductByCategory() {
    saveProductByCategory(Category.TOP);
    saveProductByCategory(Category.OUTER);
    saveProductByCategory(Category.OUTER);
    saveProductByCategory(Category.SKIRTS);
    saveProductByCategory(Category.SKIRTS);
    saveProductByCategory(Category.SKIRTS);

    Page<ProductResponseDto> productList = productQuerySlaveRepository
        .findTodayUpdatedProductByCategory(FilterVo.builder()
            .categories(new Category[]{Category.SKIRTS})
            .build(), PageRequest.of(0, 10));

    productList.forEach(i -> assertEquals(i.getCategory(), Category.SKIRTS.getCategory()));
  }

  @Test
  @DisplayName("가격과 상품을 같이 조회할 때 즉시 로딩된다")
  public void findProductWithPrice() {
    int productId = 1;
    saveProductByProductIdAndPricesCount(productId, 10);

    Product product = productQuerySlaveRepository.findByProductIdWithPrice(productId);

    assertTrue(Hibernate.isInitialized(product.getPrices()));
    assertEquals(product.getPrices().size(), 10);
  }

  @Test
  @DisplayName("마지막 업데이트된 시간을 조회한다")
  public void findLastModifiedDate() {
    for (int i = 0; i < 3; i++) {
      LocalDateTime modifiedDateTime = LocalDateTime.now().plusDays(i);
      productSlaveRepository.save(Product.builder()
          .productId(i)
          .modifiedDate(modifiedDateTime)
          .build());

      LocalDateTime result = productQuerySlaveRepository.findLastUpdatedDate();

      assertEquals(result.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
          modifiedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  @Test
  @DisplayName("특정 브랜드를 조회한다.")
  public void findProductByBrand() {
    saveProductByNameAndBrand("product 1", "A");
    saveProductByNameAndBrand("product 2", "B");
    saveProductByNameAndBrand("product 3", "C");

    Page<ProductResponseDto> results = productQuerySlaveRepository
        .findByBrand(FilterVo.builder().brands(new String[]{"A"}).build(),
            PageRequest.of(0, 25));

    assertEquals(results.getContent().size(), 1);
    results.forEach(i -> assertTrue(i.getBrand().contains("A")));
  }

  @Test
  @DisplayName("선행 일치로 브랜드별 상품 수를 조회한다.")
  public void findBrandBySearchTopic() {
    saveProductByNameAndBrand("product 1", "나이키");
    saveProductByNameAndBrand("product 2", "나이키 골프");
    saveProductByNameAndBrand("product 3", "아디다스");

    List<ProductCountByBrandDto> result = productQuerySlaveRepository.searchBrand("나이키");
    ProductCountMapByBrandDto resultMap = new ProductCountMapByBrandDto(result);

    assertEquals(result.size(), 2);
    resultMap.getBrandMap().forEach((key, value) -> {
      assertTrue(key.contains("나이키"));
      assertEquals(value.intValue(), 1);
    });
  }

  @Test
  @DisplayName("브랜드 리스트를 이니셜로 조회한다.")
  public void findBrandByInitial() {
    saveProductByNameAndBrand("product 1", "커버낫");
    saveProductByNameAndBrand("product 2", "무신사 스탠다드");
    saveProductByNameAndBrand("product 3", "마크 곤잘레스");

    List<ProductCountByBrandDto> result = productQuerySlaveRepository
        .findBrandByInitial(InitialWord.type5.getStart(), InitialWord.type5.getEnd());
    ProductCountMapByBrandDto resultMap = new ProductCountMapByBrandDto(result);

    assertFalse(resultMap.getBrandMap().containsKey("커버낫"));
    assertTrue(resultMap.getBrandMap().containsKey("무신사 스탠다드"));
    assertTrue(resultMap.getBrandMap().containsKey("마크 곤잘레스"));
  }

  private void saveProductByNameAndBrand(String name, String brand) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .productName(name)
        .brand(brand)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveProductByCategory(Category category) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .modifiedDate(LocalDateTime.now())
        .category(category.getCategory())
        .build());
  }

  private void saveProductByProductIdAndPricesCount(int productId, int count) {
    Product product = productSlaveRepository.save(Product.builder()
        .productId(productId)
        .modifiedDate(LocalDateTime.now())
        .build());
    for (int i = 0; i < count; i++) {
      priceSlaveRepository.save(Price.builder().product(product).build());
    }
  }
}
