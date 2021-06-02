package com.musinsa.watcher.domain.product.slave;

import static com.musinsa.watcher.domain.product.QProduct.product;
import static com.musinsa.watcher.domain.price.QPrice.price1;
import static com.musinsa.watcher.domain.product.discount.QTodayMinimumPriceProduct.todayMinimumPriceProduct;

import com.musinsa.watcher.SortUtils;
import com.musinsa.watcher.config.cache.CacheName;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductCountByBrandDto;
import com.musinsa.watcher.domain.product.SearchFilter;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductQuerySlaveRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ApplicationContext applicationContext;

  private ProductQuerySlaveRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<ProductResponseDto> searchItems(String text, FilterVo filterVo, Pageable pageable) {
    List<Product> results = queryFactory.selectFrom(product)
        .where(getBooleanFilter(filterVo)
            .and(product.brand.contains(text).or(product.productName.contains(text))))
        .orderBy(sortOrDefault(pageable, product.modifiedDate.desc()))
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results.stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countSearchItems(text, filterVo));
  }

  @Cacheable(value = "productCache")
  public long countSearchItems(String text, FilterVo filterVo) {
    return queryFactory.selectFrom(product)
        .where(getBooleanFilter(filterVo)
            .and(product.brand.contains(text).or(product.productName.contains(text))))
        .fetchCount();
  }

  public List<ProductCountByBrandDto> searchBrand(String text) {
    return queryFactory.from(product)
        .where(Expressions.booleanTemplate("brand like '" + text + "%'"))
        .select(product.brand, product.brand.count())
        .groupBy(product.brand)
        .orderBy(product.brand.asc())
        .select(Projections.constructor(ProductCountByBrandDto.class, product.brand,
            product.count()))
        .fetch();
  }

  public Product findByProductIdWithPrice(int productId) {
    return queryFactory.selectFrom(product)
        .leftJoin(product.prices, price1)
        .fetchJoin()
        .where(product.productId.eq(productId))
        .orderBy(price1.createdDate.desc())
        .fetchOne();
  }

  public Page<ProductResponseDto> findTodayUpdatedProductByCategory(FilterVo filterVo,
      Pageable pageable) {
    LocalDate cachedDate = this.getSpringProxy().findCachedLastUpdatedDate().toLocalDate();
    List<Product> results = queryFactory.selectFrom(product)
        .where(getBooleanFilter(filterVo)
            .and(product.modifiedDate.after(cachedDate.atStartOfDay())))
        .orderBy(sortOrDefault(pageable, product.rank.asc()))
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countTodayUpdatedProductByCategory(filterVo));
  }

  @Cacheable(value = "productCache")
  public long countTodayUpdatedProductByCategory(FilterVo filterVo) {
    LocalDate cachedDate = this.getSpringProxy().findCachedLastUpdatedDate().toLocalDate();
    return queryFactory.from(product)
        .where(getBooleanFilter(filterVo)
            .and(product.modifiedDate.after(cachedDate.atStartOfDay())))
        .fetchCount();
  }

  public Page<ProductResponseDto> findByBrand(FilterVo filterVo, Pageable pageable) {
    List<Product> results = queryFactory.selectFrom(product)
        .where(getBooleanFilter(filterVo))
        .orderBy(sortOrDefault(pageable, product.modifiedDate.desc()))
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results.stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countByBrand(filterVo));
  }

  @Cacheable(value = "productCache")
  public long countByBrand(FilterVo filterVo) {
    return queryFactory.from(product)
        .where(getBooleanFilter(filterVo))
        .fetchCount();
  }

  public LocalDateTime findLastUpdatedDate() {
    return queryFactory.selectFrom(product)
        .orderBy(product.modifiedDate.desc())
        .select(product.modifiedDate)
        .fetchFirst();
  }

  @Cacheable(value = "productCache")
  public LocalDateTime findCachedLastUpdatedDate() {
    return findLastUpdatedDate();
  }

  public List<ProductCountByBrandDto> findBrandByInitial(String initial1, String initial2) {
    return queryFactory.from(product)
        .where(product.brand.goe(initial1).and(product.brand.lt(initial2)))
        .groupBy(product.brand)
        .orderBy(product.brand.asc())
        .select(Projections.constructor(ProductCountByBrandDto.class, product.brand,
            product.count()))
        .fetch();
  }

  private OrderSpecifier sortOrDefault(Pageable pageable, OrderSpecifier defaultSort) {
    for (Sort.Order order : pageable.getSort()) {
      if (order.getProperty().trim().equals("price")) {
        return SortUtils.getOrderSpecifier(order, product.realPrice);
      }
      return SortUtils.getOrderSpecifier(order, Product.class);
    }
    return defaultSort;
  }

  private DateTemplate<LocalDate> convertToLocalDateFormat(Expression expression) {
    return Expressions.dateTemplate(LocalDate.class, "  {0}", expression);
  }

  private BooleanBuilder getBooleanFilter(FilterVo filterVo) {
    return new SearchFilter.Builder()
        .setBrands(product.brand, filterVo.getBrands())
        .setCategories(product.category, filterVo.getCategories())
        .setMaxPrice(product.realPrice, filterVo.getMaxPrice())
        .setMinPrice(product.realPrice, filterVo.getMinPrice())
        .setOnlyTodayUpdatedData(product.modifiedDate, filterVo.getOnlyTodayUpdatedData(),
            findCachedLastUpdatedDate())
        .build().getBooleanBuilder();
  }

}
