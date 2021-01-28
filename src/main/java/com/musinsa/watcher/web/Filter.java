package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.Parameter;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Filter {

  private String[] brand;
  private String[] category;
  private int minPrice;
  private int maxPrice;

  @Builder
  public Filter(String[] brand, String[] category, int minPrice, int maxPrice) {
    this.brand = brand;
    this.category = category;
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  public void necessary(Parameter parameter) {
    if (parameter.equals(Parameter.BRAND) && this.brand == null) {
      throw new RuntimeException("브랜드 정보가 입력되지 않았습니다.");
    }
    if (parameter.equals(Parameter.CATEGORY) && this.category == null) {
      throw new RuntimeException("카테고리 정보가 입력되지 않았습니다.");
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(brand) + "," + Arrays.toString(category) + "," + minPrice + ","
        + maxPrice;
  }
}
