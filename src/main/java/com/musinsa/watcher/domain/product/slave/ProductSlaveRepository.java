package com.musinsa.watcher.domain.product.slave;

import com.musinsa.watcher.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSlaveRepository extends JpaRepository<Product, Long> {
}