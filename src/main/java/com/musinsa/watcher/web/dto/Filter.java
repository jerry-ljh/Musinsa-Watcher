package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.config.webparameter.Parameter;
import com.musinsa.watcher.domain.product.Category;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Filter {

  private final String[] brands;
  private final Category[] categories;
  private final int minPrice;
  private final int maxPrice;

  @Builder
  public Filter(String[] brands, Category[] categories, int minPrice, int maxPrice) {
    this.brands = brands;
    this.categories = categories;
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  public void checkValidParameter(Parameter parameter) {
    if (parameter.equals(Parameter.BRAND) && this.brands == null) {
      throw new RuntimeException("브랜드 정보가 입력되지 않았습니다.");
    }
    if (parameter.equals(Parameter.CATEGORY) && this.categories == null) {
      throw new RuntimeException("카테고리 정보가 입력되지 않았습니다.");
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(brands) + "," + Arrays.toString(categories) + "," + minPrice + ","
        + maxPrice;
  }

}
