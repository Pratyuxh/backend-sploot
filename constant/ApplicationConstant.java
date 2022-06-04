package com.sploot.api.constant;

import com.sploot.api.util.EnvUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ApplicationConstant {
    public static final String USER_NAME = "user_name";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";


    public static final String DUMMY_NAME = "DUMMY NAME";
    public static final Integer ERROR_STATUS_CODE = 0;
    public static final Integer HTTP_RESPONSE_ERROR_CODE = 500;
    public static final Integer HTTP_RESPONSE_SUCCESS_CODE = 200;
    public static final Integer MAX_NO_OF_LOGIN_INFO = 3;
    public static final Integer SUCCESS_STATUS_CODE = 1;
    public static final Long SYSTEM_ID = -1L;
    public static final String USER_ID = "userId";
    public static final String USER_STATUS = "userStatus";
    public static final String IS_ACCOUNT_VERIFIED = "isAccountVerified";
    public static final String FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public static final int OTP_EXPIRY_TIME_IN_MINUTES = 3;
    public static final Integer noOfMonthsForExchangeData = 3;
    public static final String X_AUTH_TOKEN = "authorization";
    public static final Integer SERVER_ERROR = 500;
    public static final Integer SERVER_OK = 200;
    public static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public static final String YYYYMMDD_hyphen = "yyyy-MM-dd";
    public static final String YYYYMMDD_slash = "yyyy/MM/dd";
    public static final String DDMMYYYY_hyphen = "dd-MM-yyyy";
    public static final String DDMMYYYY_HHMMSS_hyphen = "dd-MM-yyyy HH:mm:ss:SSS";
    public static final String HHMMSS_SSS = "HH:mm:ss:SSS";
    public static final String HHMM = "HH:mm";
    public static final String DDMMYYYY_slash = "dd/MM/yyyy";
    public static final String YYYYMMMMMd = "yyyy MMMMM d";//2001 january 5
    public static final String YYYYdMMMMM = "yyyy d MMMMM";//2001 5 january
    public static final String dMMMMMYYYY = "d MMMMM yyyy";//5 january 2001
    public static final String MMMMMd = "MMMMM d";//january 5
    public static final String dMMMMM = "d MMMMM";//5 january
    public static final Integer ESSAY_MAX_MARKS = 300;
    public static final String VENDOR_ROLE = "ROLE_VENDOR";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final List<String> BYPASSED_URLS = new LinkedList<String>() {{
        add("/");
    }};
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final String BLANK_STRING = "";
    public static final long ANON_INSTITUTE_ID = -2l;
    public static final Integer ZERO_SCORE = 0;
    public static final Integer ONE_PERIOD_A_DAY = 1;
    public static final Integer ZERO_VALUE = 0;
    public static final String UPLOAD_STUDENTS_STR = "uploadStudents";
    public static final String UPLOAD_TEACHERS_STR = "uploadTeachers";
    public static final String UPLOAD_PERIODS_STR = "uploadPeriods";
    public static final String TOPIC_ATTENDANCE = "attendance";
    public static final String DEFAULT_DOMAIN = "@reppy.com";
    public static final int OTP_DEFAULT_LENGTH = 4;
    public static final String JPG_FORMAT = "jpg";
    public static final String PROD_ENVIRONMENT = "prod";
    public static final int NUM_THREADS_DEFAULT = 10;
    public static final long MESSAGE_POLL_WAIT_TIME_MILLIS = 10000;//10 sec
    public static final String DEV_REMINDER_TOPIC = "sploot-dev-reminder";
    public static final String TEST_REMINDER_TOPIC = "sploot-test-reminder";
    public static final String PROD_REMINDER_TOPIC = "sploot-prod-reminder";
    public static final String DEV_PET_PROFILE_TOPIC = "sploot-dev-pet-profile";
    public static final String TEST_PET_PROFILE_TOPIC = "sploot-test-pet-profile";
    public static final String PROD_PET_PROFILE_TOPIC = "sploot-prod-pet-profile";
    public static final String OBJECT_HASH_KEY = "map";
    public static final Integer NUM_INACTIVITY_DAYS = 3;
    public static final long LAST_LOGIN_UPDATE_THRESHOLD = 30 * 60 * 1000; // 30 mins
    public static final Integer PERIODIC_REMINDER_FUTURE_DAYS_LIMIT = 100;
    public static final List<String > WHITELISTED_IPS = Arrays.asList("3.23.187.253", "172.31.47.221");
    public static final Long NUM_MILLISECONDS_IN_A_DAY = 86400000l;
    public static final String WEEKLY_USERS_REPORT_HTML = "<p>Hi <br/> Please find the users report for sploot for this week attached in this mail. <br/> Thanks </br> Team Sploot</p>";
    public static final String TEST_ENVIRONMENT = "test";
    public static final String POPULAR_SORTED_PETS_STR = EnvUtils.getEnv() + "_popularity_sorted_pets";
    public static final long TTL_DEFAULT = 86400 * 1000 * 30;
    public static final long TTL_DEFAULT_DAYS = 30;

    //These are categories under which the essay will be evaluated
    public static Integer quotesScore = 20;
    public static Integer introScore = 30;
    public static Integer conclusionScore = 30;
    public static Integer validPointsScore = 120;
    public static Integer numWordsScore = 20;
    public static Integer languageCorrectnessScore = 50;
    public static Integer languageBeautyScore = 30;
    public static long ADMIN_ID = 1;
}
