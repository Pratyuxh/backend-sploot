package com.sploot.api.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum MedicalRecordDisplayableField {

	text(0, "Text"),
	imageUrl(1, "Image Url");

	Integer value;
	String description;

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	MedicalRecordDisplayableField(int value, String description) {
		this.value = value;
		this.description = description;
	}

	static Map<Integer, MedicalRecordDisplayableField> authoritiesMap = new HashMap<>();
	static Map<String, MedicalRecordDisplayableField> genderNameMap = new HashMap<>();

	static {
		for (MedicalRecordDisplayableField generalStatus : MedicalRecordDisplayableField.values()) {
			authoritiesMap.put(generalStatus.getValue(), generalStatus);
			genderNameMap.put(generalStatus.getDescription(), generalStatus);
		}
	}

	public static MedicalRecordDisplayableField valueOf(Integer enumValue) {
		return authoritiesMap.get(enumValue);
	}

	public static MedicalRecordDisplayableField getGenderByName(String enumDescription) {
		return genderNameMap.get(enumDescription);
	}
}
