package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductWithPriceResponseDto implements Serializable {

  private int productId;
  private String bigImg;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private String modifiedDate;
  private String category;
  private List<Price> prices;

  @Builder
  public ProductWithPriceResponseDto(Product entity) {
    this.productId = entity.getProductId();
    this.bigImg = entity.convertToBigImgUrl();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.modifiedDate = entity.getModifiedDate()
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    this.category = entity.getCategory();
    this.prices = entity.getPrices().stream().collect(Collectors.toList());
  }

}
