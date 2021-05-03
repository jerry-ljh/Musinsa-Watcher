package com.musinsa.watcher.domain.product.slave;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.Filter;
import com.musinsa.watcher.domain.price.QPrice;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.QProduct;
import com.musinsa.watcher.web.dto.BrandCountDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class ProductQuerySlaveRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ApplicationContext applicationContext;

  private ProductQuerySlaveRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<ProductResponseDto> searchItems(String text, Filter filter, Pageable pageable) {
    BooleanBuilder builder = new BooleanFilter(filter).build();
    List<Product> results = queryFactory.selectFrom(QProduct.product)
        .where(builder.and(QProduct.product.brand.contains(text)
            .or(QProduct.product.productName.contains(text))))
        .orderBy(QProduct.product.modifiedDate.desc(), QProduct.product.productName.asc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countSearchItems(text, filter));
  }

  @Cacheable(value = "productCache", key = "'search count'+#text + #filter.toString()")
  public long countSearchItems(String text, Filter filter) {
    BooleanBuilder builder = new BooleanFilter(filter).build();
    return queryFactory.selectFrom(QProduct.product)
        .where(builder.and(QProduct.product.brand.contains(text)
            .or(QProduct.product.productName.contains(text))))
        .fetchCount();
  }

  public Map<String, Integer> searchBrand(String text) {
    List<BrandCountDto> results = queryFactory.from(QProduct.product)
        .where(Expressions.booleanTemplate("brand like '" + text + "%'"))
        .select(QProduct.product.brand, QProduct.product.brand.count())
        .groupBy(QProduct.product.brand)
        .orderBy(QProduct.product.brand.asc())
        .select(Projections
            .constructor(BrandCountDto.class, QProduct.product.brand,
                QProduct.product.count().as("count")))
        .fetch();
    return BrandCountDto.toMap(results);
  }

  public Product findByProductIdWithPrice(int productId) {
    return queryFactory.selectFrom(QProduct.product)
        .leftJoin(QProduct.product.prices, QPrice.price1)
        .fetchJoin()
        .where(QProduct.product.productId.eq(productId))
        .orderBy(QPrice.price1.createdDate.desc())
        .fetchOne();
  }

  public Page<ProductResponseDto> findByCategoryAndDate(Filter filter, LocalDate date,
      Pageable pageable) {
    BooleanBuilder builder = new BooleanFilter(filter).date(date).build();
    List<Product> results = queryFactory.selectFrom(QProduct.product)
        .where(builder)
        .orderBy(QProduct.product.rank.asc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countByCategoryAndDate(filter, date));
  }

  @Cacheable(value = "productCache", key = "'category count'+#filter.toString()")
  public long countByCategoryAndDate(Filter filter, LocalDate date) {
    BooleanBuilder builder = new BooleanFilter(filter)
        .date(date)
        .build();
    return queryFactory.from(QProduct.product)
        .where(builder)
        .fetchCount();
  }

  public Page<ProductResponseDto> findByBrand(Filter filter, Pageable pageable) {
    BooleanBuilder builder = new BooleanFilter(filter).build();
    List<Product> results = queryFactory.selectFrom(QProduct.product)
        .where(builder)
        .orderBy(QProduct.product.modifiedDate.desc(), QProduct.product.brand.asc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results.stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countByBrand(filter));
  }

  @Cacheable(value = "productCache", key = "'brand count'+#builder")
  public long countByBrand(Filter filter) {
    BooleanBuilder builder = new BooleanFilter(filter).build();
    return queryFactory.from(QProduct.product)
        .where(builder)
        .fetchCount();
  }

  public LocalDateTime findLastUpdatedDate() {
    return queryFactory.selectFrom(QProduct.product)
        .orderBy(QProduct.product.modifiedDate.desc())
        .select(QProduct.product.modifiedDate)
        .fetchFirst();
  }

  public Map<String, Integer> findBrandByInitial(String initial1, String initial2) {
    List<BrandCountDto> results = queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.goe(initial1).and(QProduct.product.brand.lt(initial2)))
        .groupBy(QProduct.product.brand)
        .orderBy(QProduct.product.brand.asc())
        .select(Projections.constructor(BrandCountDto.class, QProduct.product.brand,
            QProduct.product.count().as("count")))
        .fetch();
    return BrandCountDto.toMap(results);
  }


  private static class BooleanFilter {

    private String[] brands;
    private Category[] categories;
    private int minPrice;
    private int maxPrice;
    private LocalDate date;

    private BooleanFilter(Filter filter) {
      this.brands = filter.getBrands();
      this.categories = filter.getCategories();
      this.minPrice = filter.getMinPrice();
      this.maxPrice = filter.getMaxPrice();
    }

    private BooleanFilter brand(String[] brands) {
      this.brands = brands;
      return this;
    }

    private BooleanFilter category(Category[] categories) {
      this.categories = categories;
      return this;
    }

    private BooleanFilter minPrice(int minPrice) {
      this.minPrice = minPrice;
      return this;
    }

    private BooleanFilter maxPrice(int maxPrice) {
      this.maxPrice = maxPrice;
      return this;
    }

    private BooleanFilter date(LocalDate date) {
      this.date = date;
      return this;
    }

    private BooleanBuilder build() {
      BooleanBuilder filter = new BooleanBuilder();

      if (brands != null && brands.length > 0) {
        filter.and(QProduct.product.brand.in(brands));
      }
      if (categories != null && categories.length > 0) {
        filter.and(QProduct.product.category
            .in(Arrays.stream(categories).map(Category::getCategory).toArray(String[]::new)));
      }
      if (date != null) {
        filter.and(QProduct.product.modifiedDate.after(date.atStartOfDay()));
      }
      filter.and(QProduct.product.realPrice.goe(minPrice));
      filter.and(QProduct.product.realPrice.loe(maxPrice));
      return filter;
    }
  }
}
