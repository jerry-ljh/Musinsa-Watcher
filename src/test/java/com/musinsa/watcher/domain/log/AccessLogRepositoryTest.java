package com.musinsa.watcher.domain.log;

import static org.junit.Assert.*;

import com.musinsa.watcher.domain.log.master.AccessLogRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AccessLogRepositoryTest {

  @Autowired
  private AccessLogRepository accessLogRepository;

  @Test
  @DisplayName("log 저장")
  public void save() {
    String ip = "15.123.116.10";
    String url = "/api/test";
    String parameter = "parameter";
    String agent = "iphone";
    AccessLog accessLog = AccessLog.builder()
        .ip(ip)
        .url(url)
        .parameter(parameter)
        .agent(agent)
        .build();
    accessLogRepository.save(accessLog);
    List<AccessLog> accessLogList = accessLogRepository.findAll();
    AccessLog log = accessLogList.get(0);

    assertEquals(ip, log.getIp());
    assertEquals(url, log.getUrl());
    assertEquals(parameter, log.getParameter());
    assertEquals(agent, log.getAgent());
  }

}