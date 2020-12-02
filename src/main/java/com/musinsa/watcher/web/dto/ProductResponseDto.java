package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto implements Serializable {

  private int productId;
  private String img;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private LocalDateTime time;
  private String category;

  @Builder
  public ProductResponseDto(Product entity) {
    this.productId = entity.getProductId();
    this.img = entity.getImg();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.time = entity.getTime();
    this.category = entity.getCategory();
  }
}
