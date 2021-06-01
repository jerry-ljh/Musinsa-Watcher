package com.musinsa.watcher.domain.product.discount;

import com.musinsa.watcher.domain.product.ProductCountByCategoryDto;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.discount.slave.TodayDiscountedProductQueryRepository;
import com.musinsa.watcher.domain.product.discount.slave.TodayMinimumProductQueryRepository;
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

  private final TodayDiscountedProductQueryRepository todayDiscountedProductQueryRepository;
  private final TodayMinimumProductQueryRepository todayMinimumProductQueryRepository;

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      Pageable pageable) {
    return todayMinimumProductQueryRepository
        .findTodayMinimumPriceProducts(category, pageable);
  }

  public List<ProductCountByCategoryDto> countMinimumPriceProductEachCategory() {
    return todayMinimumProductQueryRepository.countTodayMinimumPriceProductEachCategory();
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountedProducts(Category category,
      Pageable pageable) {
    return todayDiscountedProductQueryRepository
        .findTodayDiscountProducts(category, pageable);
  }

  public List<ProductCountByCategoryDto> countDiscountProductEachCategory() {
    return todayDiscountedProductQueryRepository.countTodayDiscountProductEachCategory();
  }

}
