package com.musinsa.watcher.domain.product.discount.slave;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.discount.TodayDiscountProduct;
import com.musinsa.watcher.domain.product.discount.TodayMinimumPriceProduct;
import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
public class DiscountedProductQueryRepositoryTest {

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  @Autowired
  private TodayDiscountProductRepository todayDiscountProductRepository;

  @Autowired
  private TodayMinimumPriceRepository todayMinimumPriceRepository;

  @Autowired
  private DiscountedProductQueryRepository discountedProductQueryRepository;

  @Autowired
  private CacheManager cacheManager;

  private int productId;

  @After
  public void setUp() {
    productId = 0;
    todayDiscountProductRepository.deleteAllInBatch();
    todayMinimumPriceRepository.deleteAllInBatch();
    productSlaveRepository.deleteAllInBatch();
    cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
  }

  @Test
  @DisplayName("어제 대비 오늘 할인한 상품을 조회한다.")
  public void findDiscountedProduct() {
    saveProduct(productId, Category.TOP);
    setTodayDiscountProductWithDiscountAndPercent(productId, 1000, 30);

    Page<TodayDiscountedProductDto> results = discountedProductQueryRepository
        .findTodayDiscountProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
  }

  @Test
  @DisplayName("오늘 할인된 상품을 정보가 없다면 빈 page객체를 반환한다.")
  public void findDiscountedProductWithNoDiscounted() {
    saveProduct(productId, Category.TOP);

    Page<TodayDiscountedProductDto> results = discountedProductQueryRepository
        .findTodayDiscountProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 0);
  }

  @Test
  @DisplayName("어제 대비 오늘 할인한 상품중 일정 할인율 이상을 달성하지 않으면 조회대상에서 제외된다.")
  public void findDiscountedProductWithLowDiscountRate() {
    double discountRatio = 0.99;
    saveProduct(productId, Category.TOP);
    setTodayDiscountProductWithDiscountAndPercent(productId, 1000, discountRatio);

    Page<TodayDiscountedProductDto> results = discountedProductQueryRepository
        .findTodayDiscountProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 0);
  }

  @Test
  @DisplayName("어제 대비 오늘 할인한 상품 수를 조회한다.")
  public void countDiscountedProduct() {
    saveProduct(productId, Category.TOP);
    setTodayDiscountProductWithDiscountAndPercent(productId, 1000, 30);

    long results = discountedProductQueryRepository.countTodayDiscountProducts(Category.TOP);

    assertEquals(results, 1);
  }

  @Test
  @DisplayName("어제 대비 오늘 할인한 상품 수를 카테고리별로 조회한다.")
  public void countDiscountedProductEachCategory() {
    setTodayDiscountProductWithCategory(productId++, Category.TOP);
    setTodayDiscountProductWithCategory(productId++, Category.OUTER);
    setTodayDiscountProductWithCategory(productId++, Category.OUTER);
    setTodayDiscountProductWithCategory(productId++, Category.SKIRTS);
    setTodayDiscountProductWithCategory(productId++, Category.PANTS);

    List<ProductCountByCategoryDto> results = discountedProductQueryRepository
        .countTodayDiscountProductEachCategory();

    Map<String, Integer> resultMap = new ProductCountMapByCategoryDto(results)
        .getCategoryProductMap();
    assertEquals(resultMap.get(Category.TOP.getCategory()).intValue(), 1);
    assertEquals(resultMap.get(Category.OUTER.getCategory()).intValue(), 2);
    assertEquals(resultMap.get(Category.PANTS.getCategory()).intValue(), 1);
    assertEquals(resultMap.get(Category.SKIRTS.getCategory()).intValue(), 1);
    assertEquals(resultMap.get(Category.SNEAKERS.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.SHOES.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.SOCK_LEGWEAR.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.ONEPIECE.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.BAG.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.HEADWEAR.getCategory()).intValue(), 0);
  }

  @Test
  @DisplayName("오늘 최저가인 상품을 조회한다.")
  public void findMinimumProduct() {
    int avgPrice = 10000;
    int todayPrice = 8000;
    int count = 100;
    saveProduct(productId, Category.TOP);
    setTodayMinimumPriceWithAvgPriceAndMinPriceAndCount(productId, avgPrice, todayPrice, count);

    Page<TodayMinimumPriceProductDto> results = discountedProductQueryRepository
        .findTodayMinimumPriceProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
  }

  @Test
  @DisplayName("오늘 최저가인 상품중 일정 할인율 이상을 달성하지 않으면 조회대상에서 제외된다.")
  public void findMinimumProductWithLowDiscountRate() {
    int avgPrice = 10000;
    int todayPrice = 9900;
    int count = 100;
    saveProduct(productId, Category.TOP);
    setTodayMinimumPriceWithAvgPriceAndMinPriceAndCount(productId, avgPrice, todayPrice, count);

    Page<TodayMinimumPriceProductDto> results = discountedProductQueryRepository
        .findTodayMinimumPriceProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 0);
  }

  @Test
  @DisplayName("오늘 최저가인 상품중 가격 데이터 표본 수가 기준치 미만이라면 조회대상에서 제외된다.")
  public void findMinimumProductWithLowSampleCount() {
    int avgPrice = 10000;
    int todayPrice = 8000;
    int count = 3;
    saveProduct(productId, Category.TOP);
    setTodayMinimumPriceWithAvgPriceAndMinPriceAndCount(productId, avgPrice, todayPrice, count);

    Page<TodayMinimumPriceProductDto> results = discountedProductQueryRepository
        .findTodayMinimumPriceProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 0);
  }

  @Test
  @DisplayName("오늘 최저가인 상품이 없다면 빈 page객체를 반환한다.")
  public void findMinimumProductWithNoMinimum() {
    saveProduct(productId, Category.TOP);

    Page<TodayMinimumPriceProductDto> results = discountedProductQueryRepository
        .findTodayMinimumPriceProducts(Category.TOP, "", PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 0);
  }

  @Test
  @DisplayName("오늘 최저가인 상품 수를 조회한다.")
  public void countMinimumProduct() {
    int avgPrice = 10000;
    int todayPrice = 8000;
    int count = 100;
    saveProduct(productId, Category.TOP);
    setTodayMinimumPriceWithAvgPriceAndMinPriceAndCount(productId, avgPrice, todayPrice, count);

    long results = discountedProductQueryRepository.countTodayMinimumPriceProducts(Category.TOP);

    assertEquals(results, 1);
  }

  @Test
  @DisplayName("오늘 최저가인 상품 수를 카테고리별로 조회한다.")
  public void countMinimumProductEachCategory() {
    setTodayMinimumPriceProductWithCategory(productId++, Category.BAG);
    setTodayMinimumPriceProductWithCategory(productId++, Category.HEADWEAR);
    setTodayMinimumPriceProductWithCategory(productId++, Category.HEADWEAR);
    setTodayMinimumPriceProductWithCategory(productId++, Category.SHOES);

    List<ProductCountByCategoryDto> results = discountedProductQueryRepository
        .countTodayMinimumPriceProductEachCategory();

    Map<String, Integer> resultMap = new ProductCountMapByCategoryDto(results)
        .getCategoryProductMap();
    assertEquals(resultMap.get(Category.TOP.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.OUTER.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.PANTS.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.SKIRTS.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.SNEAKERS.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.SHOES.getCategory()).intValue(), 1);
    assertEquals(resultMap.get(Category.SOCK_LEGWEAR.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.ONEPIECE.getCategory()).intValue(), 0);
    assertEquals(resultMap.get(Category.BAG.getCategory()).intValue(), 1);
    assertEquals(resultMap.get(Category.HEADWEAR.getCategory()).intValue(), 2);
  }

  public void saveProduct(int productId, Category category) {
    productSlaveRepository.save(Product.builder()
        .productId(productId)
        .category(category.getCategory())
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  public void setTodayDiscountProductWithDiscountAndPercent(int productId, int discount,
      double percent) {
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(productSlaveRepository.getOne((long) productId))
        .discount(discount)
        .percent(percent)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  public void setTodayMinimumPriceWithAvgPriceAndMinPriceAndCount(int productId, int avgPrice,
      int todayPrice, int count) {
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(productSlaveRepository.getOne((long) productId))
        .avgPrice(avgPrice)
        .minPrice(todayPrice)
        .todayPrice(todayPrice)
        .count(count)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  public void setTodayDiscountProductWithCategory(int productId, Category category) {
    Product product = Product.builder()
        .productId(productId)
        .category(category.getCategory())
        .modifiedDate(LocalDateTime.now())
        .build();
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(productSlaveRepository.save(product))
        .discount(1000)
        .percent(10)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  public void setTodayMinimumPriceProductWithCategory(int productId, Category category) {
    Product product = productSlaveRepository.save(Product.builder()
        .productId(productId)
        .category(category.getCategory())
        .modifiedDate(LocalDateTime.now())
        .build());
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product)
        .avgPrice(10000)
        .minPrice(8000)
        .todayPrice(8000)
        .count(100)
        .modifiedDate(LocalDateTime.now())
        .build());
  }
}