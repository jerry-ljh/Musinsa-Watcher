package com.musinsa.watcher.web;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = SearchController.class)
public class SearchControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private ProductService mockProductService;

  private final String API = "/api/v1/search/";

  @Test
  @DisplayName("브랜드를 초성으로 조회한다.")
  public void searchBrandByInitial() throws Exception {
    String type = "1";
    InitialWord initialWord = InitialWord.valueOf(InitialWord.getType(type));

    mvc.perform(get(API + "brand-initial")
        .param("type", type))
        .andExpect(status().isOk());

    verify(mockProductService, only())
        .searchBrandByInitial(eq(initialWord.getStart()), eq(initialWord.getEnd()));
  }


  @Test
  @DisplayName("검색 품목 조회한다.")
  public void searchProduct() throws Exception {
    String topic = "셔츠";

    mvc.perform(get(API + "product")
        .param("topic", topic))
        .andExpect(status().isOk());

    verify(mockProductService, only()).searchProduct(eq(topic), any(), any());
  }

}