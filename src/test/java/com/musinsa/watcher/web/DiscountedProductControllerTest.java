package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.service.DiscountedProductService;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = DiscountedProductController.class)
public class DiscountedProductControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private DiscountedProductService mockDiscountedProductService;

  private final String API = "/api/v1/product/";

  @Test
  @DisplayName("오늘 할인 상품을 조회한다.")
  public void findTodayDiscountedProduct() throws Exception {
    mvc.perform(get(API + "discount/today/list")
        .param("category", "001")
        .param("sort", "price_asc"))
        .andExpect(status().isOk());

    verify(mockDiscountedProductService, only())
        .findDiscountedProduct(eq(Category.getCategory("001")), any());
  }

  @Test
  @DisplayName("오늘 역대 최저가 상품을 조회한다.")
  public void findTodayMinimumPriceProduct() throws Exception {
    mvc.perform(get(API + "minimum-price/today/list")
        .param("category", "001")
        .param("sort", "percent_asc"))
        .andExpect(status().isOk());

    verify(mockDiscountedProductService, only())
        .findMinimumPriceProduct(eq(Category.getCategory("001")), any());
  }
}

