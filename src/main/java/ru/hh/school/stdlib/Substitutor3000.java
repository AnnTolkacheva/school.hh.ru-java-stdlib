package ru.hh.school.stdlib;

import java.util.HashMap;

public class Substitutor3000 {
  HashMap<String, String> cache;

  public Substitutor3000 () {
    cache = new HashMap<String, String>();
	}

  public void put(String key, String value) {
    if (cache.containsKey(key)) {
      cache.remove(key);
    }
    cache.put(key, value);
  }

  private String parseString(String value) {
    if (value == null) {
      return null;
    }
    String rezult = "";
    String regArray[] = value.split("\\$\\{");
    int index = 0;
    if (value.charAt(0) != '$') {
      rezult += regArray[index];
      index++;
    }
    for (; index < regArray.length; index++) {
      String tempArray[] = regArray[index].split("\\}");
      if (tempArray.length > 0) {
        if (this.cache.containsKey(tempArray[0])) {
          rezult += this.cache.get(tempArray[0]);
        }
      }
      if (tempArray.length > 1) {
          rezult += tempArray[index];
      }
    }
    return rezult;
  }

  public String get(String key) {
    String value = this.cache.get(key);
    String rezult = this.parseString(value);
    return rezult;
  }
}
