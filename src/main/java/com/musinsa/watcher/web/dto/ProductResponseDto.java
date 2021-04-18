package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto implements Serializable {

  private int rank;
  private int productId;
  private String img;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private LocalDate modifiedDate;
  private String category;
  private int realPrice;

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