package com.musinsa.watcher.domain.product;

public enum Category {
  TOP("001"),
  OUTER("002"),
  ONEPIECE("020"),
  PANTS("003"),
  SKIRTS("022"),
  BAG("004"),
  SNEAKERS("018"),
  SHOES("005"),
  HEADWEAR("007"),
  SOCK_LEGWEAR("008");

  private String category;

  Category(String category) {
    this.category = category;
  }

  public String getCategory() {
    return category;
  }

  public static Category getCategory(String category) {
    if (category.equals("001")) {
      return Category.TOP;
    } else if (category.equals("002")) {
      return Category.OUTER;
    } else if (category.equals("003")) {
      return Category.PANTS;
    } else if (category.equals("004")) {
      return Category.BAG;
    } else if (category.equals("005")) {
      return Category.SHOES;
    } else if (category.equals("007")) {
      return Category.HEADWEAR;
    } else if (category.equals("008")) {
      return Category.SOCK_LEGWEAR;
    } else if (category.equals("018")) {
      return Category.SNEAKERS;
    } else if (category.equals("020")) {
      return Category.ONEPIECE;
    } else if (category.equals("022")) {
      return Category.SKIRTS;
    }
    throw new IllegalArgumentException("not allow category");
  }
}