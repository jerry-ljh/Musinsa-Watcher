package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.price.Price;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id")
  private int productId;

  @Column(columnDefinition = "SMALLINT")
  private int rank;

  private String img;

  private String productName;

  private String productUrl;

  private String brand;

  private String brandUrl;

  private LocalDateTime modifiedDate;

  private String category;

  private int realPrice;

  @OneToMany(mappedBy = "product")
  List<Price> prices = new ArrayList<>();

  public String convertToBigImgUrl() {
    String baseUrl = this.img.split(".jpg")[0];
    return baseUrl.substring(0, baseUrl.length() - 3).concat("500.jpg");
  }

  @Builder
  public Product(int productId, String productName, String brand, String category, int rank,
      String img, LocalDateTime modifiedDate, int realPrice, List<Price> prices) {
    this.productId = productId;
    this.productName = productName;
    this.brand = brand;
    this.category = category;
    this.rank = rank;
    this.img = img;
    this.modifiedDate = modifiedDate;
    this.realPrice = realPrice;
    this.prices = prices;
  }

}
