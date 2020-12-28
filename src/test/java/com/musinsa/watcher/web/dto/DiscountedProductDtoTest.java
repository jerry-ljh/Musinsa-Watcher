package com.musinsa.watcher.web.dto;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DiscountedProductDtoTest {

  @Test
  @DisplayName("오늘 할인 품목 object mapper 테스트")
  public void test() {
    //given
    Object productId = 1;
    Object productName = "name";
    Object brand = "brand";
    Object img = "url";
    Object price = BigInteger.valueOf(10000L);
    Object modifiedDate = Timestamp.from(Instant.now());
    Object discount = BigInteger.valueOf(1000L);
    Object percent = new BigDecimal(15.3);
    DiscountedProductDto testDto = DiscountedProductDto
        .builder()
        .productId((int) productId)
        .productName((String) productName)
        .brand((String) brand)
        .price(((BigInteger) price).longValue())
        .img((String) img)
        .modifiedDate((Timestamp) modifiedDate)
        .discount(((BigInteger) discount).longValue())
        .percent(((BigDecimal) percent).floatValue())
        .build();
    Object[] objects = new Object[]{productId, productName, brand, price, img, modifiedDate,
        discount, percent};
    List<Object[]> list = new ArrayList<>();
    list.add(objects);
    //when
    List<DiscountedProductDto> discountedProductDtoList = DiscountedProductDto
        .objectsToDtoList(list);
    //then
    DiscountedProductDto discountedProductDto = discountedProductDtoList.get(0);
    assertEquals(testDto.getDiscount(), discountedProductDto.getDiscount());
    assertEquals(testDto.getBrand(), discountedProductDto.getBrand());
    assertEquals(testDto.getImg(), discountedProductDto.getImg());
    assertEquals(testDto.getProductId(), discountedProductDto.getProductId());
    assertEquals(Double.compare(testDto.getPercent(), discountedProductDto.getPercent()), 0);
    assertEquals(testDto.getModifiedDate(), discountedProductDto.getModifiedDate());
    assertEquals(testDto.getProductName(), discountedProductDto.getProductName());
  }
}