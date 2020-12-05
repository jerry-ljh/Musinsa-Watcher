package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.price.Price;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;

@NoArgsConstructor
@Getter
@Entity
public class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id")
  private int productId;

  private String img;

  private String productName;

  private String productUrl;

  private String brand;

  private String brandUrl;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  private String category;

  @OneToMany(mappedBy = "product")
  @BatchSize(size = 100)
  Set<Price> prices = new HashSet<>();
}