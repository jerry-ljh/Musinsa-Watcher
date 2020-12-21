package com.musinsa.watcher.domain.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT distinct p.brand FROM Product p ")
  Page<String> findAllBrand(Pageable pageable);

  @Query(value = "SELECT brand, count(brand) as count  FROM product WHERE (brand RLIKE ?1 OR ( brand >= ?2 AND brand < ?3 )) GROUP BY brand ORDER BY brand", nativeQuery = true)
  List<Object[]> findBrandByInitial(String initial1, String initial2, String initial3);

  Page<Product> findByBrand(String brand, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.category = ?1 ORDER BY function('date_format', p.modifiedDate, '%Y, %m, %d') DESC, p.rank")
  Page<Product> findByCategory(String category, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.brand LIKE %?1% OR p.productName LIKE %?1%")
  Page<Product> searchItems(String text, Pageable pageable);

  @Query("SELECT distinct p FROM Product p JOIN FETCH p.prices p2 WHERE p.productId =  ?1 ORDER BY p2.createdDate DESC")
  Product findProductWithPrice(int productId);

  @Query("SELECT max(p.modifiedDate) FROM Product p")
  LocalDateTime findLastUpdateDate();

  @Query(value =
      "SELECT p1.product_id, p1.product_name, p1.brand, min(p2.price + p2.coupon), p1.img, p1.modified_date, \n"
          + "(CASE WHEN  p2.created_date <  ?2 THEN  p2.price + p2.coupon END - min(p2.price + p2.coupon)) as discount, \n"
          + "((CASE WHEN  p2.created_date <  ?2  THEN p2.price + p2.coupon END) - min(p2.price + p2.coupon))/max(p2.price + p2.coupon + p2.coupon)*100 as percent\n"
          + "FROM product p1 inner join price p2 on p1.product_id = p2.product_id\n"
          + "where p1.category = ?1 and p2.created_date >=  ?2 - INTERVAL 1 DAY \n"
          + "group by p1.product_id having count(p2.created_date) > 1 and percent > 1\n"
          + "order by percent desc, product_name",
      countQuery = "select count(*) from (SELECT\n"
          + "      (CASE WHEN p2.created_date <=  ?2 THEN p2.price + p2.coupon END - min(p2.price + p2.coupon))/max(p2.price + p2.coupon)*100 as percent \n"
          + "      FROM product p1 \n"
          + "      inner join price p2 \n"
          + "      on p1.product_id = p2.product_id\n"
          + "      where p1.category = ?1 and p2.created_date  >=  ?2 - INTERVAL 1 DAY \n"
          + "      group by p1.product_id having count(p2.created_date) > 1 and percent > 1) t",
      nativeQuery = true)
  Page<Object[]> findDiscountedProduct(String category, LocalDate date, Pageable pageable);

  @Query(value =
      "select category, count(category) as count from (SELECT category,\n"
          + "(CASE WHEN p2.created_date <=  ?1 THEN p2.price + p2.coupon END - min(p2.price + p2.coupon))/max(p2.price + p2.coupon)*100 as percent \n"
          + "FROM product p1 \n"
          + "inner join price p2 \n"
          + "on p1.product_id = p2.product_id\n"
          + "where p2.created_date  >=   ?1 - INTERVAL 1 DAY \n"
          + "group by p1.product_id having count(p2.created_date) > 1 and percent > 1) t group by category",
      nativeQuery = true)
  List<Object[]> countDiscountProductEachCategory(LocalDate date);

  @Query(value =
      "select p1.product_id, p1.product_name, p1.brand, p1.img, p1.modified_date, today_price, max_price from \n"
          + "(SELECT p1.product_id, p1.product_name, p1.brand, p1.img, p1.modified_date,\n"
          + " min(p2.price + p2.coupon) as min_price, max(p2.price + p2.coupon) as max_price\n"
          + "FROM product p1 \n"
          + "inner join price p2 on p1.product_id = p2.product_id\n"
          + "where p1.category = ?1\n"
          + "group by p1.product_id having min_price != max_price and count(p2.created_date) > 5) p1\n"
          + "inner join \n"
          + "(SELECT p1.product_id, p1.price as today_price\n"
          + "FROM price p1 \n"
          + "where p1.created_date > ?2) p2\n"
          + "on p1.product_id = p2.product_id\n"
          + "where min_price = today_price\n"
          + "order by (max_price - min_price)/max_price DESC",
      countQuery ="select count(*) from \n"
          + "(SELECT p1.product_id, p1.product_name, p1.brand, p1.img, p1.modified_date,\n"
          + " min(p2.price + p2.coupon) as min_price, max(p2.price + p2.coupon) as max_price\n"
          + "FROM product p1 \n"
          + "inner join price p2 on p1.product_id = p2.product_id\n"
          + "where p1.category = ?1\n"
          + "group by p1.product_id having min_price != max_price and count(p2.created_date) > 5) p1\n"
          + "inner join \n"
          + "(SELECT p1.product_id, p1.price as today_price\n"
          + "FROM price p1 \n"
          + "where p1.created_date > ?2) p2\n"
          + "on p1.product_id = p2.product_id\n"
          + "where min_price = today_price",
      nativeQuery = true)
  Page<Object[]> findProductByMinimumPrice(String category, LocalDate date, Pageable pageable);

  @Query(value =
      "select category, count(category) from \n"
          + "(SELECT p1.product_id, p1.category,\n"
          + " min(p2.price + p2.coupon) as min_price, max(p2.price + p2.coupon) as max_price\n"
          + "FROM product p1 \n"
          + "inner join price p2 on p1.product_id = p2.product_id\n"
          + "group by p1.product_id having min_price != max_price and count(p2.created_date) > 5) p1\n"
          + "inner join \n"
          + "(SELECT p1.product_id, p1.price as today_price\n"
          + "FROM price p1 \n"
          + "where p1.created_date > ?1) p2\n"
          + "on p1.product_id = p2.product_id\n"
          + "where min_price = today_price\n"
          + "group by category",
      nativeQuery = true)
  List<Object[]> countMinimumPriceProductEachCategory(LocalDate date);
}
