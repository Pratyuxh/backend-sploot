package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum Gender {

  MALE(0, "male"),
  FEMALE(1, "female"),
  THIRD_GENDER(2, "third gender");

  Integer value;
  String description;

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  Gender(int value, String description) {
    this.value = value;
    this.description = description;
  }

  static Map<Integer, Gender> authoritiesMap = new HashMap<>();
  static Map<String, Gender> genderNameMap = new HashMap<>();

  static {
    for (Gender generalStatus : Gender.values()) {
      authoritiesMap.put(generalStatus.getValue(), generalStatus);
      genderNameMap.put(generalStatus.getDescription(), generalStatus);
    }
  }

  public static Gender valueOf(Integer enumValue) {
    return authoritiesMap.get(enumValue);
  }

  public static Gender getGenderByName(String enumDescription) {
    return genderNameMap.get(enumDescription);
  }
}