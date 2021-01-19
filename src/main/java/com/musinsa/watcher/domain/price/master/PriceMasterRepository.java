package com.musinsa.watcher.domain.price.master;

import com.musinsa.watcher.domain.price.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceMasterRepository extends JpaRepository<Price, Long> {

}
