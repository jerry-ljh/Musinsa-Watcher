package com.musinsa.watcher.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductCountByBrandDto {

  private final String brand;
  private final long count;

}