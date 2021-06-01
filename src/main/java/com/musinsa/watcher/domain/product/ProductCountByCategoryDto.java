package com.musinsa.watcher.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductCountByCategoryDto {

  private final String category;
  private final long count;
}