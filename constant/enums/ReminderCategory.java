package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum ReminderCategory {
  DAILY_ROUTINE(0, "Daily Routine"),
  PERIODIC_TREATMENT(1, "Periodic Treatment"),
  MEDICAL_HEALTH(2, "Medical / Health");


  Integer value;
  String description;

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  ReminderCategory(int value, String description) {
    this.value = value;
    this.description = description;
  }

  static Map<Integer, ReminderCategory> authoritiesMap = new HashMap<>();

  static {
    for (ReminderCategory generalStatus : ReminderCategory.values()) {
      authoritiesMap.put(generalStatus.getValue(), generalStatus);
    }
  }

  public static ReminderCategory valueOf(Integer enumValue) {
    return authoritiesMap.get(enumValue);
  }
}