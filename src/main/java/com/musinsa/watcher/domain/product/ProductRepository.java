package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.price.dto.ProductCountByBrandDto;
import com.musinsa.watcher.web.dto.Filter;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

  private final ProductQuerySlaveRepository productQuerySlaveRepository;

  public Page<ProductResponseDto> searchItems(String text, Filter filter, Pageable pageable) {
    return productQuerySlaveRepository.searchItems(text, filter, pageable);
  }

  public List<ProductCountByBrandDto> searchBrand(String text) {
    return productQuerySlaveRepository.searchBrand(text);
  }

  public Product findByProductIdWithPrice(int productId) {
    return productQuerySlaveRepository.findByProductIdWithPrice(productId);
  }

  public Page<ProductResponseDto> findByCategoryAndDate(Filter filter, LocalDate date,
      Pageable pageable) {
    return productQuerySlaveRepository.findByCategoryAndDate(filter, date, pageable);
  }

  public Page<ProductResponseDto> findByBrand(Filter filter, Pageable pageable) {
    return productQuerySlaveRepository.findByBrand(filter, pageable);
  }

  public LocalDateTime findLastUpdatedDate() {
    return productQuerySlaveRepository.findLastUpdatedDate();
  }

  @Cacheable(value = "productCache", key = "'current date'")
  public LocalDateTime findCachedLastUpdatedDateTime() {
    return findLastUpdatedDate();
  }

  public List<ProductCountByBrandDto> findBrandByInitial(String initial1, String initial2) {
    return productQuerySlaveRepository.findBrandByInitial(initial1, initial2);
  }

}