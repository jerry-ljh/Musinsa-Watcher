package com.musinsa.watcher.domain.product;

public enum InitialWord {
  type1("가", "나"),
  type2("나", "다"),
  type3("다", "라"),
  type4("라", "마"),
  type5("마", "바"),
  type6("바", "사"),
  type7("사", "아"),
  type8("아", "자"),
  type9("자", "차"),
  type10("차", "카"),
  type11("카", "타"),
  type12("타", "파"),
  type13("파", "하"),
  type14("하", "힣"),
  type15("0", "9");

  String start;
  String end;

  InitialWord(String start, String end) {
    this.start = start;
    this.end = end;
  }

  public String getStart() {
    return start;
  }

  public String getEnd() {
    return end;
  }

  public static String getType(String number) {
    return "type".concat(number);
  }
}
