package com.sploot.api.constant.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ReminderPeriod {
  NEVER(0, "One Time", 0),
  HOURLY(1, "Hourly", 60),
  DAILY(2, "Daily", 1440),
  WEEKLY(3, "Weekly", 1440 * 7),
  MONTHLY(4, "Monthly", 1440 * 30),
  YEARLY(5, "Yearly", 1440 * 365);


  Integer timeIntervalInMins;
  Integer value;
  String description;

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  ReminderPeriod(int value, String description, int timeIntervalInMins) {
    this.value = value;
    this.description = description;
    this.timeIntervalInMins = timeIntervalInMins;
  }

  static Map<Integer, ReminderPeriod> map = new HashMap<>();

  static {
    for (ReminderPeriod generalStatus : ReminderPeriod.values()) {
      map.put(generalStatus.getValue(), generalStatus);
    }
  }

  public static ReminderPeriod valueOf(Integer enumValue) {
    return map.get(enumValue);
  }
}