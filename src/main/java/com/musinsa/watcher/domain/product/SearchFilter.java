package com.musinsa.watcher.domain.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.time.LocalDateTime;
import java.util.Arrays;

public class SearchFilter {

  private final BooleanBuilder booleanBuilder;

  private SearchFilter(BooleanBuilder booleanBuilder) {
    this.booleanBuilder = booleanBuilder;
  }

  public BooleanBuilder getBooleanBuilder() {
    return this.booleanBuilder;
  }

  public static class Builder {

    private final BooleanBuilder booleanBuilder;

    public Builder() {
      this.booleanBuilder = new BooleanBuilder();
    }

    public Builder setBrands(StringPath path, String[] brands) {
      if (brands != null && brands.length > 0) {
        this.booleanBuilder.and(path.in(brands));
      }
      return this;
    }

    public Builder setCategories(StringPath path, Category[] categories) {
      if (categories != null && categories.length > 0) {
        this.booleanBuilder.and(
            path.in(Arrays.stream(categories).map(Category::getCategory).toArray(String[]::new)));
      }
      return this;
    }

    public Builder setMaxPrice(NumberPath<Integer> path, Integer maxPrice) {
      if (maxPrice != null) {
        this.booleanBuilder.and(path.loe(maxPrice));
      }
      return this;
    }

    public Builder setMinPrice(NumberPath<Integer> path, Integer minPrice) {
      if (minPrice != null) {
        this.booleanBuilder.and(path.goe(minPrice));
      }
      return this;
    }

    public Builder setOnlyTodayUpdatedData(DateTimePath<LocalDateTime> path,
        Boolean onlyTodayUpdatedData, LocalDateTime localDateTime) {
      if (onlyTodayUpdatedData !=null && onlyTodayUpdatedData) {
        this.booleanBuilder.and(path.after(localDateTime.toLocalDate().atStartOfDay()));
      }
      return this;
    }

    public SearchFilter build() {
      return new SearchFilter(booleanBuilder);
    }
  }
}
