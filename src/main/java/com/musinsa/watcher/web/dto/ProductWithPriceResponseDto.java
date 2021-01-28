package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.product.Product;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
public class ProductWithPriceResponseDto implements Serializable{

  private int rank;
  private int productId;
  private String bigImg;
  private String productName;
  private String productUrl;
  private String brand;
  private String brandUrl;
  private LocalDate modifiedDate;
  private String category;
  private List<PriceResponseDto> prices;

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

  public static Page<PriceResponseDto> convertPage(Page<Price> page, Pageable pageable) {
    return new PageImpl<PriceResponseDto>(page.getContent()
        .stream()
        .map(PriceResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }
}
