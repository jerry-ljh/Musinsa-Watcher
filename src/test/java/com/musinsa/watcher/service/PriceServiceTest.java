package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.price.slave.PriceSlaveQueryRepository;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import java.time.LocalDate;
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
  private PriceSlaveRepository priceSlaveRepository;
  @Mock
  private PriceSlaveQueryRepository priceSlaveQueryRepository;
  @Mock
  private CacheService cacheService;

  @Test
  @DisplayName("가격 데이터를 조회한다.")
  public void 가격을_조회한다() {
    PriceService priceService = new PriceService(priceSlaveRepository, priceSlaveQueryRepository,
        cacheService);
    int product_id = 1;
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    List<PriceResponseDto> list = new ArrayList<>();
    when(priceSlaveRepository.findByProductId(eq(product_id), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    when(mockPage.getContent()).thenReturn(list);
    //when
    priceService.findByProductId(product_id, pageable);
    //then
    verify(priceSlaveRepository, times(1)).findByProductId(product_id, pageable);
  }

  @Test
  @DisplayName("오늘 할인 폭이 큰 상품 조회")
  public void 오늘할인() {
    //given
    PriceService priceService = new PriceService(priceSlaveRepository, priceSlaveQueryRepository,
        cacheService);
    LocalDate mockLocalDate = mock(LocalDate.class);
    Pageable pageable = mock(Pageable.class);
    String sort = "price_desc";
    Page<DiscountedProductDto> results = mock(Page.class);
    Category category = Category.BAG;
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(priceSlaveQueryRepository.findTodayDiscountProducts(eq(category),
        eq(mockLocalDate), eq(pageable), eq(sort))).thenReturn(results);
    //when
    priceService.findDiscountedProduct(category, pageable, sort);
    //then
    verify(cacheService, times(1)).getLastUpdatedDate();

    verify(priceSlaveQueryRepository, times(1))
        .findTodayDiscountProducts(eq(category), eq(mockLocalDate), eq(pageable), eq(sort));
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품 조회")
  public void 오늘최저가() {
    //given
    PriceService priceService = new PriceService(priceSlaveRepository, priceSlaveQueryRepository,
        cacheService);
    LocalDateTime mockLocalDateTime = mock(LocalDateTime.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    Pageable pageable = mock(Pageable.class);
    String sort = "price_desc";
    Page<MinimumPriceProductDto> results = mock(Page.class);
    Category category = Category.SNEAKERS;
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(mockLocalDateTime.toLocalDate()).thenReturn(mockLocalDate);
    when(priceSlaveQueryRepository.findTodayMinimumPriceProducts(eq(category),
        eq(mockLocalDate), eq(pageable), eq(sort))).thenReturn(results);
    //when
    priceService.findMinimumPriceProduct(category, pageable, sort);
    //then
    verify(cacheService, times(1)).getLastUpdatedDate();
    verify(priceSlaveQueryRepository, times(1))
        .findTodayMinimumPriceProducts(eq(category), eq(mockLocalDate), eq(pageable), eq(sort));
  }


  @Test
  @DisplayName("오늘 할인 품목 수를 조회한다")
  public void 오늘할인_품목_수_조회() {
    //given
    PriceService priceService = new PriceService(priceSlaveRepository, priceSlaveQueryRepository,
        cacheService);
    LocalDate localDate = mock(LocalDate.class);
    List<Object[]> list = mock(List.class);
    Map<String, Integer> map = mock(Map.class);
    when(priceSlaveQueryRepository.countDiscountProductEachCategory(localDate)).thenReturn(map);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    //when
    Map<String, Integer> resultMap = priceService.countDiscountProductEachCategory();
    //then
    verify(priceSlaveQueryRepository, times(1)).countDiscountProductEachCategory(localDate);
    assertEquals(resultMap, map);
  }

  @Test
  @DisplayName("오늘 역대 최저가 품목 수를 조회한다")
  public void 오늘_역대_최저가_품목_수_조회() {
    //given
    PriceService priceService = new PriceService(priceSlaveRepository, priceSlaveQueryRepository,
        cacheService);
    LocalDate localDate = mock(LocalDate.class);
    Map<String, Integer> map = mock(Map.class);
    when(priceSlaveQueryRepository.countMinimumPriceProductEachCategory(localDate)).thenReturn(map);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    //when
    Map<String, Integer> resultMap = priceService.countMinimumPriceProductEachCategory();
    //then
    verify(priceSlaveQueryRepository, times(1)).countMinimumPriceProductEachCategory(localDate);
    assertEquals(resultMap, map);
  }
}