package com.musinsa.watcher.domain.price.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductCountByCategoryDto {

  private final String category;
  private final long count;
}