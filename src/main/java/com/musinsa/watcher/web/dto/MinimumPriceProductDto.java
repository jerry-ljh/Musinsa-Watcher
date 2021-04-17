package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.TodayMinimumPriceProduct;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MinimumPriceProductDto implements Serializable {

  private int productId;
  private String productName;
  private String brand;
  private String img;
  private LocalDate modifiedDate;
  private int todayPrice;
  private int avgPrice;

  @Builder
  public MinimumPriceProductDto(TodayMinimumPriceProduct entity) {
    this.productId = entity.getProduct().getProductId();
    this.productName = entity.getProduct().getProductName();
    this.todayPrice = entity.getToday_price();
    this.avgPrice = (int)entity.getAvg_price();
    this.brand = entity.getProduct().getBrand();
    this.img = entity.getProduct().getImg();
    this.modifiedDate = entity.getProduct().getModifiedDate().toLocalDate();
  }

}