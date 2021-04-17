package com.musinsa.watcher.web.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CountEachCategoryDto {
  private final String category;
  private final long count;

  public static Map<String, Integer> toMap(List<CountEachCategoryDto> list){
    Map<String, Integer> map = new HashMap<>();
    list.stream().forEach(i -> map.put(i.category, (int)i.count));
    return map;
  }
}