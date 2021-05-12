package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.PageParameter;
import com.musinsa.watcher.config.webparameter.Parameter;
import com.musinsa.watcher.config.webparameter.ParameterFilter;
import com.musinsa.watcher.domain.product.Initial;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.web.dto.Filter;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;

  @GetMapping("/api/v1/search/brands")
  public Map<String, Integer> findBrandByInitial(String type) {
    Initial initial = InitialWord.valueOf(InitialWord.getType(type)).getInitials();
    return productService
        .findBrandByInitial(initial.getSTART(), initial.getEND());
  }

  @GetMapping("/api/v1/search/brand")
  public Map<String, Integer> findBrand(String name) {
    return productService.searchBrand(name);
  }

  @GetMapping("/api/v1/search")
  public Page<ProductResponseDto> searchItem(String topic, @PageParameter PageRequest pageRequest,
      @ParameterFilter Filter filter) {
    return productService.searchItems(topic, filter, pageRequest);
  }

  @GetMapping("/api/v1/product/brand")
  public Page<ProductResponseDto> findProductByBrand(@PageParameter PageRequest pageRequest,
      @ParameterFilter(necessary = Parameter.BRAND) Filter filter) {
    return productService.findByBrand(filter, pageRequest);
  }

  @GetMapping("/api/v1/product/list")
  public Page<ProductResponseDto> findProductByCategory(@PageParameter PageRequest pageRequest,
      @ParameterFilter(necessary = Parameter.CATEGORY) Filter filter) {
    return productService.findByCategory(filter, pageRequest);
  }

  @GetMapping("/api/v1/product")
  public ProductWithPriceResponseDto findProductWithPrice(@RequestParam int id) {
    return productService.findProductWithPrice(id);
  }

  @GetMapping("/api/product/link")
  public void outboundLog() {
  }

  @GetMapping("/api/v1/product/cache/last-modified")
  public String updateDate() {
    return productService.getCachedLastUpdatedDateTime().toString();
  }

  @GetMapping("/api/v1/product/last-modified")
  public void updateCacheByDate() {
    productService.clearCacheIfOld(productService.getLastUpdatedDateTime(), productService.getCachedLastUpdatedDateTime());
  }
}
