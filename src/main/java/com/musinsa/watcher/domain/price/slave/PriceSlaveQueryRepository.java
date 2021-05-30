package com.musinsa.watcher.domain.price.slave;

import static com.musinsa.watcher.domain.price.QTodayDiscountProduct.todayDiscountProduct;
import static com.musinsa.watcher.domain.price.QTodayMinimumPriceProduct.todayMinimumPriceProduct;

import com.musinsa.watcher.domain.price.TodayDiscountProduct;
import com.musinsa.watcher.domain.price.TodayMinimumPriceProduct;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import com.musinsa.watcher.domain.price.dto.ProductCountByCategoryDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PriceSlaveQueryRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ApplicationContext applicationContext;

  private PriceSlaveQueryRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    List<TodayMinimumPriceProduct> results = queryFactory
        .selectFrom(todayMinimumPriceProduct)
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(5))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(0.95))
            .and(todayMinimumPriceProduct.product.category.eq(category.getCategory()))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayMinimumPriceProduct.product)
        .fetchJoin()
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(todayMinimumPriceSort(sort))
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayMinimumPriceProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayMinimumPriceProducts(category, localDate));
  }

  @Cacheable(value = "productCache", key = "'minimum count'+#category + #localDate")
  public long countTodayMinimumPriceProducts(Category category, LocalDate localDate) {
    return queryFactory
        .selectFrom(todayMinimumPriceProduct)
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(5))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(0.95))
            .and(todayMinimumPriceProduct.product.category.eq(category.getCategory()))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayMinimumPriceProduct.product)
        .fetchJoin()
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countMinimumPriceProductEachCategory(LocalDate localDate) {
    return queryFactory.from(todayMinimumPriceProduct)
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(5))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(0.95))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayMinimumPriceProduct.product)
        .groupBy(todayMinimumPriceProduct.product.category)
        .select(Projections
            .constructor(ProductCountByCategoryDto.class, todayMinimumPriceProduct.product.category,
                todayMinimumPriceProduct.product.count().as("count")))
        .fetch();
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    List<TodayDiscountProduct> results = queryFactory
        .selectFrom(todayDiscountProduct)
        .where(todayDiscountProduct.product.category.eq(category.getCategory())
            .and(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product)
        .fetchJoin()
        .orderBy(todayDiscountSort(sort))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayDiscountedProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayDiscountProducts(category, localDate));
  }

  @Cacheable(value = "productCache", key = "'distcount count'+#category +#localDate")
  public long countTodayDiscountProducts(Category category, LocalDate localDate) {
    return queryFactory.selectFrom(todayDiscountProduct)
        .where(todayDiscountProduct.product.category.eq(category.getCategory())
            .and(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product)
        .fetchJoin()
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countDiscountProductEachCategory(LocalDate localDate) {
    return queryFactory.from(todayDiscountProduct)
        .where(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay()))
        .innerJoin(todayDiscountProduct.product)
        .groupBy(todayDiscountProduct.product.category)
        .select(Projections
            .constructor(ProductCountByCategoryDto.class, todayDiscountProduct.product.category,
                todayDiscountProduct.product.count().as("count")))
        .fetch();
  }

  OrderSpecifier todayDiscountSort(String sort) {
    if (sort.equals("percent desc") || sort.isEmpty()) {
      return todayDiscountProduct.percent.desc();
    }
    if (sort.equals("percent asc")) {
      return todayDiscountProduct.percent.asc();
    }
    if (sort.equals("price desc")) {
      return todayDiscountProduct.product.realPrice.desc();
    }
    if (sort.equals("price asc")) {
      return todayDiscountProduct.product.realPrice.asc();
    }
    throw new IllegalArgumentException("not allow sort column");
  }

  OrderSpecifier todayMinimumPriceSort(String sort) {
    if (sort.equals("percent desc") || sort.isEmpty()) {
      return todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice).asc();
    }
    if (sort.equals("percent asc")) {
      return todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice).desc();
    }
    if (sort.equals("price desc")) {
      return todayMinimumPriceProduct.product.realPrice.desc();
    }
    if (sort.equals("price asc")) {
      return todayMinimumPriceProduct.product.realPrice.asc();
    }
    throw new IllegalArgumentException("not allow sort column");
  }

}