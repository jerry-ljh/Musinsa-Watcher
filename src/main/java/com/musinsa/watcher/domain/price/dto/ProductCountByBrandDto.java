package com.musinsa.watcher.domain.price.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductCountByBrandDto {

  private final String brand;
  private final long count;

}