package ru.hh.school.stdlib;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Substitutor3000 {
  HashMap<String, String> cache;
  final Pattern substitution;

  public Substitutor3000 () {
    cache = new HashMap<String, String>();
    substitution = Pattern.compile("\\$\\{([^\\$]{0,})\\}");
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
    Matcher m = substitution.matcher(value);
    while( m.find()) {
      String key = m.group();
      key = key.split("\\{|\\}|\\$")[2];
      String replacement;
      if (this.cache.containsKey(key)) {
        replacement = this.cache.get(key);
      }
      else {
        replacement = "";
      }
      rezult = m.replaceFirst(replacement);
      m = substitution.matcher(rezult);
    }
    return rezult;
  }

  public String get(String key) {
    String value = this.cache.get(key);
    String rezult = this.parseString(value);
    return rezult;
  }
}
