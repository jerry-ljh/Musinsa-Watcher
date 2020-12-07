package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.service.ProductService;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private ProductService mockProductService;

  private final String API = "/api/v1/product/";

  @Test
  @DisplayName("브랜드 리스트 조회")
  public void 브랜드_리스트_조회() throws Exception {
    Page<String> mockPage = mock(Page.class);
    when(mockProductService.findAllBrand(any())).thenReturn(mockPage);
    mvc.perform(get("/api/v1/search/brands/list"))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findAllBrand(any());
  }

  @Test
  @DisplayName("브랜드 상품 리스트 조회")
  public void 브랜드_상품_리스트_조회() throws Exception {
    String brandName = "good brand";
    Page<ProductResponseDto> mockPage = mock(Page.class);
    when(mockProductService.findByBrand(eq(brandName), any())).thenReturn(mockPage);
    mvc.perform(get(API + "brand")
        .param("name", brandName))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findByBrand(eq(brandName), any());
  }

  @Test
  @DisplayName("카테고리별 상품 리스트 조회")
  public void 카테고리별_상품_리스트_조회() throws Exception {
    String category1 = "001";
    String category2 = "002";
    Page<ProductResponseDto> mockPage1 = mock(Page.class);
    Page<ProductResponseDto> mockPage2 = mock(Page.class);
    when(mockProductService.findByCategory(eq(category1), any())).thenReturn(mockPage1);
    when(mockProductService.findByCategory(eq(category2), any())).thenReturn(mockPage2);
    mvc.perform(get(API + "list")
        .param("category", category1))
        .andExpect(status().isOk());
    mvc.perform(get(API + "list")
        .param("category", category2))
        .andExpect(status().isOk());
    verify(mockProductService, times(1)).findByCategory(eq(category1), any());
    verify(mockProductService, times(1)).findByCategory(eq(category2), any());
  }
}