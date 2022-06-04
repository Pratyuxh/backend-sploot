package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum OtpType {
	FORGOT_PASSWORD(0, "Forgot Password", EmailTemplateType.FORGOT_PASSWORD_OTP),
	USER_SIGNUP(1, "user signup verification", EmailTemplateType.SIGNUP_OTP);

	Integer value;
	String description;
	EmailTemplateType emailTemplateType;

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public EmailTemplateType getEmailTemplateType() {
		return emailTemplateType;
	}

	OtpType(int value, String description, EmailTemplateType emailTemplateType) {
		this.value = value;
		this.description = description;
		this.emailTemplateType = emailTemplateType;
	}

	static Map<Integer, OtpType> authoritiesMap = new HashMap<>();

	static {
		for (OtpType generalStatus : OtpType.values()) {
			authoritiesMap.put(generalStatus.getValue(), generalStatus);
		}
	}

	public static OtpType valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}
}
