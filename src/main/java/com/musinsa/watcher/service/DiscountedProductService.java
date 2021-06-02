package com.musinsa.watcher.service;

import com.musinsa.watcher.config.webparameter.FilterVo;
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

  public Page<TodayDiscountedProductDto> findDiscountedProduct(FilterVo filterVo,
      Pageable pageable) {
    return discountedRepository.findTodayDiscountedProducts(filterVo, pageable);
  }

  public ProductCountMapByCategoryDto countDiscountProductEachCategory() {
    return new ProductCountMapByCategoryDto(
        discountedRepository.countDiscountProductEachCategory());
  }

  public Page<TodayMinimumPriceProductDto> findMinimumPriceProduct(FilterVo filterVo,
      Pageable pageable) {
    return discountedRepository.findTodayMinimumPriceProducts(filterVo, pageable);
  }

  public ProductCountMapByCategoryDto countMinimumPriceProductEachCategory() {
    return new ProductCountMapByCategoryDto(
        discountedRepository.countMinimumPriceProductEachCategory());
  }
}
