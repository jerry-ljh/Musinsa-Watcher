package com.musinsa.watcher.config;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.log.AccessLog;
import com.musinsa.watcher.domain.log.master.AccessLogRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogInterceptorTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private AccessLogRepository accessLogRepository;

  private MockMvc mockMvc;

  @Before
  public void init() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @After
  public void clear() {
    accessLogRepository.deleteAll();
  }

  @Test
  @DisplayName("무신사 사이트로 유입되는 log기록")
  public void 무신사로_유입된다() throws Exception {
    //given
    String OUTBOUND_URI = "/api/product/link";
    //when
    mockMvc.perform(get(OUTBOUND_URI).header("User-Agent", "*"))
        .andExpect(status().isOk());
    //then
    List<AccessLog> accessLogList = accessLogRepository.findAll();
    AccessLog accessLog = accessLogList.get(0);
    assertEquals(accessLog.getUrl(), OUTBOUND_URI);
  }

  @Test
  @DisplayName("요청 log 저장")
  public void request_log가_저장된다() throws Exception {
    //given
    String[] urlArr = new String[]{
        "/api/product/link",
        "/api/v1/search/brands/list",
        "/api/profile"
    };
    //when
    for (String url : urlArr) {
      mockMvc.perform(get(url).header("User-Agent", "*"))
          .andExpect(status().isOk());
    }
    //then
    List<AccessLog> accessLogList = accessLogRepository.findAll();
    assertEquals(accessLogList.size(), urlArr.length);
  }
}