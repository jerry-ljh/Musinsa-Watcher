package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.MapperUtils;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

  @Mock
  private ProductSlaveRepository productSlaveRepository;
  @Mock
  private ProductQuerySlaveRepository productQuerySlaveRepository;
  @Mock
  private CacheService cacheService;

  private static MockedStatic<DiscountedProductDto> mockDiscountedProductDto;
  private static MockedStatic<MinimumPriceProductDto> mockMinimumPriceProductDto;
  private static MockedStatic<MapperUtils> mockMapperUtils;


  @BeforeClass
  public static void beforeClass() {
    mockDiscountedProductDto = mockStatic(DiscountedProductDto.class);
    mockMinimumPriceProductDto = mockStatic(MinimumPriceProductDto.class);
    mockMapperUtils = mockStatic(MapperUtils.class);
  }

  @AfterClass
  public static void afterClass() {
    mockDiscountedProductDto.close();
    mockMinimumPriceProductDto.close();
    mockMapperUtils.close();
  }

  @Test
  @DisplayName("오늘 할인 폭이 큰 상품 조회")
  public void 오늘할인() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    LocalDate mockLocalDate = mock(LocalDate.class);
    List mockList = mock(List.class);
    Pageable pageable = mock(Pageable.class);
    long offset = 100L;
    int pagesize = 25;
    List<DiscountedProductDto> list = new ArrayList<>();
    String category = "001";
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(pageable.getOffset()).thenReturn(offset);
    when(pageable.getPageSize()).thenReturn(pagesize);
    when(productSlaveRepository.findDiscountedProduct(eq(category),
        eq(mockLocalDate), eq(offset), eq(pagesize))).thenReturn(mockList);
    when(productSlaveRepository.countDiscountedProduct(eq(category), eq(mockLocalDate)))
        .thenReturn(100L);
    when(DiscountedProductDto.objectsToDtoList(any())).thenReturn(list);
    //when
    productService.findDiscountedProduct(category, pageable);
    //then
    verify(cacheService, times(2)).getLastUpdatedDate();
    verify(productSlaveRepository, times(1)).countDiscountedProduct(eq(category), eq(mockLocalDate));
    verify(productSlaveRepository, times(1))
        .findDiscountedProduct(eq(category), eq(mockLocalDate), eq(offset), eq(pagesize));
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품 조회")
  public void 오늘최저가() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    LocalDateTime mockLocalDateTime = mock(LocalDateTime.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    List mockList = mock(List.class);
    Pageable pageable = mock(Pageable.class);
    long offset = 100L;
    int pagesize = 25;
    List<MinimumPriceProductDto> list = new ArrayList<>();
    String category = "001";
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(mockLocalDateTime.toLocalDate()).thenReturn(mockLocalDate);
    when(pageable.getOffset()).thenReturn(offset);
    when(pageable.getPageSize()).thenReturn(pagesize);
    when(productSlaveRepository.findProductByMinimumPrice(eq(category),
        eq(mockLocalDate), eq(offset), eq(pagesize))).thenReturn(mockList);
    when(productSlaveRepository.countMinimumPrice(eq(category), eq(mockLocalDate)))
        .thenReturn(100L);
    when(MinimumPriceProductDto.objectsToDtoList(eq(mockList))).thenReturn(list);
    //when
    productService.findMinimumPriceProduct(category, pageable);
    //then
    verify(cacheService, times(2)).getLastUpdatedDate();
    verify(productSlaveRepository, times(1)).countMinimumPrice(eq(category), eq(mockLocalDate));
    verify(productSlaveRepository, times(1))
        .findProductByMinimumPrice(eq(category), eq(mockLocalDate), eq(offset), eq(pagesize));
  }

  @Test
  @DisplayName("카테고리 상품 조회")
  public void 카테고리조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    String category = "001";
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(productQuerySlaveRepository.findByCategory(eq(category), eq(mockLocalDate), eq(pageable)))
        .thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findByCategory(category, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .findByCategory(eq(category), eq(mockLocalDate), eq(pageable));
  }

  @Test
  @DisplayName("브랜드 상품 조회")
  public void 브랜드상품조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    String category = "001";
    when(productQuerySlaveRepository.findByBrand(eq(category), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findByBrand(category, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .findByBrand(eq(category), eq(pageable));
  }

  @Test
  @DisplayName("검색 조회")
  public void 검색조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    String category = "001";
    when(productQuerySlaveRepository.searchItems(eq(category), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.searchItems(category, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .searchItems(eq(category), eq(pageable));
  }

  @Test
  @DisplayName("브랜드 이니셜 조회")
  public void 브랜드이니셜조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Initial initial = InitialWord.type1.getInitials();
    List<Object[]> list = mock(List.class);
    Map<String, Integer> map = mock(Map.class);
    when(productQuerySlaveRepository
        .findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND())))
        .thenReturn(list);
    when(MapperUtils.BingIntegerToIntegerMap(list)).thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService
        .findBrandByInitial(initial.getSTART(), initial.getEND());
    //then
    verify(productQuerySlaveRepository, times(1))
        .findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND()));
    assertEquals(resultMap, map);
  }

  @Test
  @DisplayName("오늘 할인 품목 수 조회")
  public void 오늘할인_품목_수_조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    LocalDate localDate = mock(LocalDate.class);
    List<Object[]> list = mock(List.class);
    Map<String, Integer> map = mock(Map.class);
    when(productSlaveRepository.countDiscountProductEachCategory(localDate)).thenReturn(list);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    when(MapperUtils.BingIntegerToIntegerMap(list)).thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService.countDiscountProductEachCategory();
    //then
    verify(productSlaveRepository, times(1)).countDiscountProductEachCategory(localDate);
    assertEquals(resultMap, map);
  }

  @Test
  @DisplayName("오늘 역대 최저가 품목 수 조회")
  public void 오늘_역대_최저가_품목_수_조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    LocalDate localDate = mock(LocalDate.class);
    List<Object[]> list = mock(List.class);
    Map<String, Integer> map = mock(Map.class);
    when(productSlaveRepository.countMinimumPriceProductEachCategory(localDate)).thenReturn(list);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    when(MapperUtils.BingIntegerToIntegerMap(list)).thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService.countMinimumPriceProductEachCategory();
    //then
    verify(productSlaveRepository, times(1)).countMinimumPriceProductEachCategory(localDate);
    assertEquals(resultMap, map);
  }

}