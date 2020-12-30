package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.price.QPrice;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    QueryResults<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.brand.contains(text)
                .or(QProduct.product.productName.contains(text)))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetchResults();
    return new PageImpl<>(results.getResults()
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, results.getTotal());
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
    QueryResults<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.category.eq(category),
                QProduct.product.modifiedDate.after(updateDate.atStartOfDay()),
                QProduct.product.modifiedDate.before(updateDate.plusDays(1).atStartOfDay()))
            .orderBy(QProduct.product.rank.asc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetchResults();
    return new PageImpl<>(results.getResults()
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, results.getTotal());
  }

  public LocalDateTime findLastUpdateDate() {
    return queryFactory.selectFrom(QProduct.product)
        .orderBy(QProduct.product.modifiedDate.desc())
        .fetchFirst().getModifiedDate();
  }

}
