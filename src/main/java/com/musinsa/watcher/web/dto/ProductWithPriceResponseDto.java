package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductWithPriceResponseDto implements Serializable {

  private int productId;
  private String img;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private LocalDateTime modifiedDate;
  private String category;
  private List<Price> prices;

  @Builder
  public ProductWithPriceResponseDto(Product entity) {
    this.productId = entity.getProductId();
    this.img = entity.getImg();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.modifiedDate = entity.getModifiedDate();
    this.category = entity.getCategory();
    this.prices = entity.getPrices().stream().collect(Collectors.toList());
  }

}
