package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.price.QPrice;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    QueryResults<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(brandContains(text).or(productNameContains(text)))
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

  public Page<ProductResponseDto> findByCategory(String category, Pageable pageable) {
    StringTemplate dateFormat = Expressions
        .stringTemplate("date_format({0}, {1})", QProduct.product.modifiedDate,
            ConstantImpl.create("%Y, %m, %d"));
    QueryResults<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.category.eq(category))
            .orderBy(dateFormat.desc(), QProduct.product.rank.asc())
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

  private BooleanExpression brandContains(String brand) {
    return brand != null ? QProduct.product.brand.contains(brand) : null;
  }

  private BooleanExpression productNameContains(String productName) {
    return productName != null ? QProduct.product.productName.contains(productName) : null;
  }
}
