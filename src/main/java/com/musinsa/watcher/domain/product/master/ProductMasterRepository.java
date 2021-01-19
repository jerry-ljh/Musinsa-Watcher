package com.musinsa.watcher.domain.product.master;

import com.musinsa.watcher.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMasterRepository extends JpaRepository<Product, Long> {

}
