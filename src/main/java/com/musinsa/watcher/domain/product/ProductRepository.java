package com.musinsa.watcher.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT distinct p.brand FROM Product p ")
  Page<String> findAllBrand(Pageable pageable);

  @Query(value = "select distinct brand from product WHERE (brand RLIKE ?1 OR ( brand >= ?2 AND brand < ?3 ))"
      , nativeQuery = true)
  List<String> findBrandByInitial(String initial1, String initial2, String initial3);

  Page<Product> findByBrand(String brand, Pageable pageable);

  Page<Product> findByCategory(String category, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.brand LIKE %?1% OR p.productName LIKE %?1%")
  Page<Product> searchItems(String text, Pageable pageable);

  @Query("SELECT distinct p FROM Product p JOIN FETCH p.prices p2 WHERE p.productId =  ?1 ORDER BY p2.createdDate DESC")
  Product findProductWithPrice(int productId);
}
