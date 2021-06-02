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
    String onlyTodayUpdatedData = webRequest
        .getParameter(Parameter.ONLY_TODAY_UPDATED_DATA.getParameter());
    FilterVo filterVo = FilterVo.builder()
        .brands(getBrands(brand))
        .categories(getCategories(category))
        .minPrice(minPrice != null ? Integer.parseInt(minPrice) : null)
        .maxPrice(maxPrice != null ? Integer.parseInt(maxPrice) : null)
        .onlyTodayUpdatedData(
            onlyTodayUpdatedData != null ? Boolean.valueOf(onlyTodayUpdatedData) : null)
        .build();
    return filterVo;
  }

  private String[] getBrands(String brands) {
    if (brands == null || brands.isEmpty()) {
      return null;
    }
    String[] brandArray = brands.split(",");
    return Arrays.stream(brandArray).map(String::trim).toArray(String[]::new);
  }

  private Category[] getCategories(String categories) {
    if (categories == null || categories.isEmpty()) {
      return null;
    }
    String[] categoryArr = categories.split(",");
    return Arrays.stream(categoryArr).map(Category::getCategory).toArray(Category[]::new);
  }
}
