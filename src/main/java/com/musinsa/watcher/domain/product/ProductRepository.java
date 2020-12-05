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

  @Query("SELECT distinct p FROM Product p JOIN FETCH p.prices p2 WHERE p.productId =  ?1 ORDER BY p2.createdDate DESC")
  Product findProductWithPrice(int productId);
}
