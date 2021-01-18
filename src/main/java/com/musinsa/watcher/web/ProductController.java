package com.musinsa.watcher.web;

import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnableCaching
@CrossOrigin({"http://www.musinsa.cf/", "http://api.musinsa.cf/, https://www.musinsa.cf/",
    "https://api.musinsa.cf/"})
@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;
  private final String DEFAULT_PAGE = "0";
  private final String DEFAULT_PAGE_SIZE = "100";

  @GetMapping("/api/v1/search/brands/list")
  public Page<String> findBrandList(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService.findAllBrand(PageRequest.of(page, pagesize, Sort.by("brand")));
  }

  @GetMapping("/api/v1/search/brands")
  public Map<String, Integer> findBrandByInitial(String type) {
    Initial initial = InitialWord.valueOf(InitialWord.getType(type)).getInitials();
    return productService
        .findBrandByInitial(initial.getSTART(), initial.getEND());
  }

  @GetMapping("/api/v1/search")
  public Page<ProductResponseDto> searchItem(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String topic,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService
        .searchItems(topic, PageRequest.of(page, pagesize, Sort.by("modifiedDate").descending()));
  }

  @GetMapping("/api/v1/product/discount")
  public Page<DiscountedProductDto> findDiscountedProduct(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String category,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService.findDiscountedProduct(category, PageRequest.of(page, pagesize));
  }

  @GetMapping("/api/v1/product/minimum")
  public Page<MinimumPriceProductDto> findMinimumPriceProduct(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String category,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService.findMinimumPriceProduct(category, PageRequest.of(page, pagesize));
  }

  @GetMapping("/api/v1/product/discount/list")
  public Map<String, Integer> findDiscountedProduct() {
    return productService.countDiscountProductEachCategory();
  }

  @GetMapping("/api/v1/product/minimum/list")
  public Map<String, Integer> findMinimumPriceProduct() {
    return productService.countMinimumPriceProductEachCategory();
  }

  @GetMapping("/api/v1/product/brand")
  public Page<ProductResponseDto> findProductByBrand(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String name,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService
        .findByBrand(name, PageRequest.of(page, pagesize, Sort.by("modifiedDate").descending()));
  }

  @GetMapping("/api/v1/product/list")
  public Page<ProductResponseDto> findProductByCategory(
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page, String category,
      @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    return productService
        .findByCategory(category, PageRequest.of(page, pagesize));
  }

  @GetMapping("/api/v1/product")
  public ProductWithPriceResponseDto findProductWithPrice(@RequestParam int id) {
    return productService.findProductWithPrice(id);
  }

  @GetMapping("/api/product/link")
  public void outboundLog() {
  }

}
