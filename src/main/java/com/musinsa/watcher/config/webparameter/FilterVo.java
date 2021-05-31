package com.musinsa.watcher.config.webparameter;

import com.musinsa.watcher.domain.product.Category;
import java.util.Arrays;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FilterVo {

  private final String[] brands;
  private final Category[] categories;
  private final int minPrice;
  private final int maxPrice;

  @Builder
  public FilterVo(String[] brands, Category[] categories, int minPrice, int maxPrice) {
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
  public boolean equals(Object obj) {
    if(!(obj instanceof FilterVo)){
      return false;
    }
    FilterVo input = (FilterVo) obj;
    return this.toString().equals(input.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.toString());
  }

  @Override
  public String toString() {
    return "FilterDto{" +
        "brands=" + Arrays.toString(brands) +
        ", categories=" + Arrays.toString(categories) +
        ", minPrice=" + minPrice +
        ", maxPrice=" + maxPrice +
        '}';
  }

}
