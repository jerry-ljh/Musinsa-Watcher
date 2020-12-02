package com.musinsa.watcher.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT distinct p.brand FROM Product p ")
  Page<String> findAllBrand(Pageable pageable);

  Page<Product> findByBrand(String brand, Pageable pageable);

  Page<Product> findByCategory(String category, Pageable pageable);

}
