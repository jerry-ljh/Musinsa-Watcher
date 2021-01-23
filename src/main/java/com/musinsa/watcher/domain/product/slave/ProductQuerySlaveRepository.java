package com.musinsa.watcher.domain.product.slave;

import com.musinsa.watcher.domain.price.QPrice;
import com.musinsa.watcher.domain.product.Product;
import com.musinsa.watcher.domain.product.QProduct;
import com.musinsa.watcher.web.dto.ProductResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductQuerySlaveRepository {

  @Qualifier("slaveJPAQueryFactory")
  private final JPAQueryFactory queryFactory;
  private final ApplicationContext applicationContext;
  @PersistenceContext(unitName = "slaveEntityManager")
  private EntityManager em;

  private ProductQuerySlaveRepository getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public Page<ProductResponseDto> searchItems(String text, Pageable pageable) {
    List<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.brand.contains(text)
                .or(QProduct.product.productName.contains(text)))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable, this.getSpringProxy().countSearchItems(text));
  }

  public List<Object[]> searchBrand(String text) {
    BooleanBuilder builder = new BooleanBuilder();
    return queryFactory.from(QProduct.product)
        .where(builder.and(Expressions.booleanTemplate("brand like '" + text + "%'")))
        .select(QProduct.product.brand, QProduct.product.brand.count())
        .groupBy(QProduct.product.brand)
        .fetch().stream().map(i -> i.toArray()).collect(Collectors.toList());
  }

  @Cacheable(value = "productCache", key = "'search count'+#text")
  public long countSearchItems(String text) {
    return queryFactory.selectFrom(QProduct.product)
        .where(QProduct.product.brand.contains(text)
            .or(QProduct.product.productName.contains(text)))
        .fetchCount();
  }

  public Product findProductWithPrice(int productId) {
    return queryFactory.selectFrom(QProduct.product)
        .leftJoin(QProduct.product.prices, QPrice.price1)
        .fetchJoin()
        .where(QProduct.product.productId.eq(productId))
        .orderBy(QPrice.price1.createdDate.desc())
        .fetchOne();
  }

  public Page<ProductResponseDto> findByCategory(String category, LocalDate updateDate,
      Pageable pageable) {
    List<Product> results =
        queryFactory.selectFrom(QProduct.product)
            .where(QProduct.product.category.eq(category),
                QProduct.product.modifiedDate.after(updateDate.atStartOfDay()),
                QProduct.product.modifiedDate.before(updateDate.plusDays(1).atStartOfDay()))
            .orderBy(QProduct.product.rank.asc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    return new PageImpl<>(results
        .stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countFindByCategory(category, updateDate));
  }

  @Cacheable(value = "productCache", key = "'category count'+#category")
  public long countFindByCategory(String category, LocalDate updateDate) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.category.eq(category),
            QProduct.product.modifiedDate.after(updateDate.atStartOfDay()),
            QProduct.product.modifiedDate.before(updateDate.plusDays(1).atStartOfDay()))
        .fetchCount();
  }

  public Page<ProductResponseDto> findByBrand(String brand, Pageable pageable) {
    List<Product> results = queryFactory.selectFrom(QProduct.product)
        .where(QProduct.product.brand.eq(brand))
        .orderBy(QProduct.product.modifiedDate.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();
    return new PageImpl<>(results.stream()
        .map(ProductResponseDto::new)
        .collect(Collectors.toList()), pageable,
        this.getSpringProxy().countFindByBrand(brand));
  }

  @Cacheable(value = "productCache", key = "'brand count'+#brand")
  public long countFindByBrand(String brand) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.eq(brand))
        .fetchCount();
  }

  public LocalDateTime findLastUpdateDate() {
    return queryFactory.selectFrom(QProduct.product)
        .orderBy(QProduct.product.modifiedDate.desc())
        .fetchFirst().getModifiedDate();
  }

  public List<Object[]> findBrandByInitial(String initial1, String initial2) {
    return queryFactory.from(QProduct.product)
        .where(QProduct.product.brand.goe(initial1).and(QProduct.product.brand.lt(initial2)))
        .groupBy(QProduct.product.brand)
        .orderBy(QProduct.product.brand.asc())
        .select(QProduct.product.brand, QProduct.product.brand.count())
        .fetch().stream().map(i -> i.toArray()).collect(Collectors.toList());
  }

  public List<Object[]> findDiscountedProduct(String category, LocalDate date, long offset,
      int limit, String sort) {
    Query query = em.createNativeQuery(
        "SELECT p1.product_id, p1.product_name, p1.brand, min(p2.real_price) as price, p1.img, p1.modified_date, \n"
            + "(CASE WHEN  p2.created_date <  '" + date
            + "' THEN  p2.real_price END - min(p2.real_price)) as discount, \n"
            + "((CASE WHEN  p2.created_date <  '" + date
            + "'  THEN p2.real_price END) - min(p2.real_price))/max(p2.real_price)*100 as percent\n"
            + "FROM product p1 inner join price p2 on p1.product_id = p2.product_id\n"
            + "where p1.category = '" + category + "' and p1.modified_date > '" + date
            + "' and p2.created_date >= '" + date + "' - INTERVAL 1 DAY \n"
            + "group by p1.product_id having count(p2.created_date) > 1 and percent > 1\n"
            + "order by " + sort + ", product_name limit " + offset + ", " + limit);
    List<Object[]> resultList = query.getResultList();
    return resultList;
  }

  public List<Object[]> findProductByMinimumPrice(String category, LocalDate date, long offset,
      int limit, String sort) {
    Query query = em.createNativeQuery(
        "select p1.product_id, p1.product_name, p1.brand, p1.img, p1.modified_date, today_price as price, avg_price,"
            + "(avg_price - min_price)/avg_price as percent  from \n"
            + "(SELECT p1.product_id,  p1.product_name, p1.brand, p1.img, p1.modified_date,\n"
            + "min(p2.real_price) as min_price, avg(p2.real_price) as avg_price\n"
            + "FROM product p1 \n"
            + "inner join price p2 on p1.product_id = p2.product_id\n"
            + "where p1.category = '" + category + "'  and p1.modified_date > '" + date + "'\n"
            + "group by p1.product_id having min_price != avg_price and count(p2.created_date) > 5 order by null ) p1\n"
            + "inner join \n"
            + "(SELECT p1.product_id, p1.real_price as today_price\n"
            + "FROM price p1 \n"
            + "where p1.created_date > '" + date + "') p2\n"
            + "on p1.product_id = p2.product_id\n"
            + "where min_price = today_price and avg_price - min_price > avg_price/20\n"
            + "order by " + sort + " limit " + offset + ", " + limit);
    List<Object[]> resultList = query.getResultList();
    return resultList;
  }

}
