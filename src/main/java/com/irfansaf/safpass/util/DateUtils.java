package com.irfansaf.safpass.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DateUtils {
    private static final Logger LOG = Logger.getLogger(DateUtils.class.getName());

    private DateUtils() {
        // utility class
    }

    public static DateTimeFormatter createFormatter(String format) {
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(format);
        } catch (IllegalArgumentException | NullPointerException e) {
            LOG.log(Level.WARNING, String.format("Could not parse date format [%s] due to [%s]", format, e.getMessage()));
            formatter = DateTimeFormatter.ISO_DATE;
        }
        return formatter;
    }

    public static String formatIsoDateTime(String dateString, DateTimeFormatter formatter) {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException | NullPointerException e) {
            try {
                // fallback to epoch timestamp
                Date date = new Date(Long.parseLong(dateString));
                dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } catch (NumberFormatException | DateTimeParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, String.format("Could not parse timestamp [%s] due to [%s]", dateString, ex.getMessage()));
                dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
            }
        }
        return formatter.format(dateTime.truncatedTo(ChronoUnit.SECONDS));
    }
}
