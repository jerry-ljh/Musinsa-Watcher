package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.discount.TodayDiscountProduct;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TodayDiscountedProductDto implements Serializable {

  private final int productId;
  private final String productName;
  private final String brand;
  private final String img;
  private final LocalDate modifiedDate;
  private final int price;
  private final long discount;
  private final double percent;

  public TodayDiscountedProductDto(TodayDiscountProduct entity) {
    this.productId = entity.getProduct().getProductId();
    this.productName = entity.getProduct().getProductName();
    this.price = entity.getProduct().getRealPrice();
    this.brand = entity.getProduct().getBrand();
    this.img = entity.getProduct().getImg();
    this.modifiedDate = entity.getModifiedDate().toLocalDate();
    this.discount = entity.getDiscount();
    this.percent = entity.getPercent();
  }

}