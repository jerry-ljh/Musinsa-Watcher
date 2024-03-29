package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.SearchFilter;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/brand")
  public Page<ProductResponseDto> findProductsPageByBrand(Pageable pageable,
      @SearchFilter FilterVo filterVo) {
    return productService.findProductsPageByBrand(filterVo, pageable);
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product/list")
  public Page<ProductResponseDto> findProductsPageByCategory(
      @SearchFilter FilterVo filterVo, Pageable pageable) {
    return productService.findProductsPageByCategory(filterVo, pageable);
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/product")
  public ProductWithPriceResponseDto findProductWithPrice(@RequestParam int id) {
    return productService.findProductWithPrice(id);
  }

  @GetMapping("/api/v1/product/cache/last-modified")
  public LocalDateTime findCachedLastProductUpdatedDate() {
    return productService.getCachedLastUpdatedDateTime();
  }

}
