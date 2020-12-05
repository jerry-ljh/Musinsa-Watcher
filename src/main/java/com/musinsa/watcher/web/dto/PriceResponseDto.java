package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PriceResponseDto {

  private int rank;
  private int price;
  private int delPrice;
  private int coupon;
  private float rating;
  private int ratingCount;
  private String createdDate;

  @Builder
  public PriceResponseDto(Price entity) {
    this.rank = entity.getRank();
    this.coupon = entity.getCoupon();
    this.price = entity.getPrice();
    this.delPrice = entity.getDelPrice();
    this.rating = entity.getRating();
    this.ratingCount = entity.getRatingCount();
    this.createdDate = entity.getCreatedDate()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
  }

}
