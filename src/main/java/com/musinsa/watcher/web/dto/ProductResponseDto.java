package com.musinsa.watcher.web.dto;

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
  private int real_price;

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
    this.real_price = entity.getReal_price();
  }

  public static Page<ProductResponseDto> convertPage(List<Product> list, Pageable pageable,
      long count) {
    return new PageImpl<ProductResponseDto>(list
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, count);
  }

}
