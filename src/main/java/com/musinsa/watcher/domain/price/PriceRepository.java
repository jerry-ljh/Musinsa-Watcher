package com.musinsa.watcher.domain.price;

import com.musinsa.watcher.domain.price.slave.PriceSlaveQueryRepository;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import java.time.LocalDate;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PriceRepository {

  private final PriceSlaveRepository priceSlaveRepository;
  private final PriceSlaveQueryRepository priceSlaveQueryRepository;

  public Page<MinimumPriceProductDto> findTodayMinimumPriceProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    return priceSlaveQueryRepository
        .findTodayMinimumPriceProducts(category, localDate, pageable, sort);
  }

  public long countTodayMinimumPriceProducts(Category category, LocalDate localDate) {
    return priceSlaveQueryRepository.countTodayMinimumPriceProducts(category, localDate);
  }

  public Map<String, Integer> countMinimumPriceProductEachCategory(LocalDate localDate) {
    return priceSlaveQueryRepository.countMinimumPriceProductEachCategory(localDate);
  }

  public Page<DiscountedProductDto> findTodayDiscountProducts(Category category,
      LocalDate localDate, Pageable pageable, String sort) {
    return priceSlaveQueryRepository.findTodayDiscountProducts(category, localDate, pageable, sort);
  }

  public long countTodayDiscountProducts(Category category, LocalDate localDate) {
    return priceSlaveQueryRepository.countTodayDiscountProducts(category, localDate);
  }

  public Map<String, Integer> countDiscountProductEachCategory(LocalDate localDate) {
    return priceSlaveQueryRepository.countDiscountProductEachCategory(localDate);
  }

  public Page<Price> findByProductId(int productId, Pageable pageable) {
    return priceSlaveRepository.findByProductId(productId, pageable);
  }
}
