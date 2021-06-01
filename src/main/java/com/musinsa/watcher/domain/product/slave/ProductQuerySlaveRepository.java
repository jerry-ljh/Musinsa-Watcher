package com.musinsa.watcher.domain.product.slave;

import static com.musinsa.watcher.domain.product.QProduct.product;
import static com.musinsa.watcher.domain.price.QPrice.price1;

import com.musinsa.watcher.SortUtils;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductCountByBrandDto;
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
    BooleanBuilder builder = new BooleanFilter(filterVo).build();
    List<Product> results = queryFactory.selectFrom(product)
        .where(builder.and(product.brand.contains(text)
            .or(product.productName.contains(text))))
        .orderBy(product.modifiedDate.desc(), sortOrDefault(pageable, product.productName.asc()))
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
    BooleanBuilder builder = new BooleanFilter(filterVo).build();
    return queryFactory.selectFrom(product)
        .where(builder.and(product.brand.contains(text)
            .or(product.productName.contains(text))))
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
    BooleanBuilder builder = new BooleanFilter(filterVo).date(cachedDate).build();
    List<Product> results = queryFactory.selectFrom(product)
        .where(builder)
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
    BooleanBuilder builder = new BooleanFilter(filterVo).date(cachedDate).build();
    return queryFactory.from(product)
        .where(builder)
        .fetchCount();
  }

  public Page<ProductResponseDto> findByBrand(FilterVo filterVo, Pageable pageable) {
    BooleanBuilder builder = new BooleanFilter(filterVo).build();
    List<Product> results = queryFactory.selectFrom(product)
        .where(builder)
        .orderBy(convertToLocalDateFormat(product.modifiedDate).desc(),
            sortOrDefault(pageable, product.brand.asc()))
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
    BooleanBuilder builder = new BooleanFilter(filterVo).build();
    return queryFactory.from(product)
        .where(builder)
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
    return Expressions.dateTemplate(LocalDate.class, "{0}", expression);
  }

  private static class BooleanFilter {

    private String[] brands;
    private Category[] categories;
    private int minPrice;
    private int maxPrice;
    private LocalDate date;

    private BooleanFilter(FilterVo filterVo) {
      this.brands = filterVo.getBrands();
      this.categories = filterVo.getCategories();
      this.minPrice = filterVo.getMinPrice();
      this.maxPrice = filterVo.getMaxPrice();
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
        filter.and(product.brand.in(brands));
      }
      if (categories != null && categories.length > 0) {
        filter.and(product.category
            .in(Arrays.stream(categories).map(Category::getCategory).toArray(String[]::new)));
      }
      if (date != null) {
        filter.and(product.modifiedDate.after(date.atStartOfDay()));
      }
      filter.and(product.realPrice.goe(minPrice));
      filter.and(product.realPrice.loe(maxPrice));
      return filter;
    }
  }
}
