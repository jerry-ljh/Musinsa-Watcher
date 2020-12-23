package com.musinsa.watcher.config;

import com.musinsa.watcher.domain.log.AccessLog;
import com.musinsa.watcher.domain.log.AccessLogRepository;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogInterceptor extends HandlerInterceptorAdapter {

  private final AccessLogRepository accessLogRepository;
  private final String OUTBOUND_URI = "/api/product/link";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (!request.getRequestURI().equals(OUTBOUND_URI) && handler instanceof HandlerMethod == false) {
      return true;
    }
    accessLogRepository.save(AccessLog.builder()
        .ip(getRemoteAddr(request))
        .agent(request.getHeader("User-Agent"))
        .url(request.getRequestURI())
        .parameter(request.getQueryString())
        .build());
    if (request.getRequestURI().equals(OUTBOUND_URI)) {
      response.setStatus(HttpStatus.OK.value());
      return false;
    }
    return true;
  }

  private String getRemoteAddr(HttpServletRequest request) {
    return (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR")
        : request.getRemoteAddr();
  }

}
