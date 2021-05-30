package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.PriceRepository;
import com.musinsa.watcher.domain.product.Category;
import com.musinsa.watcher.web.dto.TodayDiscountedProductDto;
import com.musinsa.watcher.web.dto.TodayMinimumPriceProductDto;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import com.musinsa.watcher.web.dto.ProductCountMapByCategoryDto;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PriceService {

  private final PriceRepository priceRepository;
  private final ProductService productService;

  public Page<PriceResponseDto> findByProductId(int productId, Pageable pageable) {
    Page<Price> page = priceRepository.findByProductId(productId, pageable);
    return new PageImpl<>(page.getContent()
        .stream()
        .map(PriceResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }

  @Cacheable(value = "productCache", key = "'distcount'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<TodayDiscountedProductDto> findDiscountedProduct(Category category, Pageable pageable,
      String sort) {
    LocalDate cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime().toLocalDate();
    return priceRepository
        .findTodayDiscountProducts(category, cachedLastUpdatedDate, pageable, sort);
  }

  @Cacheable(value = "productCache", key = "'distcount list'")
  public ProductCountMapByCategoryDto countDiscountProductEachCategory() {
    LocalDate cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime().toLocalDate();
    return new ProductCountMapByCategoryDto(
        priceRepository.countDiscountProductEachCategory(cachedLastUpdatedDate));
  }

  @Cacheable(value = "productCache", key = "'minimum'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<TodayMinimumPriceProductDto> findMinimumPriceProduct(Category category, Pageable pageable,
      String sort) {
    LocalDate cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime().toLocalDate();
    return priceRepository
        .findTodayMinimumPriceProducts(category, cachedLastUpdatedDate, pageable, sort);
  }

  @Cacheable(value = "productCache", key = "'minimum price list'")
  public ProductCountMapByCategoryDto countMinimumPriceProductEachCategory() {
    LocalDate cachedLastUpdatedDate = productService.getCachedLastUpdatedDateTime().toLocalDate();
    return new ProductCountMapByCategoryDto(
        priceRepository.countMinimumPriceProductEachCategory(cachedLastUpdatedDate));
  }
}
