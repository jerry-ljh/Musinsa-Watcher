package com.musinsa.watcher.domain.product;

public enum InitialWord {
  type1(new String[]{"^(ㄱ|ㄲ)", "가", "나"}),
  type2(new String[]{"^ㄴ", "나", "다"}),
  type3(new String[]{"^(ㄷ|ㄸ)", "다", "라"}),
  type4(new String[]{"^ㄹ", "라", "마"}),
  type5(new String[]{"^ㅁ", "마", "바"}),
  type6(new String[]{"^ㅂ", "바", "사"}),
  type7(new String[]{"^(ㅅ|ㅆ)", "사", "아"}),
  type8(new String[]{"^ㅇ", "아", "자"}),
  type9(new String[]{"^(ㅈ|ㅉ)", "자", "차"}),
  type10(new String[]{"^ㅊ", "차", "카"}),
  type11(new String[]{"^ㅋ", "카", "타"}),
  type12(new String[]{"^ㅌ", "타", "파"}),
  type13(new String[]{"^ㅍ", "파", "하"}),
  type14(new String[]{"^ㅎ", "하", "힣"}),
  type15(new String[]{"^[0-9]+$", "0", "9"});

  private String[] initials;

  InitialWord(String[] initials) {
    this.initials = initials;
  }

  public static String getType(String number){
    return "type".concat(number);
  }

  public String[] getInitials() {
    return initials;
  }

  ;

}
