package com.musinsa.watcher.domain.product.discount;

import com.musinsa.watcher.domain.BaseTimeEntity;
import com.musinsa.watcher.domain.product.Product;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Entity
public class TodayMinimumPriceProduct extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "product_id", referencedColumnName = "product_id")
  private Product product;

  private int minPrice;

  private float avgPrice;

  private int todayPrice;

  private int count;

  private LocalDateTime modifiedDate;

  @Builder
  public TodayMinimumPriceProduct(Product product, int minPrice, float avgPrice, int todayPrice,
      int count, LocalDateTime modifiedDate) {
    this.product = product;
    this.minPrice = minPrice;
    this.avgPrice = avgPrice;
    this.todayPrice = todayPrice;
    this.count = count;
    this.modifiedDate = modifiedDate;
  }
}