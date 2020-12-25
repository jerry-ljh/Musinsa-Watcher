package com.musinsa.watcher.domain.product;

public enum InitialWord {
  type1(new Initial("^(ㄱ|ㄲ)", "가", "나")),
  type2(new Initial("^ㄴ", "나", "다")),
  type3(new Initial("^(ㄷ|ㄸ)", "다", "라")),
  type4(new Initial("^ㄹ", "라", "마")),
  type5(new Initial("^ㅁ", "마", "바")),
  type6(new Initial("^ㅂ", "바", "사")),
  type7(new Initial("^(ㅅ|ㅆ)", "사", "아")),
  type8(new Initial("^ㅇ", "아", "자")),
  type9(new Initial("^(ㅈ|ㅉ)", "자", "차")),
  type10(new Initial("^ㅊ", "차", "카")),
  type11(new Initial("^ㅋ", "카", "타")),
  type12(new Initial("^ㅌ", "타", "파")),
  type13(new Initial("^ㅍ", "파", "하")),
  type14(new Initial("^ㅎ", "하", "힣")),
  type15(new Initial("^[0-9]+$", "0", "9"));

  private Initial initials;

  InitialWord(Initial initials) {
    this.initials = initials;
  }

  public static String getType(String number){
    return "type".concat(number);
  }

  public Initial getInitials() {
    return initials;
  }
}
