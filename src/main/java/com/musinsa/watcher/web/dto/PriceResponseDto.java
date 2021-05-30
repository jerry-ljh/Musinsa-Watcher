package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PriceResponseDto implements Serializable {

  private final int rank;
  private final int price;
  private final int delPrice;
  private final int coupon;
  private final float rating;
  private final int ratingCount;
  private final LocalDate createdDate;

  @Builder
  public PriceResponseDto(Price entity) {
    this.rank = entity.getRank();
    this.coupon = entity.getCoupon();
    this.price = entity.getPrice();
    this.delPrice = entity.getDelPrice();
    this.rating = entity.getRating();
    this.ratingCount = entity.getRatingCount();
    this.createdDate = entity.getCreatedDate().toLocalDate();
  }

}