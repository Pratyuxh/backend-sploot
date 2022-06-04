package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum PushNotificationTemplateType {
	REMINDER(0, "Reminder about to expire"),
	INACTIVITY(6, "User being inactive for some period"),
	MEDICAL(1, "Notification related to medical category"),
	RECENT_BLOG_POST(2, "Recent Blog Post related"),
	INCOMPLETE_USER_PROFILE(4, "Incomplete User Profile"),
	INCOMPLETE_PET_PROFILE(3, "Incomplete Pet Profile");

	Integer screenType;
	String description;

	public Integer getScreenType() {
		return screenType;
	}

	public String getDescription() {
		return description;
	}

	PushNotificationTemplateType(int screenType, String description) {
		this.screenType = screenType;
		this.description = description;
	}

	static Map<Integer, PushNotificationTemplateType> authoritiesMap = new HashMap<>();

	static {
		for (PushNotificationTemplateType generalStatus : PushNotificationTemplateType.values()) {
			authoritiesMap.put(generalStatus.getScreenType(), generalStatus);
		}
	}

	public static PushNotificationTemplateType valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}

}
