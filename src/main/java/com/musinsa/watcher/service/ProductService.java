package com.musinsa.watcher.service;

import com.musinsa.watcher.MapperUtils;
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
import org.springframework.data.domain.PageImpl;
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

  public Page<ProductResponseDto> findByBrand(String name, Pageable pageable) {
    return productQuerySlaveRepository.findByBrand(name, pageable);
  }

  public Map<String, Integer> searchBrand(String brand){
    List<Object[]> objectList = productQuerySlaveRepository.searchBrand(brand);
    return objectList.size() > 0 ? MapperUtils.longToIntegerMapper(objectList) : null;
  }

  @Cacheable(value = "productCache", key = "'category'+#category+#pageable.pageNumber+#pageable.pageSize", condition = "#pageable.pageNumber==0")
  public Page<ProductResponseDto> findByCategory(String category, Pageable pageable) {
    return productQuerySlaveRepository
        .findByCategory(category, cacheService.getLastUpdatedDate(), pageable);
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(
        productQuerySlaveRepository.findProductWithPrice(productId));
  }

  @Cacheable(value = "productCache", key = "'brand-initial'+#initial1+#initial2")
  public Map<String, Integer> findBrandByInitial(String initial1, String initial2) {
    List<Object[]> objectList = productQuerySlaveRepository.findBrandByInitial(initial1, initial2);
    return MapperUtils.longToIntegerMapper(objectList);
  }

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    return productQuerySlaveRepository.searchItems(text, pageable);
  }

  @Cacheable(value = "productCache", key = "'distcount'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<DiscountedProductDto> findDiscountedProduct(String category, Pageable pageable, String sort) {
    List<Object[]> result = productQuerySlaveRepository
        .findDiscountedProduct(category, cacheService.getLastUpdatedDate(),
            pageable.getOffset(), pageable.getPageSize(), sort);
    return new PageImpl<DiscountedProductDto>(
        DiscountedProductDto.objectsToDtoList(result),
        pageable,
        productSlaveRepository.countDiscountedProduct(category, cacheService.getLastUpdatedDate()));
  }

  @Cacheable(value = "productCache", key = "'minimum'+#category+#pageable.pageNumber+#pageable.pageSize+#sort")
  public Page<MinimumPriceProductDto> findMinimumPriceProduct(String category, Pageable pageable, String sort) {
    List<Object[]> results = productQuerySlaveRepository
        .findProductByMinimumPrice(category, cacheService.getLastUpdatedDate(),
            pageable.getOffset(), pageable.getPageSize(), sort);
    return new PageImpl<MinimumPriceProductDto>(
        MinimumPriceProductDto.objectsToDtoList(results),
        pageable,
        productSlaveRepository.countMinimumPrice(category, cacheService.getLastUpdatedDate()));
  }

  @Cacheable(value = "productCache", key = "'distcount list'")
  public Map<String, Integer> countDiscountProductEachCategory() {
    List<Object[]> objectList = productSlaveRepository
        .countDiscountProductEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.BigIntegerToIntegerMap(objectList);
  }

  @Cacheable(value = "productCache", key = "'minimum price list'")
  public Map<String, Integer> countMinimumPriceProductEachCategory() {
    List<Object[]> objectList = productSlaveRepository
        .countMinimumPriceProductEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.BigIntegerToIntegerMap(objectList);
  }

}
