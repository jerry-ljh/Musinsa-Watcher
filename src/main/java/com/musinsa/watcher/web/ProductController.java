package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.web.dto.BrandDto;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnableCaching
@CrossOrigin("http://www.musinsa.cf/")
@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;
  private final String DEFAULT_PAGE = "0";
  
  @GetMapping("/api/v1/search/brands/list")
  public Page<String> findBrandList(@RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page) {
    return productService.findAllBrand(PageRequest.of(page, 25, Sort.by("brand")));
  }

  @GetMapping("/api/v1/search/brands")
  public List<BrandDto> findBrandByInitial(String type) {
    String[] initial = InitialWord.valueOf(InitialWord.getType(type)).getInitials();
    return productService.findBrandByInitial(initial[0], initial[1], initial[2]);
  }

  @GetMapping("/api/v1/search")
  public Page<ProductResponseDto> searchItem(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String topic) {
    return productService
        .searchItems(topic, PageRequest.of(page, 25, Sort.by("modifiedDate").descending()));
  }

  @GetMapping("/api/v1/product/discount")
  public Page<DiscountedProductDto> findDiscountedProduct(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String category) {
    return productService.findDiscountedProduct(category, PageRequest.of(page, 25));
  }

  @GetMapping("/api/v1/product/brand")
  public Page<ProductResponseDto> findProductByBrand(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String name) {
    return productService
        .findByBrand(name, PageRequest.of(page, 25, Sort.by("modifiedDate").descending()));
  }

  @GetMapping("/api/v1/product/list")
  public Page<ProductResponseDto> findProductByCategory(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String category) {
    return productService
        .findByCategory(category, PageRequest.of(page, 25));
  }

  @GetMapping("/api/v1/product")
  public ProductWithPriceResponseDto findProductWithPrice(@RequestParam int id) {
    return productService.findProductWithPrice(id);
  }

}
