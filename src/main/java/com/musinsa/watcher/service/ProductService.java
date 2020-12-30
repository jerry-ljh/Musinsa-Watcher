package com.musinsa.watcher.service;

import com.musinsa.watcher.MapperUtils;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductQueryRepository;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.MinimumPriceProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.util.Map;
import java.util.stream.Collectors;
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

  private final ProductRepository productRepository;
  private final ProductQueryRepository productQueryRepository;
  private final CacheService cacheService;

  public Page<String> findAllBrand(Pageable pageable) {
    return productRepository.findAllBrand(pageable);
  }

  public Page<ProductResponseDto> findByBrand(String name, Pageable pageable) {
    Page<Product> page = productRepository.findByBrand(name, pageable);
    return new PageImpl<ProductResponseDto>(page.getContent()
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }

  @Cacheable(value = "productCache", key = "'category'+#category+#pageable.pageNumber", condition = "#pageable.pageNumber==0")
  public Page<ProductResponseDto> findByCategory(String category, Pageable pageable) {
    return productQueryRepository
        .findByCategory(category, cacheService.getLastUpdatedDate(), pageable);
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(productQueryRepository.findProductWithPrice(productId));
  }

  @Cacheable(value = "productCache", key = "'brand-initial'+#initial1+#initial2+#initial3")
  public Map<String, Integer> findBrandByInitial(String initial1, String initial2,
      String initial3) {
    List<Object[]> objectList = productRepository.findBrandByInitial(initial1, initial2, initial3);
    return MapperUtils.objectToStringAndIntegerMap(objectList);
  }

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    return productQueryRepository.searchItems(text, pageable);
  }

  @Cacheable(value = "productCache", key = "'distcount'+#category+#pageable.pageNumber")
  public Page<DiscountedProductDto> findDiscountedProduct(String category, Pageable pageable) {
    Page<Object[]> page = productRepository
        .findDiscountedProduct(category, cacheService.getLastUpdatedDate(), pageable);
    return new PageImpl<DiscountedProductDto>(
        DiscountedProductDto.objectsToDtoList(page.getContent()),
        pageable, page.getTotalElements());
  }

  @Cacheable(value = "productCache", key = "'minimum'+#category+#pageable.pageNumber")
  public Page<MinimumPriceProductDto> findMinimumPriceProduct(String category, Pageable pageable) {
    Page<Object[]> page = productRepository
        .findProductByMinimumPrice(category, cacheService.getLastUpdatedDate(), pageable);
    return new PageImpl<MinimumPriceProductDto>(
        MinimumPriceProductDto.objectsToDtoList(page.getContent()),
        pageable, page.getTotalElements());
  }

  @Cacheable(value = "productCache", key = "'distcount list'")
  public Map<String, Integer> countDiscountProductEachCategory() {
    List<Object[]> objectList = productRepository
        .countDiscountProductEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.objectToStringAndIntegerMap(objectList);
  }

  @Cacheable(value = "productCache", key = "'minimum price list'")
  public Map<String, Integer> countMinimumPriceProductEachCategory() {
    List<Object[]> objectList = productRepository
        .countMinimumPriceProductEachCategory(cacheService.getLastUpdatedDate());
    return MapperUtils.objectToStringAndIntegerMap(objectList);
  }

}
