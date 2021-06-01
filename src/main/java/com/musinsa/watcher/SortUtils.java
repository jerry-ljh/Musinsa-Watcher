package com.musinsa.watcher;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Sort;

public class SortUtils {

  private SortUtils() {
  }

  public static Order getAscending(Sort.Order order) {
    return order.isAscending() ? Order.ASC : Order.DESC;
  }

  public static OrderSpecifier getOrderSpecifier(Sort.Order order, Class<?> type) {
    return new OrderSpecifier(getAscending(order), Expressions.path(type, order.getProperty()));
  }

  public static OrderSpecifier getOrderSpecifier(Sort.Order order, Expression expression) {
    return new OrderSpecifier(getAscending(order), expression);
  }

  public static OrderSpecifier getOrderSpecifier(Order order, Expression expression) {
    return new OrderSpecifier(order, expression);
  }
}
