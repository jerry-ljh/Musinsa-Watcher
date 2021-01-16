package com.musinsa.watcher.domain.product.master;

import com.musinsa.watcher.domain.price.QPrice;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.QProduct;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductQueryMasterRepository {

  @Qualifier("masterJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ApplicationContext applicationContext;

  private ProductQueryMasterRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    List<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.brand.contains(text)
                .or(QProduct.product.productName.contains(text)))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, this.getSpringProxy().countSearchItems(text));
  }

  @Cacheable(value = "productCache", key = "'search count'+#text")
  public long countSearchItems(String text) {
    return queryFactory.selectFrom(QProduct.product)
        .where(QProduct.product.brand.contains(text)
            .or(QProduct.product.productName.contains(text)))
        .fetchCount();
  }

  public Product findProductWithPrice(int productId) {
    return queryFactory.selectFrom(QProduct.product)
        .leftJoin(QProduct.product.prices, QPrice.price1)
        .fetchJoin()
        .where(QProduct.product.productId.eq(productId))
        .orderBy(QPrice.price1.createdDate.desc())
        .fetchOne();
  }

  public Page<ProductResponseDto> findByCategory(String category, LocalDate updateDate,
      Pageable pageable) {
    List<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.category.eq(category),
                QProduct.product.modifiedDate.after(updateDate.atStartOfDay()),
                QProduct.product.modifiedDate.before(updateDate.plusDays(1).atStartOfDay()))
            .orderBy(QProduct.product.rank.asc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countFindByCategory(category, updateDate));
  }

  @Cacheable(value = "productCache", key = "'category count'+#category")
  public long countFindByCategory(String category, LocalDate updateDate) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.category.eq(category),
            QProduct.product.modifiedDate.after(updateDate.atStartOfDay()),
            QProduct.product.modifiedDate.before(updateDate.plusDays(1).atStartOfDay()))
        .fetchCount();
  }

  public Page<ProductResponseDto> findByBrand(String brand, Pageable pageable) {
    List<Product> results = queryFactory.selectFrom(QProduct.product)
        .where(QProduct.product.brand.eq(brand))
        .orderBy(QProduct.product.modifiedDate.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results.stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countFindByBrand(brand));
  }

  @Cacheable(value = "productCache", key = "'brand count'+#brand")
  public long countFindByBrand(String brand) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.eq(brand))
        .fetchCount();
  }

  public LocalDateTime findLastUpdateDate() {
    return queryFactory.selectFrom(QProduct.product)
        .orderBy(QProduct.product.modifiedDate.desc())
        .fetchFirst().getModifiedDate();
  }

  public List<Object[]> findBrandByInitial(String initial1, String initial2) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.goe(initial1).and(QProduct.product.brand.lt(initial2)))
        .groupBy(QProduct.product.brand)
        .orderBy(QProduct.product.brand.asc())
        .select(QProduct.product.brand, QProduct.product.brand.count())
        .fetch().stream().map(i -> i.toArray()).collect(Collectors.toList());
  }
}
