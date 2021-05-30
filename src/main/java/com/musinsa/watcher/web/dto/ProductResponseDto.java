package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto implements Serializable {

  private final int rank;
  private final int productId;
  private final String img;
  private final String productName;
  private final String productUrl;
  private final String brand;
  private final String brandUrl;
  private final LocalDate modifiedDate;
  private final String category;
  private final int realPrice;

  @Builder
  public ProductResponseDto(Product entity) {
    this.rank = entity.getRank();
    this.productId = entity.getProductId();
    this.img = entity.getImg();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.modifiedDate = entity.getModifiedDate().toLocalDate();
    this.category = entity.getCategory();
    this.realPrice = entity.getRealPrice();
  }
}