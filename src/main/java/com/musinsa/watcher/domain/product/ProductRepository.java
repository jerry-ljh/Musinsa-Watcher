package com.musinsa.watcher.domain.product;

import java.time.LocalDateTime;
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

  @Query("SELECT max(p.modifiedDate) FROM Product p")
  LocalDateTime findLastUpdateDate();

  @Query(value = "SELECT p1.product_id, p1.product_name, p1.brand, min(p2.price + p2.coupon), p1.img, p1.modified_date, \n"
      + "(CASE WHEN  p2.created_date <  date_format(?2, '%Y-%m-%d') THEN  p2.price + p2.coupon END - min(p2.price + p2.coupon)) as discount, \n"
      + "((CASE WHEN  p2.created_date <  date_format(?2, '%Y-%m-%d')  THEN p2.price + p2.coupon END) - min(p2.price + p2.coupon))/max(p2.price + p2.coupon)*100 as percent\n"
      + "FROM product p1 inner join price p2 on p1.product_id = p2.product_id\n"
      + "where p1.category = ?1 and p2.created_date >=  date_format(?2, '%Y-%m-%d') - INTERVAL 1 DAY \n"
      + "group by p1.product_id having count(p2.created_date) > 1 and percent > 1\n"
      + "order by percent desc, product_name",
      countQuery = "select count(*) from (SELECT\n"
          + "      (CASE WHEN p2.created_date <=  date_format(?2, '%Y-%m-%d') THEN p2.price + p2.coupon END - min(p2.price + p2.coupon))/max(p2.price)*100 as percent \n"
          + "      FROM product p1 \n"
          + "      inner join price p2 \n"
          + "      on p1.product_id = p2.product_id\n"
          + "      where p1.category = ?1 and p2.created_date  >=  date_format(?2, '%Y-%m-%d') - INTERVAL 1 DAY \n"
          + "      group by p1.product_id having count(p2.created_date) > 1 and percent > 1) t",
      nativeQuery = true)
  Page<Object[]> findDiscountedProduct(String category, LocalDateTime date, Pageable pageable);

}
