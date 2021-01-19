package com.musinsa.watcher;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

public class MapperUtilsTest {

  @Test
  @DisplayName("DB에서 읽어온 object를 mapping한다.")
  public void mapping(){
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"001", new BigInteger("100")});
    list.add(new Object[]{"002", new BigInteger("200")});
    list.add(new Object[]{"003", new BigInteger("300")});
    list.add(new Object[]{"004", new BigInteger("400")});
    list.add(new Object[]{"005", new BigInteger("500")});
    Map<String, Integer> map = MapperUtils.BigIntegerToIntegerMap(list);
    for(int i=0; i<list.size(); i++){
      BigInteger bigInteger = (BigInteger)list.get(i)[1];
      assertEquals(bigInteger.intValue(), (int)map.get(list.get(i)[0]));
    }
  }
}