package com.musinsa.watcher.domain.price;

import com.musinsa.watcher.domain.BaseTimeEntity;
import com.musinsa.watcher.domain.product.Product;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Price extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", referencedColumnName = "product_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  private Product product;

  @Column(columnDefinition = "SMALLINT")
  private int rank;

  private int price;

  private int delPrice;

  private int coupon;

  private float rating;

  private int ratingCount;

  @Builder
  public Price(Product product, int rank, int price, int delPrice, int coupon, float rating,
      int ratingCount) {
    this.product = product;
    this.rank = rank;
    this.price = price;
    this.delPrice = delPrice;
    this.coupon = coupon;
    this.rating = rating;
    this.ratingCount = ratingCount;
  }
}
