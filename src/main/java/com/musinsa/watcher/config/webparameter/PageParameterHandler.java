package com.musinsa.watcher.config.webparameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PageParameterHandler implements HandlerMethodArgumentResolver {

  private final int DEFAULT_PAGE_NUM = 0;
  private final int DEFAULT_PAGE_SIZE = 100;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean isParameterAnnotation = parameter.getParameterAnnotation(PageParameter.class) != null;
    boolean isPageRequestClass = PageRequest.class.equals(parameter.getParameterType());
    return isParameterAnnotation && isPageRequestClass;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    String page = webRequest.getParameter(Parameter.PAGE_NUM.getParameter());
    String pageSize = webRequest.getParameter(Parameter.PAGE_SIZE.getParameter());
    return PageRequest.of(page != null ? Integer.parseInt(page) : DEFAULT_PAGE_NUM,
        pageSize != null ? Integer.parseInt(pageSize) : DEFAULT_PAGE_SIZE);
  }
}
