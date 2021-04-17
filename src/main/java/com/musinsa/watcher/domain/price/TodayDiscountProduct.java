package com.musinsa.watcher.domain.price;

import com.musinsa.watcher.domain.BaseTimeEntity;
import com.musinsa.watcher.domain.product.Product;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class TodayDiscountProduct extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(nullable = false, name = "product_id", referencedColumnName = "product_id")
  private Product product;

  @Column(nullable = false)
  private int discount;

  @Column(nullable = false)
  private float percent;

  private LocalDateTime modifiedDate;
}