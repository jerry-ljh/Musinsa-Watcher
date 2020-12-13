package com.musinsa.watcher.web;

import com.musinsa.watcher.service.PriceService;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
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
@WebMvcTest(controllers = PriceController.class)
public class PriceControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private PriceService priceService;

  private final String API = "/api/v1/product/";

  @Test
  @DisplayName("일자별 상품 상세정보 조회")
  public void 상품_상세정보_조회() throws Exception {
    int productId = 1234;
    Page<PriceResponseDto> mockPage = mock(Page.class);
    when(priceService.findByProductId(eq(productId), any())).thenReturn(mockPage);
    mvc.perform(get(API + "price")
        .param("id", Integer.toString(productId)))
        .andExpect(status().isOk());
    verify(priceService, times(1)).findByProductId(eq(productId), any());
  }
}