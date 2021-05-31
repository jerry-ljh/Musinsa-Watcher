package com.musinsa.watcher.config.webparameter;

import com.musinsa.watcher.domain.product.Category;
import java.util.Arrays;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ParameterHandler implements HandlerMethodArgumentResolver {

  private final int DEFAULT_MIN_PRICE = 0;
  private final int DEFAULT_MAX_PRICE = 100_000_000;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean isParameterAnnotation = parameter.getParameterAnnotation(SearchFilter.class) != null;
    boolean isFilterClass = FilterVo.class.equals(parameter.getParameterType());
    return isParameterAnnotation && isFilterClass;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    String brand = webRequest.getParameter(Parameter.BRAND.getParameter());
    String category = webRequest.getParameter(Parameter.CATEGORY.getParameter());
    String minPrice = webRequest.getParameter(Parameter.MIN_PRICE.getParameter());
    String maxPrice = webRequest.getParameter(Parameter.MAX_PRICE.getParameter());
    FilterVo filterVo = FilterVo.builder()
        .brands(brand != null && !brand.isEmpty() ? brand.split(",") : null)
        .categories(splitCategory(category))
        .minPrice(minPrice != null ? Integer.parseInt(minPrice) : DEFAULT_MIN_PRICE)
        .maxPrice(maxPrice != null ? Integer.parseInt(maxPrice) : DEFAULT_MAX_PRICE)
        .build();
    filterVo.checkValidParameter(parameter.getParameterAnnotation(SearchFilter.class).required());
    return filterVo;
  }

  private Category[] splitCategory(String category) {
    if (category == null) {
      return null;
    }
    String[] categories = category.split(",");
    return Arrays.stream(categories).map(Category::getCategory).toArray(Category[]::new);
  }
}
