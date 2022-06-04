package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum PersonType {
	pet(1, "pet"),
	user(2, "user");

	Integer value;
	String description;

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	PersonType(int value, String description) {
		this.value = value;
		this.description = description;
	}

	static Map<Integer, PersonType> authoritiesMap = new HashMap<>();

	static {
		for (PersonType generalStatus : PersonType.values()) {
			authoritiesMap.put(generalStatus.getValue(), generalStatus);
		}
	}

	public static PersonType valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}
}
