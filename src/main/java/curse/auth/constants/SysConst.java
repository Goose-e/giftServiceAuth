package curse.auth.constants;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SysConst {
    private SysConst() {}

    public static final String VERSION_1_0_0 = "1.0.0";
    public static final String CRLF = "\n";
    public static final String NOT_DEFINED = "?";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String NOT_ASSIGNED = "NOT ASSIGNED";
    public static final String NULL = "null";
    public static final String ZERO_STRING = "0";
    public static final String EMPTY_STRING = "";
    public static final String BLANK_SYMBOL = " ";
    public static final String DATETIME_MS_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    public static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_MS = "HH:mm:ss.SSS";
    public static final String APP_LOCALE = "ru";
    public static final String LOG_PKG_NAME = "root-service";

    public static final DateTimeFormatter FORMAT_dd_MM_yyyy__HH_mm_ss = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    public static final DateTimeFormatter FORMAT_dd_MM_yyyy = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final boolean SSL = true;
    public static final boolean NO_SSL = !SSL;
    public static final String K9S_MODE = "server.k9sMode";
    public static final Duration ONE_SECOND = Duration.ofSeconds(1);
    public static final Duration TEN_SECONDS = Duration.ofSeconds(10);
    public static final long MILLIS_1000 = 1000L;
    public static final int TIMEOUT_10000_MILLIS = 10000;
    public static final long TIMEOUT_10000_MILLIS_LONG = 10000L;
    public static final Duration TIMEOUT_30_SEC = Duration.ofSeconds(30);
    public static final Duration TIMEOUT_60_SEC = Duration.ofSeconds(60);
    public static final Duration TIMEOUT_120_SEC = Duration.ofSeconds(120);
    public static final int ONE_ATTEMPT = 1;
    public static final int TWO_ATTEMPTS = 2;
    public static final int FIVE_ATTEMPTS = 5;
    public static final String DOCKER_FOLDER = "/opt/";
    public static final String KOTLIN_VERSION = "KotlinVersion = " + "N/A" + ", JavaVersion = " + Runtime.version();

    public static final long LONG_ZERO = 0L;
    public static final long LONG_ONE = 1L;
    public static final long SERVICE_USER_ID = LONG_ONE;
    public static final Integer INT_NULL = null;
    public static final Long LONG_NULL = null;
    public static final int INT_10POW9 = 1000000000;
    public static final int INTEGER_ZERO = 0;
    public static final int INTEGER_ONE = 1;
    public static final BigDecimal BIGDECIMAL_ZERO = BigDecimal.ZERO;
    public static final double DOUBLE_ZERO = 0.0;
    public static final Integer INTEGER_NULL = null;
    public static final LocalDate LOCALDATE_NULL = null;
    public static final LocalDateTime LOCALDATETIME_NULL = null;
    public static final String STRING_NULL = null;
    public static final String STRING_ONE = "1";
    public static final String STRING_ZERO = "0";
    public static final String STRING_TEN = "10";
    public static final String STRING_TRUE = "true";
    public static final String STRING_FALSE = "false";
    public static final byte[] BYTES_NULL = null;

    public static final String OC_OK = "OC_OK";
    public static final String OC_BUGS = "OC_BUGS";
    public static final String USER_BANNED = "OC_BUGS";
    public static final String INVALID_ENTITY_ATTR = "INVALID ENTITY ATTR";
    public static final String SAVE_USER_ERROR = "SAVE USER ERROR";
    public static final String CREATE_USER_ERROR = "CREATE USER ERROR";
    public static final String CREATE_COMPANY_ERROR = "CREATE COMPANY ERROR";

    public static final Class<Void> VOID_CLASS = Void.class;
    public static final Class<BigDecimal> BIGDECIMAL_CLASS = BigDecimal.class;
    public static final Class<LocalDate> LOCALDATE_CLASS = LocalDate.class;
    public static final Class<LocalDateTime> LOCALDATETIME_CLASS = LocalDateTime.class;
    public static final Class<String> STRING_CLASS = String.class;
    public static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;
    public static final Class<Integer> INTEGER_CLASS = Integer.class;
    public static final Class<Long> LONG_CLASS = Long.class;

}
