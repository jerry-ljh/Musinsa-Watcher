package com.musinsa.watcher.domain.price.slave;

import com.musinsa.watcher.domain.price.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceSlaveRepository extends JpaRepository<Price, Long> {

}