package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.PageParameter;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.service.PriceService;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class PriceController {

  private final PriceService priceService;

  private final String DEFAULT_SORT = "percent desc";

  @GetMapping("/api/v1/product/price")
  public Page<PriceResponseDto> findByPostId(@RequestParam int id,
      @RequestParam(defaultValue = "30") int count) {
    return priceService
        .findByProductId(id, PageRequest.of(0, count, Sort.by("createdDate").descending()));
  }


  @GetMapping("/api/v1/product/discount")
  public Page<DiscountedProductDto> findDiscountedProduct(
      String category,
      @PageParameter PageRequest pageRequest,
      @RequestParam(required = false, defaultValue = DEFAULT_SORT) String sort) {
    return priceService.findDiscountedProduct(Category.getCategory(category), pageRequest,
        String.join(" ", sort.split("_")));
  }

  @GetMapping("/api/v1/product/minimum")
  public Page<MinimumPriceProductDto> findMinimumPriceProduct(
      @PageParameter PageRequest pageRequest,
      String category,
      @RequestParam(required = false, defaultValue = DEFAULT_SORT) String sort) {
    return priceService.findMinimumPriceProduct(Category.getCategory(category), pageRequest,
        String.join(" ", sort.split("_")));
  }

  @GetMapping("/api/v1/product/discount/list")
  public Map<String, Integer> findDiscountedProduct() {
    return priceService.countDiscountProductEachCategory();
  }

  @GetMapping("/api/v1/product/minimum/list")
  public Map<String, Integer> findMinimumPriceProduct() {
    return priceService.countMinimumPriceProductEachCategory();
  }
}
