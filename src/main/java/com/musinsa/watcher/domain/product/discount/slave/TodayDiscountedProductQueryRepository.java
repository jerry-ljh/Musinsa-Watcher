package com.musinsa.watcher.domain.product.discount.slave;


import static com.musinsa.watcher.domain.product.discount.QTodayDiscountProduct.todayDiscountProduct;

import com.musinsa.watcher.SortUtils;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.domain.product.discount.TodayDiscountProduct;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
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
public class TodayDiscountedProductQueryRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ProductRepository productRepository;
  private final ApplicationContext applicationContext;
  private final double MINIMUM_DISCOUNT_PERCENT = 5;

  private TodayDiscountedProductQueryRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountProducts(FilterVo filterVo,
      Pageable pageable) {
    LocalDate cachedDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    List<TodayDiscountProduct> results = queryFactory.selectFrom(todayDiscountProduct)
        .where(getBooleanFilter(filterVo)
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_PERCENT))
            .and(todayDiscountProduct.modifiedDate.after(cachedDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product).fetchJoin()
        .orderBy(sort(pageable))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(TodayDiscountedProductDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayDiscountProducts(filterVo));
  }

  @Cacheable(value = "productCache")
  public long countTodayDiscountProducts(FilterVo filterVo) {
    LocalDate localDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.selectFrom(todayDiscountProduct)
        .where(getBooleanFilter(filterVo)
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_PERCENT))
            .and(todayDiscountProduct.modifiedDate.after(localDate.atStartOfDay())))
        .innerJoin(todayDiscountProduct.product).fetchJoin()
        .fetchCount();
  }

  public List<ProductCountByCategoryDto> countTodayDiscountProductEachCategory() {
    LocalDate cachedDate = productRepository.findCachedLastUpdatedDateTime().toLocalDate();
    return queryFactory.from(todayDiscountProduct)
        .where(todayDiscountProduct.modifiedDate.after(cachedDate.atStartOfDay())
            .and(todayDiscountProduct.percent.goe(MINIMUM_DISCOUNT_PERCENT)))
        .innerJoin(todayDiscountProduct.product)
        .groupBy(todayDiscountProduct.product.category)
        .select(Projections.constructor(ProductCountByCategoryDto.class,
            todayDiscountProduct.product.category, todayDiscountProduct.product.count()))
        .fetch();
  }

  private OrderSpecifier sort(Pageable pageable) {
    for (Sort.Order order : pageable.getSort()) {
      if (order.getProperty().trim().equals("price")) {
        return SortUtils.getOrderSpecifier(order, todayDiscountProduct.product.realPrice);
      }
      return SortUtils.getOrderSpecifier(order, TodayDiscountProduct.class);
    }
    return SortUtils.getOrderSpecifier(Order.DESC, todayDiscountProduct.percent);
  }

  private BooleanBuilder getBooleanFilter(FilterVo filterVo) {
    return new com.musinsa.watcher.domain.product.SearchFilter.Builder()
        .setBrands(todayDiscountProduct.product.brand, filterVo.getBrands())
        .setCategories(todayDiscountProduct.product.category, filterVo.getCategories())
        .setMaxPrice(todayDiscountProduct.product.realPrice, filterVo.getMaxPrice())
        .setMinPrice(todayDiscountProduct.product.realPrice, filterVo.getMinPrice())
        .build().getBooleanBuilder();
  }
}
