package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.product.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductWithPriceResponseDto {

  private final int rank;
  private final int productId;
  private final String bigImg;
  private final String productName;
  private final String productUrl;
  private final String brand;
  private final String brandUrl;
  private final LocalDate modifiedDate;
  private final String category;
  private final List<PriceResponseDto> prices;

  @Builder
  public ProductWithPriceResponseDto(Product entity) {
    this.rank = entity.getRank();
    this.productId = entity.getProductId();
    this.bigImg = entity.convertToBigImgUrl();
    this.productName = entity.getProductName();
    this.productUrl = entity.getProductUrl();
    this.brand = entity.getBrand();
    this.brandUrl = entity.getBrandUrl();
    this.modifiedDate = entity.getModifiedDate().toLocalDate();
    this.category = entity.getCategory();
    this.prices = entity.getPrices().stream().map(PriceResponseDto::new)
        .collect(Collectors.toList());
  }

}