package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
  @DisplayName("브랜드 상품 리스트를 조회한다.")
  public void findProductByBrand() throws Exception {
    String brandName = "good brand";

    mvc.perform(get(API + "brand")
        .param("brand", brandName)
        .param("onlyTodayUpdatedData", "true"))
        .andExpect(status().isOk());

    verify(mockProductService, only()).findProductsPageByBrand(any(), any());
  }

  @Test
  @DisplayName("여러 브랜드를 동시에 조회한다.")
  public void findProductByBrands() throws Exception {
    FilterVo filterVo = FilterVo.builder()
        .brands(new String[]{"나이키", "아디다스", "언더아머"})
        .build();

    mvc.perform(get(API + "brand")
        .param("brand", "나이키, 아디다스, 언더아머"))
        .andExpect(status().isOk());

    verify(mockProductService, only()).findProductsPageByBrand(eq(filterVo), any());
  }

  @Test
  @DisplayName("카테고리 상품 리스트 조회한다.")
  public void findProductByCategory() throws Exception {
    for (Category category : Category.values()) {
      mvc.perform(get(API + "list")
          .param("category", category.getCategory())
          .param("minprice", "0")
          .param("maxprice", "10000"))
          .andExpect(status().isOk());
    }
    verify(mockProductService, times(Category.values().length))
        .findProductsPageByCategory(any(), any());
  }

  @Test
  @DisplayName("여러 카테고리를 동시에 조회한다.")
  public void findProductByCategorys() throws Exception {
    FilterVo filterVo = FilterVo.builder()
        .categories(new Category[]{Category.TOP, Category.OUTER, Category.PANTS})
        .build();

    mvc.perform(get(API + "list")
        .param("category", "001, 002, 003"))
        .andExpect(status().isOk());

    verify(mockProductService, only()).findProductsPageByCategory(eq(filterVo), any());
  }

  @Test
  @DisplayName("품목 상세 정보를 조회한다.")
  public void findProductDetail() throws Exception {
    int productId = 1;

    mvc.perform(get(API)
        .param("id", Integer.toString(productId)))
        .andExpect(status().isOk());

    verify(mockProductService, only()).findProductWithPrice(eq(productId));
  }
}