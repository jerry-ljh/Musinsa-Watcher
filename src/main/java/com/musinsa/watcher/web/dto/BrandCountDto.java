package com.musinsa.watcher.web.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BrandCountDto {

  private final String brand;
  private final long count;

  public static Map<String, Integer> toMap(List<BrandCountDto> list){
    Map<String, Integer> map = new LinkedHashMap<>();
    list.stream().forEach(i -> map.put(i.brand, (int)i.count));
    return map;
  }
}