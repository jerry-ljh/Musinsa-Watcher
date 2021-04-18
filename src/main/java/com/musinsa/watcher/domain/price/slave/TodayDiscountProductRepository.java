package com.musinsa.watcher.domain.price.slave;

import com.musinsa.watcher.domain.price.TodayDiscountProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayDiscountProductRepository extends JpaRepository<TodayDiscountProduct, Long> {

}
