package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.slave.PriceSlaveRepository;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PriceService {

  private final PriceSlaveRepository priceSlaveRepository;

  public Page<PriceResponseDto> findByProductId(int productId, Pageable pageable) {
    Page<Price> page = priceSlaveRepository.findByProductId(productId, pageable);
    return PriceResponseDto.convertPage(page, pageable, page.getTotalElements());
  }
}
