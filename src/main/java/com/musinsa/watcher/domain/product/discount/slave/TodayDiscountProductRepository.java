package com.musinsa.watcher.domain.product.discount.slave;

import com.musinsa.watcher.domain.product.discount.TodayDiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayDiscountProductRepository extends JpaRepository<TodayDiscountProduct, Long> {

}
