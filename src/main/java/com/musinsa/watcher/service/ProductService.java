package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.ProductCountMapByBrandDto;
import com.musinsa.watcher.config.webparameter.FilterVo;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductCountMapByBrandDto searchBrandByName(String brand) {
    return new ProductCountMapByBrandDto(productRepository.searchBrand(brand));
  }

  public Page<ProductResponseDto> searchProduct(String text, FilterVo filterVo,
      Pageable pageable) {
    return productRepository.searchItems(text, filterVo, pageable);
  }

  public ProductCountMapByBrandDto searchBrandByInitial(String initial1, String initial2) {
    return new ProductCountMapByBrandDto(productRepository.findBrandByInitial(initial1, initial2));
  }

  public Page<ProductResponseDto> findProductsPageByBrand(FilterVo filterVo, Pageable pageable) {
    return productRepository.findProductByBrand(filterVo, pageable);
  }

  public Page<ProductResponseDto> findProductsPageByCategory(FilterVo filterVo,
      Pageable pageable) {
    return productRepository.findProductByCategoryAndDate(filterVo, pageable);
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(productRepository.findByProductIdWithPrice(productId));
  }

  public LocalDateTime getLastUpdatedDateTime() {
    return productRepository.findLastUpdatedDate();
  }

  public LocalDateTime getCachedLastUpdatedDateTime() {
    return productRepository.findCachedLastUpdatedDateTime();
  }

}
