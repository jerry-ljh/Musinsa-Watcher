package com.musinsa.watcher.web.dto;

import com.musinsa.watcher.domain.price.Price;
import java.io.Serializable;
import java.time.LocalDate;

import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
public class PriceResponseDto implements Serializable {

  private int rank;
  private int price;
  private int delPrice;
  private int coupon;
  private float rating;
  private int ratingCount;
  private LocalDate createdDate;

  @Builder
  public PriceResponseDto(Price entity) {
    this.rank = entity.getRank();
    this.coupon = entity.getCoupon();
    this.price = entity.getPrice();
    this.delPrice = entity.getDelPrice();
    this.rating = entity.getRating();
    this.ratingCount = entity.getRatingCount();
    this.createdDate = entity.getCreatedDate().toLocalDate();
  }

  public static Page<PriceResponseDto> convertPage(Page<Price> page, Pageable pageable,
      long count) {
    return new PageImpl<PriceResponseDto>(page.getContent()
        .stream()
        .map(PriceResponseDto::new)
        .collect(Collectors.toList()),
        pageable,
        count);
  }
}
