package com.musinsa.watcher.web.dto;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BrandDto {

  private String brand;
  private int count;

  @Builder
  public BrandDto(String brand, int count) {
    this.brand = brand;
    this.count = count;
  }

  public static BrandDto objectToDto(Object[] object) {
    return BrandDto
        .builder()
        .brand((String) object[0])
        .count(((BigInteger) object[1]).intValue())
        .build();
  }

  public static List<BrandDto> objectsToDtoList(List<Object[]> objectList) {
    return objectList.stream()
        .map(brandObject -> BrandDto.objectToDto(brandObject))
        .collect(Collectors.toList());
  }
}
