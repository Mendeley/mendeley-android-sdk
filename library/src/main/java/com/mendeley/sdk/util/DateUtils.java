package com.mendeley.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    // ISO 8601 format, used by the Mendeley web API for timestamps.
    public final static SimpleDateFormat mendeleyApiDateFormat;

    private final static SimpleDateFormat yearMonthDateDateFormat;

    static {
        mendeleyApiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        mendeleyApiDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        yearMonthDateDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    /**
     * Returns a {@link java.util.Date} given one String with a timestamp in the format used by the web API.
     *
     * @param date in the format used by Mendeley web API
     * @return parsed date
     * @throws java.text.ParseException
     */
    public static Date parseMendeleyApiTimestamp(String date) throws ParseException {
        synchronized (mendeleyApiDateFormat) {
            return mendeleyApiDateFormat.parse(date);
        }
    }

    public static String formatMendeleyApiTimestamp(Date date) {
        synchronized (mendeleyApiDateFormat) {
            return mendeleyApiDateFormat.format(date);
        }
    }

    public synchronized static Date parseYearMonthDayDate(String date) throws ParseException {
        return yearMonthDateDateFormat.parse(date);
    }

    public static String formatYearMonthDayDate(Date date) {
        synchronized (yearMonthDateDateFormat) {
            return yearMonthDateDateFormat.format(date);
        }
    }
}
