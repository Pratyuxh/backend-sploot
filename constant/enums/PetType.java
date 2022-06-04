package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

//TODO: deprecate this
public enum PetType {
  Dog(1, "dog"),
  Cat(2, "cat");

  Integer value;
  String description;

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  PetType(int value, String description) {
    this.value = value;
    this.description = description;
  }

  static Map<Integer, PetType> authoritiesMap = new HashMap<>();

  static {
    for (PetType generalStatus : PetType.values()) {
      authoritiesMap.put(generalStatus.getValue(), generalStatus);
    }
  }

  public static PetType valueOf(Integer enumValue) {
    return authoritiesMap.get(enumValue);
  }
}
