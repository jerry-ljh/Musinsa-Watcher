package com.musinsa.watcher.domain.product.discount.slave;

import com.musinsa.watcher.domain.product.discount.TodayMinimumPriceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayMinimumPriceRepository extends JpaRepository<TodayMinimumPriceProduct, Long> {

}
