package com.musinsa.watcher.web;

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
        .param("brand", brandName))
        .andExpect(status().isOk());

    verify(mockProductService, only()).findProductsPageByBrand(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("브랜드 상품 리스트를 조회할 때 brand 파라미터가 없으면 예외가 발생한다.")
  public void throwValidParameterException1() throws Exception {
    mvc.perform(get(API + "brand"))
        .andExpect(status().isOk());
  }

  @Test(expected = Exception.class)
  @DisplayName("브랜드 상품 리스트를 조회할 때 brand 파라미터가 있어도 값이 없다면 예외가 발생한다.")
  public void throwValidParameterException2() throws Exception {
    mvc.perform(get(API + "brand")
        .param("brand", ""))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("카테고리 상품 리스트 조회한다.")
  public void findProductByCategory() throws Exception {
    for (Category category : Category.values()) {
      mvc.perform(get(API + "list")
          .param("category", category.getCategory()))
          .andExpect(status().isOk());
    }
    verify(mockProductService, times(Category.values().length)).findProductsPageByCategory(any(), any());
  }

  @Test(expected = Exception.class)
  @DisplayName("카테고리 상품 리스트를 조회할 때 카테고리 파라미터가 없으면 오류가 발생한다.")
  public void throwValidParameterException3() throws Exception {
    mvc.perform(get(API + "list"))
        .andExpect(status().isOk());
  }

  @Test(expected = Exception.class)
  @DisplayName("카테고리 상품 리스트를 조회할 때 카테고리 파라미터가 있어도 값이 없다면 오류가 발생한다.")
  public void throwValidParameterException4() throws Exception {
    mvc.perform(get(API + "list")
        .param("category", ""))
        .andExpect(status().isOk());
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