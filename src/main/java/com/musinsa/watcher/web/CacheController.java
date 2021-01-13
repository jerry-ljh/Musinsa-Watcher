package com.musinsa.watcher.web;

import com.musinsa.watcher.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({"http://www.musinsa.cf/", "http://api.musinsa.cf/, https://www.musinsa.cf/",
    "https://api.musinsa.cf/"})
@RequiredArgsConstructor
@RestController
public class CacheController {

  private final CacheService cacheService;

  @GetMapping("/api/v1/product/last-modified")
  public String updateCacheByDate() {
    return cacheService.updateCacheByDate().toString();
  }

  @GetMapping("/api/v1/product/cache/last-modified")
  public String updateDate() {
    return cacheService.getLastUpdatedDate().toString();
  }

  @GetMapping("/api/v1/cache/sync")
  public String sycnCache(){
    return cacheService.doSynchronize() ? "동기화되었습니다." : "이미 동기화되었습니다.";
  }
}
