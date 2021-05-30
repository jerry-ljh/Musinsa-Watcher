package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.dto.ProductCountByBrandDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class ProductCountMapByBrandDto {

  private final Map<String, Integer> brandMap;

  public ProductCountMapByBrandDto(List<ProductCountByBrandDto> list) {
    Map<String, Integer> map = new LinkedHashMap<>();
    list.forEach(i -> map.put(i.getBrand(), (int) i.getCount()));
    this.brandMap = map;
  }
}
