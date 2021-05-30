package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.TodayMinimumPriceProduct;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TodayMinimumPriceProductDto implements Serializable {

  private final int productId;
  private final String productName;
  private final String brand;
  private final String img;
  private final LocalDate modifiedDate;
  private final int todayPrice;
  private final int avgPrice;

  @Builder
  public TodayMinimumPriceProductDto(TodayMinimumPriceProduct entity) {
    this.productId = entity.getProduct().getProductId();
    this.productName = entity.getProduct().getProductName();
    this.todayPrice = entity.getTodayPrice();
    this.avgPrice = (int)entity.getAvgPrice();
    this.brand = entity.getProduct().getBrand();
    this.img = entity.getProduct().getImg();
    this.modifiedDate = entity.getProduct().getModifiedDate().toLocalDate();
  }

}