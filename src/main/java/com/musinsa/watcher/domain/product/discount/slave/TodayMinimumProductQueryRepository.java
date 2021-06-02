package com.musinsa.watcher.domain.product.discount.slave;


import static com.musinsa.watcher.domain.product.discount.QTodayMinimumPriceProduct.todayMinimumPriceProduct;

import com.musinsa.watcher.SortUtils;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.domain.product.discount.TodayMinimumPriceProduct;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TodayMinimumProductQueryRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ProductRepository productRepository;
  private final ApplicationContext applicationContext;
  private final double MINIMUM_DISCOUNT_RATE = 0.05;
  private final int MINIMUM_SAMPLE_COUNT = 5;

  private TodayMinimumProductQueryRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(FilterVo filterVo,
      Pageable pageable) {
    LocalDate cachedDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    List<TodayMinimumPriceProduct> results = queryFactory.selectFrom(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product).fetchJoin()
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(getBooleanFilter(filterVo))
            .and(todayMinimumPriceProduct.modifiedDate.after(cachedDate.atStartOfDay())))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(sort(pageable))
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayMinimumPriceProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayMinimumPriceProducts(filterVo));
  }

  @Cacheable(value = "productCache")
  public long countTodayMinimumPriceProducts(FilterVo filterVo) {
    LocalDate cachedDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.selectFrom(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product).fetchJoin()
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(getBooleanFilter(filterVo))
            .and(todayMinimumPriceProduct.modifiedDate.after(cachedDate.atStartOfDay())))
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countTodayMinimumPriceProductEachCategory() {
    LocalDate cachedDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.from(todayMinimumPriceProduct)
        .innerJoin(todayMinimumPriceProduct.product)
        .where(todayMinimumPriceProduct.minPrice.eq(todayMinimumPriceProduct.todayPrice)
            .and(todayMinimumPriceProduct.avgPrice.gt(todayMinimumPriceProduct.todayPrice))
            .and(todayMinimumPriceProduct.count.goe(MINIMUM_SAMPLE_COUNT))
            .and(todayMinimumPriceProduct.minPrice.divide(todayMinimumPriceProduct.avgPrice)
                .loe(1 - MINIMUM_DISCOUNT_RATE))
            .and(todayMinimumPriceProduct.modifiedDate.after(cachedDate.atStartOfDay())))
        .groupBy(todayMinimumPriceProduct.product.category)
        .select(Projections.constructor(ProductCountByCategoryDto.class,
            todayMinimumPriceProduct.product.category,
            todayMinimumPriceProduct.product.count()))
        .fetch();
  }

  private OrderSpecifier sort(Pageable pageable) {
    for (Sort.Order order : pageable.getSort()) {
      if (order.getProperty().trim().equals("percent")) {
        return SortUtils.getOrderSpecifier(order,
            todayMinimumPriceProduct.avgPrice.divide(todayMinimumPriceProduct.todayPrice));
      }
      if (order.getProperty().trim().equals("price")) {
        return SortUtils.getOrderSpecifier(order, todayMinimumPriceProduct.todayPrice);
      }
      return SortUtils.getOrderSpecifier(order, TodayMinimumProductQueryRepository.class);
    }
    return SortUtils.getOrderSpecifier(Order.DESC,
        todayMinimumPriceProduct.avgPrice.divide(todayMinimumPriceProduct.todayPrice));
  }

  private BooleanBuilder getBooleanFilter(FilterVo filterVo) {
    return new com.musinsa.watcher.domain.product.SearchFilter.Builder()
        .setBrands(todayMinimumPriceProduct.product.brand, filterVo.getBrands())
        .setCategories(todayMinimumPriceProduct.product.category, filterVo.getCategories())
        .setMaxPrice(todayMinimumPriceProduct.product.realPrice, filterVo.getMaxPrice())
        .setMinPrice(todayMinimumPriceProduct.product.realPrice, filterVo.getMinPrice())
        .build().getBooleanBuilder();
  }
}
