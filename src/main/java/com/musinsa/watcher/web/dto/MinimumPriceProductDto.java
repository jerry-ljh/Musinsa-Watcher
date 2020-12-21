package com.musinsa.watcher.web.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MinimumPriceProductDto implements Serializable {

  private int productId;
  private String productName;
  private String brand;
  private String img;
  private LocalDate modifiedDate;
  private long today_price;
  private long maxPrice;

  @Builder
  public MinimumPriceProductDto(int productId, String productName, String brand,
      String img, Timestamp modifiedDate, long today_price, long maxPrice) {
    this.productId = productId;
    this.productName = productName;
    this.today_price = today_price;
    this.maxPrice = maxPrice;
    this.brand = brand;
    this.img = img;
    this.modifiedDate = modifiedDate.toLocalDateTime().toLocalDate();
  }

  public static MinimumPriceProductDto objectToDto(Object[] object) {
    return MinimumPriceProductDto
        .builder()
        .productId((int) object[0])
        .productName((String) object[1])
        .brand((String) object[2])
        .img((String) object[3])
        .modifiedDate((Timestamp) object[4])
        .today_price(((Integer) object[5]).longValue())
        .maxPrice(((BigInteger) object[6]).longValue())
        .build();
  }

  public static List<MinimumPriceProductDto> objectsToDtoList(List<Object[]> objectList) {
    return objectList.stream()
        .map(product -> MinimumPriceProductDto.objectToDto(product))
        .collect(Collectors.toList());
  }
}
