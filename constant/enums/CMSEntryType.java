package com.sploot.api.constant.enums;

public enum CMSEntryType {
    EXPLORE("explore"),
    MEMORY("memory"),
    CHALLENGE("challenge"),
    READS("reads");

    private String type;

    CMSEntryType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return type;
    }

    public static CMSEntryType getTypeFromDescription(String value) {
        for (CMSEntryType type : CMSEntryType.values()) {
            if (type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
