package com.musinsa.watcher.domain.price.slave;

import com.musinsa.watcher.domain.price.TodayMinimumPriceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayMinimumPriceRepository extends JpaRepository<TodayMinimumPriceProduct, Long> {

}
