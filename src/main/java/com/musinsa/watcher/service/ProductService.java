package com.musinsa.watcher.service;

import com.musinsa.watcher.web.Filter;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductQuerySlaveRepository productQuerySlaveRepository;
  private final CacheService cacheService;

  public Page<ProductResponseDto> findByBrand(Filter filter, Pageable pageable) {
    return productQuerySlaveRepository.findByBrand(filter, pageable);
  }

  public Map<String, Integer> searchBrand(String brand) {
    return productQuerySlaveRepository.searchBrand(brand);
  }

  @Cacheable(value = "productCache", key = "'category'+#filter.toString()+#pageable.pageNumber+#pageable.pageSize", condition = "#pageable.pageNumber==0")
  public Page<ProductResponseDto> findByCategory(Filter filter, Pageable pageable) {
    return productQuerySlaveRepository
        .findByCategoryAndDate(filter, cacheService.getLastUpdatedDate(), pageable);
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(
        productQuerySlaveRepository.findByProductIdWithPrice(productId));
  }

  @Cacheable(value = "productCache", key = "'brand-initial'+#initial1+#initial2")
  public Map<String, Integer> findBrandByInitial(String initial1, String initial2) {
    return productQuerySlaveRepository.findBrandByInitial(initial1, initial2);
  }

  public Page<ProductResponseDto> searchItems(String text, Filter filter, Pageable pageable) {
    return productQuerySlaveRepository.searchItems(text, filter, pageable);
  }

}
