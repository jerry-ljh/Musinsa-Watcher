package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.price.dto.ProductCountByBrandDto;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.Filter;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.web.dto.ProductCountMapByBrandDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Test
  @DisplayName("카테고리 상품 조회")
  public void 카테고리조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    LocalDateTime localDateTime = LocalDateTime.now();
    when(productRepository.findCachedLastUpdatedDateTime()).thenReturn(localDateTime);
    when(productRepository
        .findByCategoryAndDate(eq(filter), eq(localDateTime.toLocalDate()), eq(pageable)))
        .thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.findByCategory(filter, pageable);
    //then
    verify(productRepository, times(1))
        .findByCategoryAndDate(eq(filter), eq(localDateTime.toLocalDate()), eq(pageable));
    assertEquals(page, mockPage);
  }

  @Test
  @DisplayName("브랜드 상품 조회")
  public void 브랜드상품조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    when(productRepository.findByBrand(eq(filter), eq(pageable))).thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.findByBrand(filter, pageable);
    //then
    verify(productRepository, times(1)).findByBrand(eq(filter), eq(pageable));
    assertEquals(page, mockPage);

  }

  @Test
  @DisplayName("검색 조회")
  public void 검색조회() {
    //given
    ProductService productService = new ProductService(productRepository);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    String topic = "001";
    when(productRepository.searchItems(eq(topic), eq(filter), eq(pageable)))
        .thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.searchItems(topic, filter, pageable);
    //then
    verify(productRepository, times(1))
        .searchItems(eq(topic), eq(filter), eq(pageable));
    assertEquals(page, mockPage);
  }

}