package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.dto.ProductCountByCategoryDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class ProductCountMapByCategoryDto {

  private final Map<String, Integer> categoryProductMap;

  public ProductCountMapByCategoryDto(List<ProductCountByCategoryDto> list) {
    Map<String, Integer> map = new LinkedHashMap<>();
    list.forEach(i -> map.put(i.getCategory(), (int) i.getCount()));
    this.categoryProductMap = map;
  }

}
