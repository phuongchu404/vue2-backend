package vn.mk.eid.common.util;

import lombok.extern.slf4j.Slf4j;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.exception.ServiceException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author mk.com.vn
 * @date 2018/9/29 13:51
 */
@Slf4j
public class DateUtils {
    public final static String DEFAULT_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static Long getTimes(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDateTime
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String getFormatStr(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime);
    }

    public static String getFormatStr(LocalDateTime localDateTime) {
        return DateUtils.getFormatStr(localDateTime, DEFAULT_PATTERN);
    }

    public static LocalDateTime dateParseLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String formatDatetime(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            return formater.format(date);
        } catch (Exception ex) {
        }
        return null;
    }

    public static String formatDatetime(long time, String format) {
        Date date = new Date(time);
        return formatDatetime(date, format);
    }

    public static String formatCurrentTime(String format) {
        return formatDatetime(new Date(), format);
    }

    public static LocalDate convertStringToLocalDate(String date, String pattern) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("[CONVERT_DATE] error: {}", e.getMessage());
            throw new ServiceException(ResultCode.VALIDATE_ERROR.getCode(), ExceptionConstants.TIME_INVALID);
        }
    }
}