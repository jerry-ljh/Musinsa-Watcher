package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.service.ProductService;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;

  @GetMapping("/api/v1/product/brand/list")
  public Page<String> findBrandList(@RequestParam(required = false, defaultValue = "0") int page) {
    return productService.findAllbrand(PageRequest.of(page, 50, Sort.by("brand")));
  }

  @GetMapping("/api/v1/product/brand")
  public Page<ProductResponseDto> findBrandList(
      @RequestParam(required = false, defaultValue = "0") int page, String name) {
    return productService.findByBrand(name, PageRequest.of(page, 50, Sort.by("productName")));
  }

  @GetMapping("/api/v1/product/list")
  public Page<ProductResponseDto> findProductByCategory(
      @RequestParam(required = false, defaultValue = "0") int page, String category) {
    return productService
        .findByCategory(category, PageRequest.of(page, 50, Sort.by("productName")));
  }

}
