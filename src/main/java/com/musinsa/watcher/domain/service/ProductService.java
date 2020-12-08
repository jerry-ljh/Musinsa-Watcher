package com.musinsa.watcher.domain.service;

import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.ProductRepository;
import com.musinsa.watcher.web.dto.DiscountedProductDto;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.musinsa.watcher.web.dto.ProductWithPriceResponseDto;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductRepository productRepository;

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

  public Page<ProductResponseDto> findByCategory(String category, Pageable pageable) {
    productRepository.findByCategory("001", pageable);
    Page<Product> page = productRepository.findByCategory(category, pageable);
    return new PageImpl<ProductResponseDto>(page.getContent()
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }

  public ProductWithPriceResponseDto findProductWithPrice(int productId) {
    return new ProductWithPriceResponseDto(productRepository.findProductWithPrice(productId));
  }

  public List<String> findBrandByInitial(String initial1, String initial2, String initial3) {
    return productRepository.findBrandByInitial(initial1, initial2, initial3);
  }

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    Page<Product> page = productRepository.searchItems(text, pageable);
    return new PageImpl<ProductResponseDto>(page.getContent()
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }

  public Page<DiscountedProductDto> findDiscountedProduct(String category, Pageable pageable) {
    LocalDateTime lastUpdateDate = productRepository.findLastUpdateDate();
    Page<Object[]> page = productRepository
        .findDiscountedProduct(category, lastUpdateDate, pageable);
    return new PageImpl<DiscountedProductDto>(DiscountedProductDto.objectsToDtoList(page.getContent()),
        pageable, page.getTotalElements());
  }
}
