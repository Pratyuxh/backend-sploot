package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum EmailTemplateType {
    SIGNUP_OTP(1, "OTP"),
    FORGOT_PASSWORD_OTP(2, "Institute Confirmation"),
    WELCOME_MAILER(3, "Institute Confirmation");

    Integer value;
    String description;

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    EmailTemplateType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    static Map<Integer, EmailTemplateType> authoritiesMap = new HashMap<>();

    static {
        for (EmailTemplateType generalStatus : EmailTemplateType.values()) {
            authoritiesMap.put(generalStatus.getValue(), generalStatus);
        }
    }

    public static EmailTemplateType valueOf(Integer enumValue) {
        return authoritiesMap.get(enumValue);
    }
}
