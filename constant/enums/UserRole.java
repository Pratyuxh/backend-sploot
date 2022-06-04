package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserRole {
	ADMIN(1, "ADMIN_ROLE"),
	USER(2, "USER"),
	ANON(3, "ANON");

	Integer authorityCode;
	String discription;

	public Integer getAuthorityCode() {
		return authorityCode;
	}

	public String getDiscription() {
		return discription;
	}

	UserRole(int value, String discription) {
		this.authorityCode = value;
		this.discription = discription;
	}

	static Map<Integer, UserRole> authoritiesMap = new HashMap<>();

	static {
		for (UserRole userRole : UserRole.values()) {
			authoritiesMap.put(userRole.getAuthorityCode(), userRole);
		}
	}

	public static UserRole valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}
}
