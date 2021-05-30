package com.musinsa.watcher.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.price.PriceRepository;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PriceServiceTest {

  @Mock
  private PriceRepository priceRepository;
  @Mock
  private ProductService productService;

  @Test
  @DisplayName("가격 데이터를 조회한다.")
  public void 가격을_조회한다() {
    PriceService priceService = new PriceService(priceRepository, productService);
    int product_id = 1;
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    List<PriceResponseDto> list = new ArrayList<>();
    when(priceRepository.findByProductId(eq(product_id), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    when(mockPage.getContent()).thenReturn(list);
    //when
    priceService.findByProductId(product_id, pageable);
    //then
    verify(priceRepository, times(1)).findByProductId(product_id, pageable);
  }

  @Test
  @DisplayName("오늘 할인 폭이 큰 상품 조회")
  public void 오늘할인() {
    //given
    PriceService priceService = new PriceService(priceRepository, productService);
    LocalDateTime localDateTime = LocalDateTime.now();
    Pageable pageable = mock(Pageable.class);
    String sort = "price_desc";
    Page<TodayDiscountedProductDto> results = mock(Page.class);
    Category category = Category.BAG;
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(localDateTime);
    when(priceRepository.findTodayDiscountProducts(eq(category),
        eq(localDateTime.toLocalDate()), eq(pageable), eq(sort))).thenReturn(results);
    //when
    priceService.findDiscountedProduct(category, pageable, sort);
    //then
    verify(productService, times(1)).getCachedLastUpdatedDateTime();

    verify(priceRepository, times(1))
        .findTodayDiscountProducts(eq(category), eq(localDateTime.toLocalDate()), eq(pageable), eq(sort));
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품 조회")
  public void 오늘최저가() {
    //given
    PriceService priceService = new PriceService(priceRepository, productService);
    LocalDateTime localDateTime = LocalDateTime.now();
    Pageable pageable = mock(Pageable.class);
    String sort = "price_desc";
    Page<TodayMinimumPriceProductDto> results = mock(Page.class);
    Category category = Category.SNEAKERS;
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(localDateTime);
    when(priceRepository.findTodayMinimumPriceProducts(eq(category),
        eq(localDateTime.toLocalDate()), eq(pageable), eq(sort))).thenReturn(results);
    //when
    priceService.findMinimumPriceProduct(category, pageable, sort);
    //then
    verify(productService, times(1)).getCachedLastUpdatedDateTime();
    verify(priceRepository, times(1))
        .findTodayMinimumPriceProducts(eq(category), eq(localDateTime.toLocalDate()), eq(pageable), eq(sort));
  }


  @Test
  @DisplayName("오늘 할인 품목 수를 조회한다")
  public void 오늘할인_품목_수_조회() {
    //given
    PriceService priceService = new PriceService(priceRepository, productService);
    LocalDateTime localDateTime = LocalDateTime.now();
    when(priceRepository.countDiscountProductEachCategory(localDateTime.toLocalDate())).thenReturn(mock(List.class));
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(localDateTime);
    //when
    ProductCountMapByCategoryDto resultMap = priceService.countDiscountProductEachCategory();
    //then
    verify(priceRepository, times(1)).countDiscountProductEachCategory(localDateTime.toLocalDate());
  }

  @Test
  @DisplayName("오늘 역대 최저가 품목 수를 조회한다")
  public void 오늘_역대_최저가_품목_수_조회() {
    //given
    PriceService priceService = new PriceService(priceRepository, productService);
    LocalDateTime localDateTime = LocalDateTime.now();
    Map<String, Integer> map = mock(Map.class);
    when(priceRepository.countMinimumPriceProductEachCategory(localDateTime.toLocalDate())).thenReturn(mock(List.class));
    when(productService.getCachedLastUpdatedDateTime()).thenReturn(localDateTime);
    //when
    ProductCountMapByCategoryDto resultMap = priceService.countMinimumPriceProductEachCategory();
    //then
    verify(priceRepository, times(1)).countMinimumPriceProductEachCategory(localDateTime.toLocalDate());
  }
}