package com.musinsa.watcher.domain.product.discount;

import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.discount.slave.DiscountedProductQueryRepository;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DiscountedRepository {

  private final DiscountedProductQueryRepository discountedProductQueryRepository;

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      Pageable pageable, String sort) {
    return discountedProductQueryRepository
        .findTodayMinimumPriceProducts(category, sort, pageable);
  }

  public List<ProductCountByCategoryDto> countMinimumPriceProductEachCategory() {
    return discountedProductQueryRepository.countTodayMinimumPriceProductEachCategory();
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountedProducts(Category category,
      Pageable pageable, String sort) {
    return discountedProductQueryRepository
        .findTodayDiscountProducts(category, sort, pageable);
  }

  public List<ProductCountByCategoryDto> countDiscountProductEachCategory() {
    return discountedProductQueryRepository.countTodayDiscountProductEachCategory();
  }

}