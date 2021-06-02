package com.musinsa.watcher.config.cache;

public enum CacheName {
  PRODUCT_CACHE("productCache"),
  LAST_UPDATE_DATE_KEY("findCachedLastUpdatedDate");

  String name;

  CacheName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
