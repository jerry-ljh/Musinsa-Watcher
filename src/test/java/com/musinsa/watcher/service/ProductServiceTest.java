package com.musinsa.watcher.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.watcher.web.Filter;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDate;
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
  private ProductSlaveRepository productSlaveRepository;
  @Mock
  private ProductQuerySlaveRepository productQuerySlaveRepository;
  @Mock
  private CacheService cacheService;

  @Test
  @DisplayName("카테고리 상품 조회")
  public void 카테고리조회() {
    //given
    ProductService productService = new ProductService(productSlaveRepository,
        productQuerySlaveRepository,
        cacheService);
    Page<ProductResponseDto> mockPage = mock(Page.class);
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    LocalDate mockLocalDate = mock(LocalDate.class);
    when(cacheService.getLastUpdatedDate()).thenReturn(mockLocalDate);
    when(productQuerySlaveRepository
        .findByCategoryAndDate(eq(filter), eq(mockLocalDate), eq(pageable)))
        .thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.findByCategory(filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .findByCategoryAndDate(eq(filter), eq(mockLocalDate), eq(pageable));
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
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    when(productQuerySlaveRepository.findByBrand(eq(filter), eq(pageable))).thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.findByBrand(filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1)).findByBrand(eq(filter), eq(pageable));
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
    Filter filter = mock(Filter.class);
    Pageable pageable = mock(Pageable.class);
    String topic = "001";
    when(productQuerySlaveRepository.searchItems(eq(topic), eq(filter), eq(pageable)))
        .thenReturn(mockPage);
    //when
    Page<ProductResponseDto> page = productService.searchItems(topic, filter, pageable);
    //then
    verify(productQuerySlaveRepository, times(1))
        .searchItems(eq(topic), eq(filter), eq(pageable));
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
    Map<String, Integer> map = mock(Map.class);
    when(productQuerySlaveRepository
        .findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND())))
        .thenReturn(map);
    //when
    Map<String, Integer> resultMap = productService
        .findBrandByInitial(initial.getSTART(), initial.getEND());
    //then
    verify(productQuerySlaveRepository, times(1))
        .findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND()));
    assertEquals(resultMap, map);
  }

}