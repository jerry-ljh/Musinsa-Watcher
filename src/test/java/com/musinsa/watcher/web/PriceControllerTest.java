package com.musinsa.watcher.web;

import com.musinsa.watcher.config.LogInterceptor;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.service.PriceService;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PriceController.class)
public class PriceControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private PriceService priceService;

  @MockBean
  private LogInterceptor logInterceptor;

  private final String API = "/api/v1/product/";

  @Test
  @DisplayName("일자별 상품 상세정보를 조회한다")
  public void 상품_상세정보_조회() throws Exception {
    int productId = 1234;
    Page<PriceResponseDto> mockPage = mock(Page.class);
    when(logInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    when(priceService.findByProductId(eq(productId), any())).thenReturn(mockPage);
    mvc.perform(get(API + "price")
        .param("id", Integer.toString(productId)))
        .andExpect(status().isOk());
    verify(priceService, times(1)).findByProductId(eq(productId), any());
  }

  @Test
  @DisplayName("오늘 할인 품목을 조회한다")
  public void 오늘할인_품목_조회() throws Exception {
    Category category = Category.TOP;
    when(logInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    String[] sorts = new String[]{"percent_desc", "percent_asc", "price_asc", "price_desc"};
    for (String sort : sorts) {
      when(priceService.findDiscountedProduct(eq(category), any(), eq(sort.replace("_", " "))))
          .thenReturn(mock(Page.class));
      mvc.perform(get(API + "discount")
          .param("category", category.getCategory())
          .param("sort", sort))
          .andExpect(status().isOk());
      verify(priceService, times(1))
          .findDiscountedProduct(eq(category), any(), eq(sort.replace("_", " ")));
    }
  }

  @Test
  @DisplayName("오늘 역대 최저가 품목을 조회한다")
  public void 오늘역대최저가_품목_조회() throws Exception {
    Category category = Category.TOP;
    when(logInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    String[] sorts = new String[]{"percent_desc", "percent_asc", "price_asc", "price_desc"};
    for (String sort : sorts) {
      when(priceService.findMinimumPriceProduct(eq(category), any(), eq(sort.replace("_", " "))))
          .thenReturn(mock(Page.class));
      mvc.perform(get(API + "minimum")
          .param("category", category.getCategory())
          .param("sort", sort))
          .andExpect(status().isOk());
      verify(priceService, times(1))
          .findMinimumPriceProduct(eq(category), any(), eq(sort.replace("_", " ")));
    }
  }
}