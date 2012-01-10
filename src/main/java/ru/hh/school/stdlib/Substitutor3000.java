package ru.hh.school.stdlib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Substitutor3000 {
  HashMap<String, String> cache;
  HashMap<String, List<String>> superKeys;
  
  public Substitutor3000 () {
    cache = new HashMap<String, String>();
    superKeys = new HashMap<String, List<String>>();
	}
  
  public void setSuperKeys(String superKey, List<String> keys) {
    if (superKeys.get(superKey) != null) {
      superKeys.remove(superKey);
    }
    superKeys.put(superKey, keys);
  }
  
  public void put(String key, String value) {
    if (cache.get(key) != null) {
      cache.remove(key);
    }
    cache.put(key, value);
  }

  private List<String> getKeys(String superKey) {
    List<String> keys = superKeys.get(superKey);
    if (keys == null) {
      keys = new LinkedList<String>();
      keys.add(superKey);
    }
    else {
      List<String> subKeys;
      // проверяем нет ли еще вложенных зависимостей
      for(int index = 0; index < keys.size(); index++) {
        subKeys = superKeys.get(keys.get(index));
        if ( subKeys != null) {
          keys.remove(index);
          for(int i = 0; i < subKeys.size(); i++) {
            keys.add(subKeys.get(i));
          }
          index--;          
        }
      }
    }
    return keys;
  }
  
  public String get(String key) {
    List<String> keys = this.getKeys(key);
    String rezult = null;
    for (int index = 0; index < keys.size(); index++) {
      if (cache.get(keys.get(index)) != null) {
        if (rezult == null) {
          rezult = cache.get(keys.get(index));       
        } else {
          rezult += " ";
          rezult += cache.get(keys.get(index));
        }
      }
    } 
    return rezult;
  }
}
