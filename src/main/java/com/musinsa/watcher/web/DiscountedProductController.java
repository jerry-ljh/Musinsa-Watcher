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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class DiscountedProductController {

  private final DiscountedProductService discountedProductService;

  private final String DEFAULT_SORT = "percent desc";

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/discount/today/list")
  public Page<TodayDiscountedProductDto> findDiscountedProductsPage(String category,
      Pageable pageable, @RequestParam(required = false, defaultValue = DEFAULT_SORT) String sort) {
    return discountedProductService.findDiscountedProduct(Category.getCategory(category),
        pageable, String.join(" ", sort.split("_")));
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/discount/today/count")
  public ProductCountMapByCategoryDto findDiscountedProductsMapByCategory() {
    return discountedProductService.countDiscountProductEachCategory();
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/minimum-price/today/list")
  public Page<TodayMinimumPriceProductDto> findMinimumPriceProductsPage(Pageable pageable,
      String category, @RequestParam(required = false, defaultValue = DEFAULT_SORT) String sort) {
    return discountedProductService.findMinimumPriceProduct(Category.getCategory(category),
        pageable, String.join(" ", sort.split("_")));
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/minimum-price/today/count")
  public ProductCountMapByCategoryDto findMinimumPriceProductsMapByCategory() {
    return discountedProductService.countMinimumPriceProductEachCategory();
  }
}
