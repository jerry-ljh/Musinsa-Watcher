package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.domain.product.discount.DiscountedRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiscountedProductService {

  private final DiscountedRepository discountedRepository;

  public Page<TodayDiscountedProductDto> findDiscountedProduct(Category category,
      Pageable pageable) {
    return discountedRepository.findTodayDiscountedProducts(category, pageable);
  }

  public ProductCountMapByCategoryDto countDiscountProductEachCategory() {
    return new ProductCountMapByCategoryDto(
        discountedRepository.countDiscountProductEachCategory());
  }

  public Page<TodayMinimumPriceProductDto> findMinimumPriceProduct(Category category,
      Pageable pageable) {
    return discountedRepository.findTodayMinimumPriceProducts(category, pageable);
  }

  public ProductCountMapByCategoryDto countMinimumPriceProductEachCategory() {
    return new ProductCountMapByCategoryDto(
        discountedRepository.countMinimumPriceProductEachCategory());
  }
}
