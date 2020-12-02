package com.musinsa.watcher.domain.product;

import com.musinsa.watcher.web.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT distinct p.brand FROM Product p ")
  public Page<String> findAllBrand(Pageable pageable);

  public Page<Product> findByBrand(String brand, Pageable pageable);
}
