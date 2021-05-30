package com.musinsa.watcher.domain.price;

import com.musinsa.watcher.domain.price.slave.PriceSlaveQueryRepository;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.price.dto.ProductCountByCategoryDto;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PriceRepository {

  private final PriceSlaveRepository priceSlaveRepository;
  private final PriceSlaveQueryRepository priceSlaveQueryRepository;

  public Page<TodayMinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    return priceSlaveQueryRepository
        .findTodayMinimumPriceProducts(category, localDate, pageable, sort);
  }

  public List<ProductCountByCategoryDto> countMinimumPriceProductEachCategory(LocalDate localDate) {
    return priceSlaveQueryRepository.countMinimumPriceProductEachCategory(localDate);
  }

  public Page<TodayDiscountedProductDto> findTodayDiscountProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    return priceSlaveQueryRepository.findTodayDiscountProducts(category, localDate, pageable, sort);
  }

  public List<ProductCountByCategoryDto> countDiscountProductEachCategory(LocalDate localDate) {
    return priceSlaveQueryRepository.countDiscountProductEachCategory(localDate);
  }

  public Page<Price> findByProductId(int productId, Pageable pageable) {
    return priceSlaveRepository.findByProductId(productId, pageable);
  }
}
