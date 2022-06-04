package com.sploot.api.constant;

public class TableColumns {

	public static final String CLASS_ROOM = "class_rooms";
	public static final String CLASSROOM_STUDY_LEVEL = "class_standard_id";
	public static final String CLASSROOM_STUDY_SECTION = "section_id";
	public static final String CLASSROOM_INSTITUTE_SHIFT = "shift_id";

	public static final String CLASS_ROOM_PROFILE = "class_room_profiles";
	public static final String CLASS_ROOM_PROFILE_CLASS_ROOM = "class_room_id";
	public static final String CLASS_ROOM_PROFILE_CLASS_TEACHER = "teacher_id";
	public static final String CLASS_ROOM_PROFILE_INSTITUTE = "institute_id";

	public static final String DEPARTMENT = "departments";
	public static final String DEPARTMENT_NAME = "name";

	public static final String INSTITUTE_SHIFT = "institute_shifts";
	public static final String INSTITUTE_SHIFT_INSTITUTE = "institute";
	public static final String INSTITUTE_SHIFT_NAME = "name";

	public static final String STUDENT_TIME_TABLE = "student_time_tables";
	public static final String STUDENT_TIME_TABLE_STUDENT = "student_id";
	public static final String STUDENT_TIME_TABLE_CLASS_ROOM = "class_room_id";
	public static final String STUDENT_TIME_TABLE_SUBJECT = "subject_id";
	public static final String STUDENT_TIME_TABLE_TIME_SLOT = "time_slot_id";
	public static final String STUDENT_TIME_TABLE_TEACHER = "teacher_id";
	public static final String STUDENT_TIME_TABLE_DAY = "day";

	public static final String STUDY_LEVEL = "study_levels";
	public static final String STUDY_LEVEL_NAME = "name";
	public static final String STUDY_LEVEL_INSTITUTE_SHIFT_MAPPING = "study_level_shift_mapping";
	public static final String STUDY_LEVEL_INSTITUTE_SHIFT_MAPPING_INSTITUTE_SHIFT = "shift_id";
	public static final String STUDY_LEVEL_INSTITUTE_SHIFT_MAPPING_STUDY_LEVEL = "study_level_id";

	public static final String STUDY_SECTION = "study_section";
	public static final String STUDY_SECTION_NAME = "name";

	public static final String SUBJECT = "subjects";
	public static final String SUBJECT_NAME = "name";
	public static final String SUBJECT_NAME_PREREQUISITES = "prerequisites";
	public static final String SUBJECT_DEPARTMENT = "department_id";

	public static final String TEACHER_TIME_TABLE = "teacher_time_tables";
	public static final String TEACHER_TIME_TABLE_TEACHER = "teacher_id";
	public static final String TEACHER_TIME_TABLE_CLASS_ROOM = "class_room_id";
	public static final String TEACHER_TIME_TABLE_SUBJECT = "subject_id";
	public static final String TEACHER_TIME_TABLE_TIME_SLOT = "time_slot_id";
	public static final String TEACHER_TIME_TABLE_DAY = "day";

	public static final String TIME_SLOT = "time_slots";
	public static final String TIME_SLOT_NAME = "name";
	public static final String TIME_SLOT_END_TIME = "end_time";
	public static final String TIME_SLOT_START_TIME = "start_time";

	public static final String STUDENT_PROFILE = "student_profiles";
	public static final String STUDENT_PROFILE_USER = "user_id";
	public static final String STUDENT_PROFILE_REFERRAL_CODE = "referral_code";
	public static final String STUDENT_PROFILE_LAST_NAME = "last_name";
	public static final String STUDENT_PROFILE_USER_TYPE = "user_type";
	public static final String STUDENT_PROFILE_JOINING_DATE = "joining_date";
	public static final String STUDENT_PROFILE_UPI_ID = "upi_id";
	public static final String STUDENT_PROFILE_POINTS_BALANCE = "points";
	public static final String STUDENT_PROFILE_FATHER = "father_id";
	public static final String STUDENT_PROFILE_MOTHER = "mother_id";
	public static final String STUDENT_PROFILE_CLASSROOM_PROFILE = "classroom_id";
	public static final String STUDENT_PROFILE_INSTITUTE = "institute_id";
	public static final String STUDENT_PROFILE_GENDER = "gender";
	public static final String STUDENT_PROFILE_ROLL_NUMBER = "roll_number";

	public static final String USER_AUTHORITY = "authority";
	public static final String USER_AUTHORITY_NAME = "name";

	public static final String USER_SUBJECT_PROFILE_SUBJECTS_MAPPING = "user_subjects_mapping";
	public static final String USER_SUBJECT_PROFILE_SUBJECTS_MAPPING_SUBJECT = "subject_id";
	public static final String USER_SUBJECT_PROFILE_SUBJECTS_MAPPING_USER_PROFILE = "user_profile_id";

	public static final String INSTITUTE_SHIFT_TIME_SLOT_MAPPING = "shift_time_slot_mapping";
	public static final String INSTITUTE_SHIFT_TIME_SLOT_MAPPING_INSTITUTE_SHIFT = "institute_shift_id";
	public static final String INSTITUTE_SHIFT_TIME_SLOT_MAPPING_TIME_SLOT = "time_slot_id";
	public static final String INSTITUTE_SHIFT_NUM_DAYS = "days_working";

	public static final String USER_DEVICES = "user_devices";
	public static final String  USER_DEVICES_USER = "user_id";
	public static final String USER_DEVICES_MODEL = "model";
	public static final String USER_DEVICES_IMEI = "imei";
	public static final String USER_DEVICES_OS = "os";
	public static final String FIRE_BASE_ID ="fire_base_id";

	public static final String CLASS_ROOM_SUBJECT = "class_room_subjects";
	public static final String CLASS_ROOM_SUBJECT_CLASSROOM = "class_room_id";
	public static final String CLASS_ROOM_SUBJECT_SUBJECT = "subject_id";
	public static final String CLASS_ROOM_SUBJECT_OPTIONAL = "optional";
	public static final String CLASS_ROOM_SUBJECT_WEEKLY_DURATION = "week_limit";
	public static final String CLASS_ROOM_SUBJECT_MAX_PERIODS_A_DAY = "daily_limit";

	public static final String TEACHER_CLASSES = "teacher_classes";
	public static final String TEACHER_CLASSES_TEACHER = "teacher_id";
	public static final String TEACHER_CLASSES_STUDY_LEVEL = "standard_id";
	public static final String TEACHER_CLASSES_INSTI_SHIFT = "shift_id";

	public static final String STAFF_PROFILE = "staff_profiles";
	public static final String STAFF_PROFILE_USER = "user_id";
	public static final String STAFF_PROFILE_REFERRAL_CODE = "referral_code";
	public static final String STAFF_PROFILE_LAST_NAME = "last_name";
	public static final String STAFF_PROFILE_USER_TYPE = "user_type";
	public static final String STAFF_PROFILE_JOINING_DATE = "joining_date";
	public static final String STAFF_PROFILE_UPI_ID = "upi_id";
	public static final String STAFF_PROFILE_POINTS_BALANCE = "points";
	public static final String STAFF_PROFILE_INSTITUTE = "institute_id";
	public static final String STAFF_PROFILE_GENDER = "gender";
	public static final String STAFF_PROFILE_MIN_WEEKLY_HOURS = "min_hours";

	public static final String GUARDIAN_PROFILE = "guardian_profiles";
	public static final String GUARDIAN_PROFILE_USER = "user_id";
	public static final String GUARDIAN_PROFILE_REFERRAL_CODE = "referral_code";
	public static final String GUARDIAN_PROFILE_LAST_NAME = "last_name";
	public static final String GUARDIAN_PROFILE_JOINING_DATE = "joining_date";
	public static final String GUARDIAN_PROFILE_MEMBERSHIP_TYPE = "membership_type";
	public static final String GUARDIAN_PROFILE_UPI_ID = "upi_id";
	public static final String GUARDIAN_PROFILE_POINTS_BALANCE = "points";
	public static final String GUARDIAN_PROFILE_GENDER = "gender";
	public static final String GUARDIAN_PROFILE_CUSTODY_FOR = "custody_for";

	public static final String GENERIC_PROFILE = "generic_profiles";
	public static final String GENERIC_PROFILE_USER = "user_id";
	public static final String GENERIC_PROFILE_ADDRESS = "address_id";
	public static final String GENERIC_PROFILE_MEMBERSHIP_TYPE = "membership_type";
	public static final String GENERIC_PROFILE_POINTS_BALANCE = "points";

	public static final String INSTITUTE_PROFILE = "institute_profiles";
	public static final String INSTITUTE_PROFILE_USER = "user_id";
	public static final String INSTITUTE_PROFILE_ADDRESS = "address_id";
	public static final String INSTITUTE_PROFILE_MEMBERSHIP_TYPE = "membership_type";
	public static final String INSTITUTE_PROFILE_POINTS_BALANCE = "points";

	public static final String TEACHER_CLASSES_SUBJECTS_MAPPING = "teacher_class_subjects_mapping";
	public static final String TEACHER_CLASSES_SUBJECTS_MAPPING_SUBJECT = "subject_id";
	public static final String TEACHER_CLASSES_SUBJECTS_MAPPING_TEACHER_CLASS = "teacher_class_id";

	public static final String COLUMN_DEFINITION_DATE_TIME = "time not null";
	public static final String COLUMN_DEFINITION_INT_DEFAULT_40 = "int(11) not null default 40";
	public static final String COLUMN_DEFINITION_INT_DEFAULT_5 = "int(5) not null default 5";
	public static final String COLUMN_DEFINITION_INT_DEFAULT_1 = "int(5) not null default 1";
	public static final String COLUMN_DEFINITION_INT_DEFAULT_8 = "int(5) not null default 8";

	public static final String PROJECT_COMPONENT = "project_component";
	public static final String PROJECT_COMPONENT_NAME = "name";
	public static final String PROJECT_COMPONENT_AUTHORITIES_MAPPING = "project_component_authorities_mapping";
	public static final String PROJECT_COMPONENT_AUTHORITIES_MAPPING_COMPONENT_ID = "component_id";
	public static final String PROJECT_COMPONENT_AUTHORITIES_MAPPING_AUTHORITY_ID = "authority_id";
	public static final String PROJECT_COMPONENT_IS_PARENT = "is_parent";
	public static final String PROJECT_COMPONENT_PARENT_ID = "parent_id";
	public static final String TEACHER_ROLL_NUMBER = "roll_number";
	public static final String USER_PROFILE = "user_profile";
	public static final String USER_PROFILE_USER = "user_id";
	public static final String EMAIL_SENT_RECORD = "email_sent_record";
	public static final String EMAIL_TEMPLATE = "email_template";
	public static final String EMAIL_TEMPLATE_TYPE = "email_template_type";

	public static final String EMAIL_TEMPLATE_TEXT = "template_text";
	public static final String EMAIL_TEMPLATE_SUBJECT = "template_email_subject";
	public static final String EMAIL_TO_BE_SAVED = "email_to_be_saved";
	public static final String USER_OTP_RECORD = "user_otp_record";
	public static final String USER_OTP_USER = "user_id";
	public static final String PET_PROFILE = "pet_profile";
	public static final String PET_USER = "pet_user_id";
	public static final String PET_TYPE = "pet_type";
	public static final String PET_NAME = "name";
	public static final String PET_BREED = "pet_breed";
	public static final String PET_BREED_ID = "pet_breed_id";
	public static final String LOGO_URL = "logo_url";
	public static final String PROFILE_PIC_URL = "profile_pic_url";
	public static final String PET_GENDER = "gender";
	public static final String DATE_OF_BIRTH = "date_of_birth";

	public static final String AGE = "age";
	public static final String ALERT_ENABLED = "alert";
	public static final String REMINDER_TYPE = "reminder_type";
	public static final String REMINDER_TYPE_NAME = "name";
	public static final String REMINDER_TYPE_LOGO = "logo_url";
	public static final String MEDICAL_RECORD_TYPE_LOGO = "logo_url";
	public static final String REMINDER = "reminder";
	public static final String REMINDER_USER = "user_id";
	public static final String REMINDER_TIME = "time" ;
	public static final String REMINDER_NOTES = "notes";
	public static final String REMINDER_TYPE_CATEGORY = "category";
	public static final String REMINDER_PERIOD = "period";
	public static final String REMINDER_USER_PET = "user_pet_id";
	public static final String REMINDER_COMPLETED = "completed";
	public static final String REMINDER_BG_COLOR="bg_color";
	public static final String MEDICAL_RECORD_BG_COLOR="bg_color";
	public static final String REMINDER_SEPARATOR_COLOR="separator_color";
	public static final String MEDICAL_RECORD_SEPARATOR_COLOR="separator_color";
	public static final String MEDICAL_RECORD = "medical_record";
	public static final String MEDICAL_RECORD_TYPE_ID = "medical_record_type_id";
	public static final String MEDICAL_RECORD_PET_PROFILE = "pet_profile_id";
	public static final String MEDICAL_RECORD_IMAGE = "image_url";
	public static final String MEDICAL_RECORD_TEXT = "text_info";
	public static final String MEDICAL_RECORD_DATE = "date_of_record";
	public static final String MEDICAL_RECORD_TYPE_DISPLAY_FIELD = "display_field";
	public static final String MEDICAL_RECORD_USER = "user_id";
	public static final String MEDICAL_RECORD_TYPE = "medical_record_type";
	public static final String MEDICAL_RECORD_TYPE_NAME = "name";
	public static final String REMINDER_TYPE_ID = "reminder_type_id";
	public static final String VALID_TILL = "valid_till";
	public static final String BREED_NAME = "name";
	public static final String OTP_TYPE = "otp_type";
	public static final String HISTORICAL_REMINDER = "historical_reminder";
	public static final String REMINDER_NAME = "name";
	public static final String IS_DEFAULT = "is_default";

	public static final String SECTION_TYPE="section_type";
	public static final String WP_CATEGORY_ID="wp_category_id";
	public static final String SECTION="section";
	public static final String SECTION_TITLE="section_title";

	public static final String REMINDER_TYPE_ORDER = "priority";
	public static final String MEDICAL_RECORD_TYPE_ORDER = "priority";
	public static final String PUSH_NOTIFICATION_TEMPLATE_TEXT = "text";
	public static final String PUSH_NOTIFICATION_TEMPLATE_SUBJECT = "subject";
	public static final String PUSH_NOTIFICATION_TEMPLATE_TYPE = "type";
	public static final String PUSH_NOTIFICATION_SET_VIBRATE = "vibrate";
	public static final String PUSH_NOTIFICATION_SET_SOUND = "sound";
	public static final String PUSH_NOTIFICATION_SHOW_IN_FOREGROUND = "show_in_foreground";
	public static final String PUSH_NOTIFICATION_PRIORITY = "priority";
	public static final String PUSH_NOTIFICATION_CONTENT_AVAILABLE = "content_available";
	public static final String PUSH_NOTIFICATION_TEMPLATE = "push_notification_template";
	public static final String PARENT_REMINDER = "parent_reminder_id";
	public static final String PURGED_REMINDER = "purged_reminder_id";
	public static final String COMMON_PET_BREED = "common_pet_breed";
}
