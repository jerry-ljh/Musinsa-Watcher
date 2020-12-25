package com.musinsa.watcher.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  private ProductRepository productRepository;

  private static MockedStatic<DiscountedProductDto> mockDiscountedProductDto;
  private static MockedStatic<MinimumPriceProductDto> mockMinimumPriceProductDto;

  @BeforeClass
  public static void beforeClass() {
    mockDiscountedProductDto = mockStatic(DiscountedProductDto.class);
    mockMinimumPriceProductDto = mockStatic(MinimumPriceProductDto.class);
  }

  @AfterClass
  public static void afterClass() {
    mockDiscountedProductDto.close();
    mockMinimumPriceProductDto.close();
  }

  @Test
  @DisplayName("오늘 할인 폭이 큰 상품 조회")
  public void 오늘할인() {
    //given
    ProductService productService = new ProductService(productRepository);
    LocalDateTime mockLocalDateTime = mock(LocalDateTime.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    List<DiscountedProductDto> list = new ArrayList<>();
    String category = "001";
    when(productRepository.findLastUpdateDate()).thenReturn(mockLocalDateTime);
    when(mockLocalDateTime.toLocalDate()).thenReturn(mockLocalDate);
    when(productRepository.findDiscountedProduct(eq(category), eq(mockLocalDate), any()))
        .thenReturn(mockPage);
    when(DiscountedProductDto.objectsToDtoList(any())).thenReturn(list);
    when(mockPage.getContent()).thenReturn(list);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findDiscountedProduct(category, pageable);
    //then
    verify(productRepository, times(1)).findLastUpdateDate();
    verify(productRepository, times(1))
        .findDiscountedProduct(eq(category), eq(mockLocalDate), any());
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품 조회")
  public void 오늘최저가() {
    //given
    ProductService productService = new ProductService(productRepository);
    LocalDateTime mockLocalDateTime = mock(LocalDateTime.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    List<MinimumPriceProductDto> list = new ArrayList<>();
    String category = "001";
    when(productRepository.findLastUpdateDate()).thenReturn(mockLocalDateTime);
    when(mockLocalDateTime.toLocalDate()).thenReturn(mockLocalDate);
    when(productRepository.findProductByMinimumPrice(eq(category), eq(mockLocalDate), any()))
        .thenReturn(mockPage);
    when(MinimumPriceProductDto.objectsToDtoList(any())).thenReturn(list);
    when(mockPage.getContent()).thenReturn(list);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findMinimumPriceProduct(category, pageable);
    //then
    verify(productRepository, times(1)).findLastUpdateDate();
    verify(productRepository, times(1))
        .findProductByMinimumPrice(eq(category), eq(mockLocalDate), any());
  }

  @Test
  @DisplayName("카테고리 상품 조회")
  public void 카테고리조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    String category = "001";
    when(productRepository.findByCategory(eq(category), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findByCategory(category, pageable);
    //then
    verify(productRepository, times(1))
        .findByCategory(eq(category), eq(pageable));
  }

  @Test
  @DisplayName("브랜드 상품 조회")
  public void 브랜드상품조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    String category = "001";
    when(productRepository.findByBrand(eq(category), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.findByBrand(category, pageable);
    //then
    verify(productRepository, times(1))
        .findByBrand(eq(category), eq(pageable));
  }

  @Test
  @DisplayName("검색 조회")
  public void 검색조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    String category = "001";
    when(productRepository.searchItems(eq(category), eq(pageable))).thenReturn(mockPage);
    when(mockPage.getTotalElements()).thenReturn(100L);
    //when
    productService.searchItems(category, pageable);
    //then
    verify(productRepository, times(1))
        .searchItems(eq(category), eq(pageable));
  }
}