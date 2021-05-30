package com.musinsa.watcher.domain.price.slave;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.price.TodayDiscountProduct;
import com.musinsa.watcher.domain.price.TodayMinimumPriceProduct;
import com.musinsa.watcher.domain.price.dto.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceSlaveQueryRepositoryTest {

  @Autowired
  private PriceSlaveQueryRepository priceSlaveQueryRepository;
  @Autowired
  private ProductSlaveRepository productSlaveRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private TodayDiscountProductRepository todayDiscountProductRepository;
  @Autowired
  private TodayMinimumPriceRepository todayMinimumPriceRepository;

  @After
  public void tearDown() {
    todayMinimumPriceRepository.deleteAll();
    todayDiscountProductRepository.deleteAll();
    productSlaveRepository.deleteAll();
  }

  @Test
  @DisplayName("오늘 할인 상품을 조회한다")
  public void 오늘할인상품_조회() {
    //given
    saveTodayDiscountProduct();
    //when
    Page<TodayDiscountedProductDto> result = priceSlaveQueryRepository
        .findTodayDiscountProducts(Category.TOP, LocalDate.now(), PageRequest.of(0, 10),
            "percent desc");
    //then
    assertEquals(result.getTotalElements(), 1);
  }

  @Test
  @DisplayName("오늘 할인 상품의 카테고리별 수를 카운팅한다.")
  public void 오늘할인상품을_카테고리별로_카운트한다() {
    //given
    saveTodayDiscountProduct();
    //when
    List<ProductCountByCategoryDto> result = priceSlaveQueryRepository
        .countDiscountProductEachCategory(LocalDate.now());

    //then
    ProductCountMapByCategoryDto resultMap = new ProductCountMapByCategoryDto(result);;
    assertEquals(resultMap.getCategoryProductMap().keySet().size(), 3);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.TOP.getCategory()), 1);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.BAG.getCategory()), 1);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.SKIRTS.getCategory()), 1);
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품을 조회한다")
  public void 오늘역대최저가상품_조회() {
    //given
    saveTodayMinimumPriceProduct();
    //when
    Page<TodayMinimumPriceProductDto> result = priceSlaveQueryRepository
        .findTodayMinimumPriceProducts(Category.TOP, LocalDate.now(), PageRequest.of(0, 10),
            "percent desc");
    //then
    assertEquals(result.getTotalElements(), 1);
  }


  @Test
  @DisplayName("오늘 역대 최저가 상품의 카테고리별 수를 카운팅한다.")
  public void 오늘역대최저가상품을_카테고리별로_카운트한다() {
    saveTodayMinimumPriceProduct();

    List<ProductCountByCategoryDto> result = priceSlaveQueryRepository
        .countMinimumPriceProductEachCategory(LocalDate.now());

    ProductCountMapByCategoryDto resultMap = new ProductCountMapByCategoryDto(result);
    assertEquals(resultMap.getCategoryProductMap().keySet().size(), 3);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.TOP.getCategory()), 1);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.BAG.getCategory()), 1);
    assertEquals((int) resultMap.getCategoryProductMap().get(Category.SKIRTS.getCategory()), 1);
  }

  @Test
  @DisplayName("input에 따라 결과가 정렬된다.")
  public void input에_따라_결과가_정렬된다() {
    String[] sorts = new String[]{"percent desc", "percent asc", "price asc", "price desc"};
    saveTodayDiscountProductForTopCategory();
    saveTodayMinimumPriceProductForTopCategory();

    for (String sort : sorts) {
      Page<TodayDiscountedProductDto> discountResult = priceSlaveQueryRepository
          .findTodayDiscountProducts(Category.TOP, LocalDate.now(), PageRequest.of(0, 10),
              sort);
      Page<TodayMinimumPriceProductDto> minimumPriceResult = priceSlaveQueryRepository
          .findTodayMinimumPriceProducts(Category.TOP, LocalDate.now(), PageRequest.of(0, 10),
              sort);

      if (sort.equals("percent desc")) {
        assertEquals("C product", discountResult.getContent().get(0).getProductName());
        assertEquals("B product", minimumPriceResult.getContent().get(0).getProductName());
      } else if (sort.equals("percent asc")) {
        assertEquals("A product", discountResult.getContent().get(0).getProductName());
        assertEquals("C product", minimumPriceResult.getContent().get(0).getProductName());
      } else if (sort.equals("price desc")) {
        assertEquals("C product", discountResult.getContent().get(0).getProductName());
        assertEquals("C product", minimumPriceResult.getContent().get(0).getProductName());
      } else if (sort.equals("price asc")) {
        assertEquals("A product", discountResult.getContent().get(0).getProductName());
        assertEquals("A product", minimumPriceResult.getContent().get(0).getProductName());
      }
    }
  }

  private void saveTodayMinimumPriceProduct() {
    Product product1 = Product.builder()
        .productId(1)
        .productName("A product")
        .brand("A brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(1000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product2 = Product.builder()
        .productId(2)
        .productName("B product")
        .brand("B brand")
        .category(Category.BAG.getCategory())
        .img("https://www.img.com")
        .realPrice(10000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product3 = Product.builder()
        .productId(3)
        .productName("C product")
        .brand("C brand")
        .category(Category.SKIRTS.getCategory())
        .img("https://www.img.com")
        .realPrice(100000)
        .modifiedDate(LocalDateTime.now())
        .build();
    productSlaveRepository.save(product1);
    productSlaveRepository.save(product2);
    productSlaveRepository.save(product3);
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product1)
        .minPrice(1000)
        .avgPrice(3000)
        .todayPrice(1000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product2)
        .minPrice(10000)
        .avgPrice(100000)
        .todayPrice(10000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product3)
        .minPrice(100000)
        .avgPrice(110000)
        .todayPrice(100000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveTodayDiscountProduct() {
    Product product1 = Product.builder()
        .productId(4)
        .productName("A product")
        .brand("A brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(1000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product2 = Product.builder()
        .productId(5)
        .productName("B product")
        .brand("B brand")
        .category(Category.BAG.getCategory())
        .img("https://www.img.com")
        .realPrice(10000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product3 = Product.builder()
        .productId(6)
        .productName("C product")
        .brand("C brand")
        .category(Category.SKIRTS.getCategory())
        .img("https://www.img.com")
        .realPrice(100000)
        .modifiedDate(LocalDateTime.now())
        .build();
    productSlaveRepository.save(product1);
    productSlaveRepository.save(product2);
    productSlaveRepository.save(product3);
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product1)
        .percent(10)
        .discount(1000)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product2)
        .percent(20)
        .discount(2000)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product3)
        .percent(30)
        .discount(3000)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveTodayMinimumPriceProductForTopCategory() {
    Product product1 = Product.builder()
        .productId(1)
        .productName("A product")
        .brand("A brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(1000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product2 = Product.builder()
        .productId(2)
        .productName("B product")
        .brand("B brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(10000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product3 = Product.builder()
        .productId(3)
        .productName("C product")
        .brand("C brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(100000)
        .modifiedDate(LocalDateTime.now())
        .build();
    productSlaveRepository.save(product1);
    productSlaveRepository.save(product2);
    productSlaveRepository.save(product3);
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product1)
        .minPrice(1000)
        .avgPrice(3000)
        .todayPrice(1000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product2)
        .minPrice(10000)
        .avgPrice(100000)
        .todayPrice(10000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayMinimumPriceRepository.save(TodayMinimumPriceProduct.builder()
        .product(product3)
        .minPrice(100000)
        .avgPrice(110000)
        .todayPrice(100000)
        .count(10)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveTodayDiscountProductForTopCategory() {
    Product product1 = Product.builder()
        .productId(4)
        .productName("A product")
        .brand("A brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(1000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product2 = Product.builder()
        .productId(5)
        .productName("B product")
        .brand("B brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(10000)
        .modifiedDate(LocalDateTime.now())
        .build();
    Product product3 = Product.builder()
        .productId(6)
        .productName("C product")
        .brand("C brand")
        .category(Category.TOP.getCategory())
        .img("https://www.img.com")
        .realPrice(100000)
        .modifiedDate(LocalDateTime.now())
        .build();
    productSlaveRepository.save(product1);
    productSlaveRepository.save(product2);
    productSlaveRepository.save(product3);
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product1)
        .percent(10)
        .discount(1000)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product2)
        .percent(20)
        .discount(2000)
        .modifiedDate(LocalDateTime.now())
        .build());
    todayDiscountProductRepository.save(TodayDiscountProduct.builder()
        .product(product3)
        .percent(30)
        .discount(3000)
        .modifiedDate(LocalDateTime.now())
        .build());
  }
}