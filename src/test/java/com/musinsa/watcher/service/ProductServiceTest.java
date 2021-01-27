package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.web.Filter;
import com.musinsa.watcher.MapperUtils;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
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
  private static MockedStatic<ProductResponseDto> mockProductResponseDto;
  private static MockedStatic<MapperUtils> mockMapperUtils;


  @BeforeClass
  public static void beforeClass() {
    mockDiscountedProductDto = mockStatic(DiscountedProductDto.class);
    mockMinimumPriceProductDto = mockStatic(MinimumPriceProductDto.class);
    mockMapperUtils = mockStatic(MapperUtils.class);
    mockProductResponseDto = mockStatic(ProductResponseDto.class);
  }

  @AfterClass
  public static void afterClass() {
    mockDiscountedProductDto.close();
    mockMinimumPriceProductDto.close();
    mockProductResponseDto.close();
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
    String sort = "price_desc";
    long offset = 100L;
    int pagesize = 25;
    List<DiscountedProductDto> list = new ArrayList<>();
    String category = "001";
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(pageable.getOffset()).thenReturn(offset);
    when(pageable.getPageSize()).thenReturn(pagesize);
    when(productQuerySlaveRepository.findDiscountByCategoryAndDate(eq(category),
        eq(mockLocalDate), eq(offset), eq(pagesize), eq(sort))).thenReturn(mockList);
    when(productSlaveRepository.countDiscountByCategoryAndDate(eq(category), eq(mockLocalDate)))
        .thenReturn(100L);
    when(DiscountedProductDto.convertList(any())).thenReturn(list);
    //when
    productService.findDiscountedProduct(category, pageable, sort);
    //then
    verify(cacheService, times(2)).getLastUpdatedDate();
    verify(productSlaveRepository, times(1))
        .countDiscountByCategoryAndDate(eq(category), eq(mockLocalDate));
    verify(productQuerySlaveRepository, times(1))
        .findDiscountByCategoryAndDate(eq(category), eq(mockLocalDate), eq(offset), eq(pagesize),
            eq(sort));
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
    String sort = "price_desc";
    long offset = 100L;
    int pagesize = 25;
    List<MinimumPriceProductDto> list = new ArrayList<>();
    String category = "001";
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(mockLocalDateTime.toLocalDate()).thenReturn(mockLocalDate);
    when(pageable.getOffset()).thenReturn(offset);
    when(pageable.getPageSize()).thenReturn(pagesize);
    when(productQuerySlaveRepository.findMinimumByCategoryAndDate(eq(category),
        eq(mockLocalDate), eq(offset), eq(pagesize), eq(sort))).thenReturn(mockList);
    when(productSlaveRepository.countMinimumByCategoryAndDate(eq(category), eq(mockLocalDate)))
        .thenReturn(100L);
    when(MinimumPriceProductDto.convertList(eq(mockList))).thenReturn(list);
    //when
    productService.findMinimumPriceProduct(category, pageable, sort);
    //then
    verify(cacheService, times(2)).getLastUpdatedDate();
    verify(productSlaveRepository, times(1))
        .countMinimumByCategoryAndDate(eq(category), eq(mockLocalDate));
    verify(productQuerySlaveRepository, times(1))
        .findMinimumByCategoryAndDate(eq(category), eq(mockLocalDate), eq(offset), eq(pagesize),
            eq(sort));
  }

  @Test
  @DisplayName("카테고리 상품 조회")
  public void 카테고리조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    List mockList = mock(List.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(productQuerySlaveRepository
        .findByCategoryAndDate(eq(filter), eq(mockLocalDate), eq(pageable)))
        .thenReturn(mockList);
    when(productQuerySlaveRepository.countByCategoryAndDate(eq(filter), eq(mockLocalDate)))
        .thenReturn(100L);
    when(ProductResponseDto.convertPage(eq(mockList), eq(pageable), eq(100L))).thenReturn(mockPage);
    when(mockList.size()).thenReturn(100);
    //when
    Page<ProductResponseDto> page = productService.findByCategory(filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .findByCategoryAndDate(eq(filter), eq(mockLocalDate), eq(pageable));
    verify(productQuerySlaveRepository, times(1))
        .countByCategoryAndDate(eq(filter), eq(mockLocalDate));
    assertEquals(page, mockPage);
  }

  @Test
  @DisplayName("브랜드 상품 조회")
  public void 브랜드상품조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    List mockList = mock(List.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    when(productQuerySlaveRepository.findByBrand(eq(filter), eq(pageable))).thenReturn(mockList);
    when(productQuerySlaveRepository.countByBrand(eq(filter))).thenReturn(100L);
    when(ProductResponseDto.convertPage(eq(mockList), eq(pageable), eq(100L))).thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.findByBrand(filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1)).findByBrand(eq(filter), eq(pageable));
    verify(productQuerySlaveRepository, times(1)).countByBrand(eq(filter));
    assertEquals(page, mockPage);

  }

  @Test
  @DisplayName("검색 조회")
  public void 검색조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    List mockList = mock(List.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    String topic = "001";
    when(productQuerySlaveRepository.searchItems(eq(topic), eq(filter), eq(pageable)))
        .thenReturn(mockList);
    when(productQuerySlaveRepository.countSearchItems(eq(topic), eq(filter))).thenReturn(100L);
    when(ProductResponseDto.convertPage(eq(mockList), eq(pageable), eq(100L))).thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.searchItems(topic, filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .searchItems(eq(topic), eq(filter), eq(pageable));
    verify(productQuerySlaveRepository, times(1))
        .countSearchItems(eq(topic), eq(filter));
    assertEquals(page, mockPage);

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
    when(MapperUtils.BigIntegerToIntegerMap(list)).thenReturn(map);
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
    when(productSlaveRepository.countDiscountEachCategory(localDate)).thenReturn(list);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    when(MapperUtils.BigIntegerToIntegerMap(list)).thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService.countDiscountProductEachCategory();
    //then
    verify(productSlaveRepository, times(1)).countDiscountEachCategory(localDate);
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
    when(productSlaveRepository.countMinimumEachCategory(localDate)).thenReturn(list);
    when(cacheService.getLastUpdatedDate()).thenReturn(localDate);
    when(MapperUtils.BigIntegerToIntegerMap(list)).thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService.countMinimumPriceProductEachCategory();
    //then
    verify(productSlaveRepository, times(1)).countMinimumEachCategory(localDate);
    assertEquals(resultMap, map);
  }

}