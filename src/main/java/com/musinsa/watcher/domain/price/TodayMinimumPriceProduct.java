package com.musinsa.watcher.domain.price;

import com.musinsa.watcher.domain.BaseTimeEntity;
import com.musinsa.watcher.domain.product.Product;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Entity
public class TodayMinimumPriceProduct extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(nullable = false, name = "product_id", referencedColumnName = "product_id")
  private Product product;

  private int min_price;

  private float avg_price;

  private int today_price;

  private int count;

  private LocalDateTime modifiedDate;
}