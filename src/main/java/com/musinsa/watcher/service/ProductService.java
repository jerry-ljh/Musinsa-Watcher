package com.musinsa.watcher.service;

import com.musinsa.watcher.web.Filter;
import com.musinsa.watcher.MapperUtils;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.slave.ProductQuerySlaveRepository;
import com.musinsa.watcher.domain.product.slave.ProductSlaveRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductSlaveRepository productSlaveRepository;
  private final ProductQuerySlaveRepository productQuerySlaveRepository;
  private final CacheService cacheService;

  public Page<String> findAllBrand(Pageable pageable) {
    return productSlaveRepository.findAllBrand(pageable);
  }

  public Page<ProductResponseDto> findByBrand(Filter filter, Pageable pageable) {
    List<Product> list = productQuerySlaveRepository
        .findByBrand(filter, pageable);
    long count = productQuerySlaveRepository.countByBrand(filter);
    return ProductResponseDto.convertPage(list, pageable, count);
  }

  public Map<String, Integer> searchBrand(String brand) {
    List<Object[]> objectList = productQuerySlaveRepository.searchBrand(brand);
    return objectList.size() > 0 ? MapperUtils.longToIntegerMapper(objectList) : null;
  }

  @Cacheable(value = "productCache", key = "'category'+#filter.toString()+#pageable.pageNumber+#pageable.pageSize", condition = "#pageable.pageNumber==0")
  public Page<ProductResponseDto> findByCategory(Filter filter, Pageable pageable) {
    List<Product> list = productQuerySlaveRepository
        .findByCategoryAndDate(filter, cacheService.getLastUpdatedDate(), pageable);
    long count = productQuerySlaveRepository
        .countByCategoryAndDate(filter, cacheService.getLastUpdatedDate());
    return ProductResponseDto.convertPage(list, pageable, count);
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(
        productQuerySlaveRepository.findByProductIdWithPrice(productId));
  }

  @Cacheable(value = "productCache", key = "'brand-initial'+#initial1+#initial2")
  public Map<String, Integer> findBrandByInitial(String initial1, String initial2) {
    List<Object[]> objectList = productQuerySlaveRepository.findBrandByInitial(initial1, initial2);
    return MapperUtils.longToIntegerMapper(objectList);
  }

  public Page<ProductResponseDto> searchItems(String text, Filter filter, Pageable pageable) {
    List<Product> list = productQuerySlaveRepository.searchItems(text, filter, pageable);
    long count = productQuerySlaveRepository.countSearchItems(text, filter);
    return ProductResponseDto.convertPage(list, pageable, count);
  }

  @Cacheable(value = "productCache", key = "'distcount'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<DiscountedProductDto> findDiscountedProduct(String category, Pageable pageable,
      String sort) {
    List<Object[]> result = productQuerySlaveRepository
        .findDiscountByCategoryAndDate(category, cacheService.getLastUpdatedDate(),
            pageable.getOffset(), pageable.getPageSize(), sort);
    long count = productSlaveRepository
        .countDiscountByCategoryAndDate(category, cacheService.getLastUpdatedDate());
    return DiscountedProductDto.convertPage(result, pageable, count);
  }

  @Cacheable(value = "productCache", key = "'minimum'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<MinimumPriceProductDto> findMinimumPriceProduct(String category, Pageable pageable,
      String sort) {
    List<Object[]> results = productQuerySlaveRepository
        .findMinimumByCategoryAndDate(category, cacheService.getLastUpdatedDate(),
            pageable.getOffset(), pageable.getPageSize(), sort);
    long count = productSlaveRepository
        .countMinimumByCategoryAndDate(category, cacheService.getLastUpdatedDate());
    return MinimumPriceProductDto.convertPage(results, pageable, count);
  }

  @Cacheable(value = "productCache", key = "'distcount list'")
  public Map<String, Integer> countDiscountProductEachCategory() {
    List<Object[]> objectList = productSlaveRepository
        .countDiscountEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.BigIntegerToIntegerMap(objectList);
  }

  @Cacheable(value = "productCache", key = "'minimum price list'")
  public Map<String, Integer> countMinimumPriceProductEachCategory() {
    List<Object[]> objectList = productSlaveRepository
        .countMinimumEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.BigIntegerToIntegerMap(objectList);
  }

}
