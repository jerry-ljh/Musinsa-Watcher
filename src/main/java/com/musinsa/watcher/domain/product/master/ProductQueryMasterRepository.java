package com.musinsa.watcher.domain.product.master;

import com.musinsa.watcher.domain.product.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductQueryMasterRepository {

  @Qualifier("masterJPAQueryFactory")
  private final JPAQueryFactory queryFactory;

  public List<Object[]> findBrandByInitial(String initial1, String initial2) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.goe(initial1).and(QProduct.product.brand.lt(initial2)))
        .groupBy(QProduct.product.brand)
        .orderBy(QProduct.product.brand.asc())
        .select(QProduct.product.brand, QProduct.product.brand.count())
        .fetch().stream().map(i -> i.toArray()).collect(Collectors.toList());
  }
}
