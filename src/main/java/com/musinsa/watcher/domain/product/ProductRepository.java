package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

  private final ProductQuerySlaveRepository productQuerySlaveRepository;

  public Page<ProductResponseDto> searchItems(String text, FilterVo filterVo, Pageable pageable) {
    return productQuerySlaveRepository.searchItems(text, filterVo, pageable);
  }

  public List<ProductCountByBrandDto> searchBrand(String text) {
    return productQuerySlaveRepository.searchBrand(text);
  }

  public Product findByProductIdWithPrice(int productId) {
    return productQuerySlaveRepository.findByProductIdWithPrice(productId);
  }

  public Page<ProductResponseDto> findProductByCategoryAndDate(FilterVo filterVo, Pageable pageable) {
    return productQuerySlaveRepository.findTodayUpdatedProductByCategory(filterVo, pageable);
  }

  public Page<ProductResponseDto> findProductByBrand(FilterVo filterVo, Pageable pageable) {
    return productQuerySlaveRepository.findByBrand(filterVo, pageable);
  }

  public LocalDateTime findLastUpdatedDate() {
    return productQuerySlaveRepository.findLastUpdatedDate();
  }

  public LocalDateTime findCachedLastUpdatedDateTime() {
    return productQuerySlaveRepository.findCachedLastUpdatedDate();
  }

  public List<ProductCountByBrandDto> findBrandByInitial(String initial1, String initial2) {
    return productQuerySlaveRepository.findBrandByInitial(initial1, initial2);
  }

}