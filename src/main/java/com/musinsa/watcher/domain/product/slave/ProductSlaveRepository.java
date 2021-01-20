package com.musinsa.watcher.domain.product.slave;

import com.musinsa.watcher.domain.product.Product;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductSlaveRepository extends JpaRepository<Product, Long> {

  @Query("SELECT distinct p.brand FROM Product p ")
  Page<String> findAllBrand(Pageable pageable);

  @Cacheable(value = "productCache", key = "'distcount count'+#category")
  @Query(value = "select count(*) from (SELECT\n"
      + "      (CASE WHEN p2.created_date <=  ?2 THEN p2.real_price END - min(p2.real_price))/max(p2.real_price)*100 as percent \n"
      + "      FROM product p1 \n"
      + "      inner join price p2 \n"
      + "      on p1.product_id = p2.product_id\n"
      + "      where p1.category = ?1 and p1.modified_date > ?2 and p2.created_date  >=  ?2 - INTERVAL 1 DAY \n"
      + "      group by p1.product_id having count(p2.created_date) > 1 and percent > 1) t",
      nativeQuery = true)
  Long countDiscountedProduct(String category, LocalDate date);

  @Query(value =
      "select category, count(category) as count from (SELECT category,\n"
          + "(CASE WHEN p2.created_date <=  ?1 THEN p2.real_price END - min(p2.real_price))/max(p2.real_price)*100 as percent \n"
          + "FROM product p1 \n"
          + "inner join price p2 \n"
          + "on p1.product_id = p2.product_id\n"
          + "where p2.created_date  >=   ?1 - INTERVAL 1 DAY \n"
          + "group by p1.product_id having count(p2.created_date) > 1 and percent > 1) t group by category order by null",
      nativeQuery = true)
  List<Object[]> countDiscountProductEachCategory(LocalDate date);

  @Cacheable(value = "productCache", key = "'minimum count'+#category")
  @Query(value = "select count(p1.product_id) from \n"
      + "(SELECT p1.product_id, min(p2.real_price) as min_price, avg(p2.real_price) as avg_price\n"
      + "FROM product p1 \n"
      + "inner join price p2 on p1.product_id = p2.product_id\n"
      + "where p1.category = ?1  and p1.modified_date > ?2\n"
      + "group by p1.product_id having min_price != avg_price and count(p2.created_date) > 5 order by null ) p1\n"
      + "inner join \n"
      + "(SELECT p1.product_id, p1.real_price as today_price\n"
      + "FROM price p1 \n"
      + "where p1.created_date > ?2) p2\n"
      + "on p1.product_id = p2.product_id\n"
      + "where min_price = today_price and avg_price - min_price > avg_price/20;",
      nativeQuery = true)
  long countMinimumPrice(String category, LocalDate date);

  @Query(value =
      "select category, count(category) from \n"
          + "(SELECT p1.product_id, p1.category,\n"
          + " min(p2.real_price) as min_price, avg(p2.real_price) as avg_price\n"
          + "FROM product p1 \n"
          + "inner join price p2 on p1.product_id = p2.product_id\n"
          + "group by p1.product_id having min_price != avg_price and count(p2.created_date) > 5) p1\n"
          + "inner join \n"
          + "(SELECT p1.product_id, p1.real_price as today_price\n"
          + "FROM price p1 \n"
          + "where p1.created_date > ?1) p2\n"
          + "on p1.product_id = p2.product_"
          + "id\n"
          + "where min_price = today_price and avg_price - min_price > avg_price/20 \n"
          + "group by category",
      nativeQuery = true)
  List<Object[]> countMinimumPriceProductEachCategory(LocalDate date);
}
