package com.musinsa.watcher;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MapperUtils {

  private MapperUtils() {
  }

  public static Map<String, Integer> BigIntegerToIntegerMap(List<Object[]> objectList) {
    Map<String, Integer> map = new TreeMap<>();
    objectList.stream()
        .forEach(object -> map.put((String) object[0], ((BigInteger) object[1]).intValue()));
    return map;
  }

  public static Map<String, Integer> longToIntegerMapper(List<Object[]> objectList) {
    Map<String, Integer> map = new TreeMap<>();
    objectList.stream()
        .forEach(object -> map.put((String) object[0], ((Long) object[1]).intValue()));
    return map;
  }

}
