package com.musinsa.watcher.config.webparameter;

import com.musinsa.watcher.web.Filter;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class ParameterHandler implements HandlerMethodArgumentResolver {

  private final int DEFAULT_MIN_PRICE = 0;
  private final int DEFAULT_MAX_PRICE = 100_000_000;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean isParameterAnnotation = parameter.getParameterAnnotation(ParameterFilter.class) != null;
    boolean isFilterClass = Filter.class.equals(parameter.getParameterType());
    return isParameterAnnotation && isFilterClass;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    String brand = webRequest.getParameter(Parameter.BRAND.getParameter());
    String category = webRequest.getParameter(Parameter.CATEGORY.getParameter());
    String minPrice = webRequest.getParameter(Parameter.MIN_PRICE.getParameter());
    String maxPrice = webRequest.getParameter(Parameter.MAX_PRICE.getParameter());
    Filter filter = Filter.builder()
        .brand(brand != null && !brand.isEmpty() ? brand.split(",") : null)
        .category(category != null && !category.isEmpty() ? category.split(",") : null)
        .minPrice(minPrice != null ? Integer.parseInt(minPrice) : DEFAULT_MIN_PRICE)
        .maxPrice(maxPrice != null ? Integer.parseInt(maxPrice) : DEFAULT_MAX_PRICE)
        .build();
    filter.necessary(parameter.getParameterAnnotation(ParameterFilter.class).necessary());
    return filter;
  }
}
