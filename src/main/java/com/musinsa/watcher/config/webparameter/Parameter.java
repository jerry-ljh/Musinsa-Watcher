package com.musinsa.watcher.config.webparameter;

public enum Parameter {
  BRAND("brand"),
  CATEGORY("category"),
  MIN_PRICE("minprice"),
  MAX_PRICE("maxprice"),
  ONLY_TODAY_UPDATED_DATA("onlyTodayUpdatedData"),
  NULL("");

  String parameter;

  Parameter(String parameter){
    this.parameter = parameter;
  }

  public String getParameter() {
    return parameter;
  }
}
