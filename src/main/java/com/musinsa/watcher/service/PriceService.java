package com.musinsa.watcher.service;

import com.musinsa.watcher.domain.price.Price;
import com.musinsa.watcher.domain.price.PriceRepository;
import com.musinsa.watcher.web.dto.PriceResponseDto;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PriceService {

  private final PriceRepository priceRepository;

  public Page<PriceResponseDto> findByProductId(int productId, Pageable pageable) {
    Page<Price> page = priceRepository.findByProductId(productId, pageable);
    return new PageImpl<PriceResponseDto>(page.getContent()
        .stream()
        .map(PriceResponseDto::new)
        .collect(Collectors.toList()), pageable, page.getTotalElements());
  }
}
