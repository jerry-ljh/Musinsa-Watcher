package com.musinsa.watcher.domain.product;

public enum InitialWord {
  type1(new Initial( "가", "나")),
  type2(new Initial( "나", "다")),
  type3(new Initial("다", "라")),
  type4(new Initial("라", "마")),
  type5(new Initial("마", "바")),
  type6(new Initial("바", "사")),
  type7(new Initial("사", "아")),
  type8(new Initial("아", "자")),
  type9(new Initial("자", "차")),
  type10(new Initial("차", "카")),
  type11(new Initial("카", "타")),
  type12(new Initial("타", "파")),
  type13(new Initial( "파", "하")),
  type14(new Initial("하", "힣")),
  type15(new Initial("0", "9"));

  private Initial initials;

  InitialWord(Initial initials) {
    this.initials = initials;
  }

  public static String getType(String number) {
    return "type".concat(number);
  }

  public Initial getInitials() {
    return initials;
  }
}
