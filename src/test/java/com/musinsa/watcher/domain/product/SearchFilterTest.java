package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchFilterTest {

  @Autowired
  private ProductQuerySlaveRepository productQuerySlaveRepository;

  @Autowired
  private ProductSlaveRepository productSlaveRepository;

  private int productId = 0;

  @After
  public void clear() {
    productId = 0;
    productSlaveRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("카테고리로 필터링한다.")
  public void filterCategory() {
    String searchText = "나이키";
    saveProductWithCategory("나이키 신발", Category.SHOES);
    saveProductWithCategory("나이키 티셔츠", Category.TOP);
    saveProductWithCategory("나이키 모자", Category.HEADWEAR);

    Page<ProductResponseDto> results = productQuerySlaveRepository.searchItems(searchText,
        FilterVo.builder().categories(new Category[]{Category.SHOES}).build(),
        PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
    assertEquals(results.getContent().get(0).getCategory(), Category.SHOES.getCategory());
  }

  @Test
  @DisplayName("브랜드로 필터링한다.")
  public void filterBrand() {
    String searchText = "신발";
    saveProductWithBrand("나이키 신발", "나이키");
    saveProductWithBrand("언더아머 신발", "언더아머");
    saveProductWithBrand("아디다스 신발", "아디다스");

    Page<ProductResponseDto> results = productQuerySlaveRepository
        .searchItems(searchText, FilterVo.builder().brands(new String[]{"나이키"}).build(),
            PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
    assertEquals(results.getContent().get(0).getBrand(), "나이키");
  }

  @Test
  @DisplayName("최소 금액으로 필터링한다.")
  public void filterMinPrice() {
    String searchText = "신발";
    saveProductWithPrice("나이키 신발", 10000);
    saveProductWithPrice("언더아머 신발", 15000);
    saveProductWithPrice("아디다스 신발", 20000);

    Page<ProductResponseDto> results = productQuerySlaveRepository
        .searchItems(searchText, FilterVo.builder().minPrice(14900).build(), PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 2);
    assertTrue(results.getContent().get(0).getRealPrice() > 14900);
    assertTrue(results.getContent().get(1).getRealPrice() > 14900);

  }

  @Test
  @DisplayName("최대 금액으로 필터링한다.")
  public void filterMaxPrice() {
    String searchText = "신발";
    saveProductWithPrice("나이키 신발", 10000);
    saveProductWithPrice("언더아머 신발", 15000);
    saveProductWithPrice("아디다스 신발", 20000);

    Page<ProductResponseDto> results = productQuerySlaveRepository
        .searchItems(searchText, FilterVo.builder().maxPrice(12000).build(), PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
    assertTrue(results.getContent().get(0).getRealPrice() < 12000);
  }

  @Test
  @DisplayName("오늘 업데이트된 상품으로 필터링한다.")
  public void filterTodayUpdatedData() {
    String searchText = "신발";
    saveProductWithDate("나이키 신발", LocalDateTime.now());
    saveProductWithDate("언더아머 신발", LocalDateTime.now().minusDays(1));
    saveProductWithDate("아디다스 신발", LocalDateTime.now().minusDays(2));

    Page<ProductResponseDto> results = productQuerySlaveRepository
        .searchItems(searchText, FilterVo.builder().onlyTodayUpdatedData(true).build(),
            PageRequest.of(0, 20));

    assertEquals(results.getTotalElements(), 1);
    assertTrue(results.getContent().get(0).getModifiedDate().isEqual(LocalDate.now()));
  }

  private void saveProductWithCategory(String productName, Category category) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .productName(productName)
        .category(category.getCategory())
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveProductWithBrand(String productName, String brand) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .productName(productName)
        .brand(brand)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveProductWithPrice(String productName, int price) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .productName(productName)
        .realPrice(price)
        .modifiedDate(LocalDateTime.now())
        .build());
  }

  private void saveProductWithDate(String productName, LocalDateTime localDateTime) {
    productSlaveRepository.save(Product.builder()
        .productId(productId++)
        .productName(productName)
        .modifiedDate(localDateTime)
        .build());
  }
}