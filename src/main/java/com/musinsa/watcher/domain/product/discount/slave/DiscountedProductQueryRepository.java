package com.musinsa.watcher.domain.product.discount.slave;


import static com.musinsa.watcher.domain.product.discount.QTodayDiscountProduct.todayDiscountProduct;
import static com.musinsa.watcher.domain.product.discount.QTodayMinimumPriceProduct.todayMinimumPriceProduct;

import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.domain.product.discount.TodayDiscountProduct;
import com.musinsa.watcher.domain.product.discount.TodayMinimumPriceProduct;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
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
public class DiscountedProductQueryRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ProductRepository productRepository;
  private final ApplicationContext applicationContext;
  private final double MINIMUM_DISCOUNT_RATE = 0.05;
  private final int MINIMUM_SAMPLE_COUNT = 5;

  private DiscountedProductQueryRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      String sort, Pageable pageable) {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    List<TodayMinimumPriceProduct> results = queryFactory.selectFrom(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product).fetchJoin()
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(todayMinimumPriceProduct.product.category.eq(category.getCategory()))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(todayMinimumPriceSort(sort))
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayMinimumPriceProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayMinimumPriceProducts(category));
  }

  @Cacheable(value = "productCache")
  public long countTodayMinimumPriceProducts(Category category) {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.selectFrom(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product).fetchJoin()
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(todayMinimumPriceProduct.product.category.eq(category.getCategory()))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countTodayMinimumPriceProductEachCategory() {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.from(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product)
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(todayMinimumPriceProduct.modifiedDate.after(localDate.atStartOfDay())))
        .groupBy(todayMinimumPriceProduct.product.category)
        .select(Projections.constructor(ProductCountByCategoryDto.class,
            todayMinimumPriceProduct.product.category,
            todayMinimumPriceProduct.product.count().as("count")))
        .fetch();
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountProducts(Category category,
      String sort, Pageable pageable) {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    List<TodayDiscountProduct> results = queryFactory.selectFrom(todayDiscountProduct)
        .where(todayDiscountProduct.product.category.eq(category.getCategory())
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_RATE * 100))
            .and(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product).fetchJoin()
        .orderBy(todayDiscountSort(sort))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayDiscountedProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayDiscountProducts(category));
  }

  @Cacheable(value = "productCache")
  public long countTodayDiscountProducts(Category category) {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.selectFrom(todayDiscountProduct)
        .where(todayDiscountProduct.product.category.eq(category.getCategory())
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_RATE * 100))
            .and(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product).fetchJoin()
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countTodayDiscountProductEachCategory() {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.from(todayDiscountProduct)
        .where(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_RATE * 100)))
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
