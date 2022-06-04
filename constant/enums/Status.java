package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	ACTIVE(0, "active"),
	INACTIVE(1, "inactive"),
	DELETED(2, "deleted");

	Integer value;
	String description;

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	Status(int value, String description) {
		this.value = value;
		this.description = description;
	}

	static Map<Integer, Status> authoritiesMap = new HashMap<>();

	static {
		for (Status generalStatus : Status.values()) {
			authoritiesMap.put(generalStatus.getValue(), generalStatus);
		}
	}

	public static Status valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}
}
