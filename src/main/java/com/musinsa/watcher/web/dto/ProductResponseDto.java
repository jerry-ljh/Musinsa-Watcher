package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto {

  private int rank;
  private int productId;
  private String img;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private String modifiedDate;
  private String category;

  @Builder
  public ProductResponseDto(Product entity) {
    this.rank = entity.getRank();
    this.productId = entity.getProductId();
    this.img = entity.getImg();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.modifiedDate = entity.getModifiedDate()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    this.category = entity.getCategory();
  }
}
