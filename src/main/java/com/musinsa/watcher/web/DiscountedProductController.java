package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.service.DiscountedProductService;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiscountedProductController {

  private final DiscountedProductService discountedProductService;

  private final String DEFAULT_SORT = "percent desc";

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/discount/today/list")
  public Page<TodayDiscountedProductDto> findDiscountedProductsPage(String category,
      Pageable pageable) {
    return discountedProductService.findDiscountedProduct(Category.getCategory(category), pageable);
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/discount/today/count")
  public ProductCountMapByCategoryDto findDiscountedProductsMapByCategory() {
    return discountedProductService.countDiscountProductEachCategory();
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/minimum-price/today/list")
  public Page<TodayMinimumPriceProductDto> findMinimumPriceProductsPage(Pageable pageable,
      String category) {
    return discountedProductService
        .findMinimumPriceProduct(Category.getCategory(category), pageable);
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/minimum-price/today/count")
  public ProductCountMapByCategoryDto findMinimumPriceProductsMapByCategory() {
    return discountedProductService.countMinimumPriceProductEachCategory();
  }
}
