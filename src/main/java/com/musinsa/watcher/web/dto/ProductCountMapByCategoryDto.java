package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class ProductCountMapByCategoryDto implements Serializable {

  private final Map<String, Integer> categoryProductMap;

  public ProductCountMapByCategoryDto(List<ProductCountByCategoryDto> list) {
    Map<String, Integer> map = new LinkedHashMap<>();
    list.forEach(i -> map.put(i.getCategory(), (int) i.getCount()));
    Arrays.stream(Category.values()).forEach(i -> map.putIfAbsent(i.getCategory(), 0));
    this.categoryProductMap = map;
  }

}
