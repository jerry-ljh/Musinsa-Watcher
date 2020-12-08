package com.musinsa.watcher.domain.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  private static MockedStatic<DiscountedProductDto> mockDiscountedProductDto;

  @BeforeClass
  public static void beforeClass() {
    mockDiscountedProductDto = mockStatic(DiscountedProductDto.class);
  }

  @AfterClass
  public static void afterClass() {
    mockDiscountedProductDto.close();
  }

  @Test
  @DisplayName("오늘 할인 폭이 큰 상품 조회")
  public void 오늘할인() {
    ProductService productService = new ProductService(productRepository);
    LocalDateTime mockLocalDateTime = mock(LocalDateTime.class);
    Page mockPage = mock(Page.class);
    Pageable pageable = mock(Pageable.class);
    List<DiscountedProductDto> list = new ArrayList<>();
    String category = "001";
    when(productRepository.findLastUpdateDate()).thenReturn(mockLocalDateTime);
    when(productRepository.findDiscountedProduct(eq(category), eq(mockLocalDateTime), any()))
        .thenReturn(mockPage);
    when(DiscountedProductDto.objectsToDtoList(any())).thenReturn(list);
    when(mockPage.getContent()).thenReturn(list);
    when(mockPage.getTotalElements()).thenReturn(100L);

    productService.findDiscountedProduct(category, pageable);

    verify(productRepository, times(1)).findLastUpdateDate();
    verify(productRepository, times(1))
        .findDiscountedProduct(eq(category), eq(mockLocalDateTime), any());
  }
}