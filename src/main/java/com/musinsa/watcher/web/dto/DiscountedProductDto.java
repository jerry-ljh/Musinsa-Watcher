package com.musinsa.watcher.web.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiscountedProductDto {

  private int productId;
  private String productName;
  private String brand;
  private String img;
  private String modifiedDate;
  private long price;
  private long discount;
  private float percent;

  @Builder
  public DiscountedProductDto(int productId, String productName, String brand, long price,
      String img, Timestamp modifiedDate, long discount, float percent) {
    this.productId = productId;
    this.productName = productName;
    this.price = price;
    this.brand = brand;
    this.img = img;
    this.modifiedDate = modifiedDate.toLocalDateTime().toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    this.discount = discount;
    this.percent = percent;
  }

  public static DiscountedProductDto objectToDto(Object[] object) {
    return DiscountedProductDto
        .builder()
        .productId((int) object[0])
        .productName((String) object[1])
        .brand((String) object[2])
        .price(((BigInteger) object[3]).longValue())
        .img((String) object[4])
        .modifiedDate((Timestamp) object[5])
        .discount(((BigInteger) object[6]).longValue())
        .percent(((BigDecimal) object[7]).floatValue())
        .build();
  }

  public static List<DiscountedProductDto> objectsToDtoList(List<Object[]> objectList) {
    return objectList.stream()
        .map(product -> DiscountedProductDto.objectToDto(product))
        .collect(Collectors.toList());
  }
}
