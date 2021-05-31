package com.musinsa.watcher.web;

import com.musinsa.watcher.config.webparameter.SearchFilter;
import com.musinsa.watcher.domain.product.InitialWord;
import com.musinsa.watcher.service.ProductService;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.web.dto.ProductCountMapByBrandDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class SearchController {

  private final ProductService productService;

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/search/brand-initial")
  public ProductCountMapByBrandDto searchBrandByInitial(String type) {
    InitialWord initialWord = InitialWord.valueOf(InitialWord.getType(type));
    return productService.searchBrandByInitial(initialWord.getStart(), initialWord.getEnd());
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/search/brand")
  public ProductCountMapByBrandDto searchBrandByName(String name) {
    return productService.searchBrandByName(name);
  }

  @Cacheable(value = "productCache")
  @GetMapping("/api/v1/search/product")
  public Page<ProductResponseDto> searchProduct(String topic, Pageable pageable,
      @SearchFilter FilterVo filterVo) {
    return productService.searchProduct(topic, filterVo, pageable);
  }
}
