package com.musinsa.watcher.domain.product;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("큰 이미지 url를 만든다")
  public void mkBigImageUrl() {
    Product product = Product.builder()
        .productId(10000)
        .productName("셔츠")
        .brand("고급 브랜드")
        .category("001")
        .img("http://cdn.img?id=10000_125.jpg")
        .rank(1).build();
    String bigImgUrl = product.convertToBigImgUrl();
    assertEquals(bigImgUrl, "http://cdn.img?id=10000_500.jpg");
  }

  //@Test
  @DisplayName("특정 카테고리를 조회한다.")
  public void findByCategory() {
    //TODO native 쿼리가 섞여있어 테스트시 오류발생
    String findCategory = "001";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand("고급 브랜드")
          .category(findCategory)
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("셔츠")
          .brand("고급 브랜드")
          .category(findCategory)
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand("고급 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    Page<Product> productPage = productRepository
        .findByCategory(findCategory, PageRequest.of(0, 25));
    List<Product> productList = productPage.getContent();

    assertEquals(productPage.getTotalElements(), 20);
    productList.stream().forEach(i -> assertEquals(i.getCategory(), findCategory));
  }

  @Test
  @DisplayName("특정 브랜드를  조회한다.")
  public void findByBrand() {
    String findBrand = "고급 브랜드";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("셔츠")
          .brand(findBrand)
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand(findBrand)
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("바지")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    Page<Product> productPage = productRepository
        .findByBrand(findBrand, PageRequest.of(0, 25));
    List<Product> productList = productPage.getContent();

    assertEquals(productPage.getTotalElements(), 20);
    productList.stream().forEach(i -> assertEquals(i.getBrand(), findBrand));
  }

  @Test
  @DisplayName("특정 검색어를 조회한다.")
  public void findByTopic() {
    String searchText = "신발";
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("신발 최강")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    Page<Product> productPage = productRepository
        .searchItems(searchText, PageRequest.of(0, 20));
    List<Product> productList = productPage.getContent();
    assertEquals(productPage.getTotalElements(), 20);
    productList.stream().forEach(i -> assertTrue(
        i.getProductName().contains(searchText) ||
            i.getBrand().contains(searchText)
    ));
  }

  //@Test
  @DisplayName("브랜드 리스트를 이니셜로 조회한다.")
  public void 이니셜로_조회() {
    //TODO native 쿼리가 섞여있어 테스트시 오류발생
    Initial initial = InitialWord.type1.getInitials();
    for (int i = 0; i < 10; i++) {
      productRepository.save(Product.builder()
          .productId(i)
          .productName("고급 신발")
          .brand("고급 브랜드")
          .category("001")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(10 + i)
          .productName("모자")
          .brand("가장 좋은 브랜드")
          .category("003")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
      productRepository.save(Product.builder()
          .productId(20 + i)
          .productName("편한 셔츠")
          .brand("일반 브랜드")
          .category("002")
          .img("http://cdn.img?id=10000_125.jpg")
          .build());
    }

    List<Object[]> brandList = productRepository
        .findBrandByInitial(initial.getRLIKE(), initial.getSTART(), initial.getEND());

    assertEquals(brandList.size(), 2);
    assertEquals(brandList.get(0)[0], "고급 브랜드");
    assertEquals(brandList.get(1)[0], "가장 좋은 브랜드");
  }
}