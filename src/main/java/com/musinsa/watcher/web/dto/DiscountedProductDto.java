package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.TodayDiscountProduct;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class DiscountedProductDto implements Serializable {

  private int productId;
  private String productName;
  private String brand;
  private String img;
  private LocalDate modifiedDate;
  private int price;
  private long discount;
  private float percent;

  public DiscountedProductDto(TodayDiscountProduct entity) {
    this.productId = entity.getProduct().getProductId();
    this.productName = entity.getProduct().getProductName();
    this.price = entity.getProduct().getRealPrice();
    this.brand = entity.getProduct().getBrand();
    this.img = entity.getProduct().getImg();
    this.modifiedDate = entity.getCreatedDate().toLocalDate();
    this.discount = entity.getDiscount();
    this.percent = entity.getPercent();
  }

}