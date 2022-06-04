package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum SocialType {
	google(1, "google"),
	portal(3, "portal"),
	facebook(2, "facebook"),
	apple(4, "apple");


	Integer value;
	String description;

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	SocialType(int value, String description) {
		this.value = value;
		this.description = description;
	}

	static Map<Integer, SocialType> authoritiesMap = new HashMap<>();

	static {
		for (SocialType generalStatus : SocialType.values()) {
			authoritiesMap.put(generalStatus.getValue(), generalStatus);
		}
	}

	public static SocialType valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}
}
