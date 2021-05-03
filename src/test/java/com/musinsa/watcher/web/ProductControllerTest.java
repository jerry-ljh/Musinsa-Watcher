package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.HashMap;
import java.util.Map;
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
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private ProductService mockProductService;

  private final String API = "/api/v1/product/";

  @Test
  @DisplayName("브랜드 상품 리스트 조회한다.")
  public void 브랜드_상품_리스트_조회() throws Exception {
    String brandName = "good brand";
    Page<ProductResponseDto> mockPage = mock(Page.class);
    when(mockProductService.findByBrand(any(), any())).thenReturn(mockPage);
    mvc.perform(get(API + "brand")
        .param("brand", brandName))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findByBrand(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("브랜드 상품 리스트를 조회할 때 brand 파라미터가 없으면 예외가 발생한다.")
  public void 브랜드_상품_리스트_조회_예외1() throws Exception {
    Page<ProductResponseDto> mockPage = mock(Page.class);
    when(mockProductService.findByBrand(any(), any())).thenReturn(mockPage);
    mvc.perform(get(API + "brand"))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findByBrand(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("브랜드 상품 리스트를 조회할 때 brand 파라미터가 있어도 값이 없다면 예외가 발생한다.")
  public void 브랜드_상품_리스트_조회_예외2() throws Exception {
    String brandName = "good brand";
    Page<ProductResponseDto> mockPage = mock(Page.class);
    when(mockProductService.findByBrand(any(), any())).thenReturn(mockPage);
    mvc.perform(get(API + "brand")
        .param("brand", ""))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findByBrand(any(), any());
  }

  @Test
  @DisplayName("브랜드 초성 조회한다.")
  public void 브랜드_초성_조회() throws Exception {
    String type = "1";
    Map<String, Integer> map = new HashMap<>();
    Initial initial = InitialWord.valueOf(InitialWord.getType(type)).getInitials();
    when(mockProductService.findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND())))
        .thenReturn(map);
    mvc.perform(get("/api/v1/search/brands")
        .param("type", type))
        .andExpect(status().isOk());
    verify(mockProductService, only())
        .findBrandByInitial(eq(initial.getSTART()), eq(initial.getEND()));
  }

  @Test
  @DisplayName("카테고리 상품 리스트 조회한다.")
  public void 카테고리별_상품_리스트_조회() throws Exception {
    for (Category category : Category.values()) {
      when(mockProductService.findByCategory(any(), any())).thenReturn(mock(Page.class));
      mvc.perform(get(API + "list")
          .param("category", category.getCategory())
          .param("maxprice", "10000"))
          .andExpect(status().isOk());
    }
    verify(mockProductService, times(Category.values().length)).findByCategory(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("카테고리 상품 리스트를 조회할 때 카테고리 파라미터가 없으면 오류가 발생한다.")
  public void 카테고리별_상품_리스트_조회_예외1() throws Exception {
    Page<ProductResponseDto> mockPage = mock(Page.class);

    when(mockProductService.findByCategory(any(), any())).thenReturn(mockPage);
    mvc.perform(get(API + "list"))
        .andExpect(status().isOk());

    verify(mockProductService, times(1)).findByCategory(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("카테고리 상품 리스트를 조회할 때 카테고리 파라미터가 있어도 값이 없다면 오류가 발생한다.")
  public void 카테고리별_상품_리스트_조회_예외2() throws Exception {
    Page<ProductResponseDto> mockPage = mock(Page.class);

    when(mockProductService.findByCategory(any(), any())).thenReturn(mockPage);
    mvc.perform(get(API + "list")
        .param("category", ""))
        .andExpect(status().isOk());

    verify(mockProductService, times(1)).findByCategory(any(), any());
  }

  @Test
  @DisplayName("검색 품목 조회한다.")
  public void 검색_품목_조회() throws Exception {
    String topic = "셔츠";
    Page<ProductResponseDto> mockPage = mock(Page.class);
    when(mockProductService.searchItems(eq(topic), any(), any())).thenReturn(mockPage);
    mvc.perform(get("/api/v1/search")
        .param("topic", topic))
        .andExpect(status().isOk());
    verify(mockProductService, only()).searchItems(eq(topic), any(), any());
  }

  @Test
  @DisplayName("품목 상세 정보 조회한다.")
  public void 품목_상세_정보_조회() throws Exception {
    int productId = 1;
    ProductWithPriceResponseDto mockDto = mock(ProductWithPriceResponseDto.class);
    when(mockProductService.findProductWithPrice(eq(productId))).thenReturn(mockDto);
    mvc.perform(get(API)
        .param("id", Integer.toString(productId)))
        .andExpect(status().isOk());
    verify(mockProductService, only()).findProductWithPrice(eq(productId));
  }
}