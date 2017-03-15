package start.application.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;

/**
 * 日期时间工具类
 * @author Start
 */
public class TimeUtils {
	
	private final static Logger log=LoggerFactory.getLogger(TimeUtils.class);
	
	public final static String yyyyMMdd_C="yyyy年MM月dd日";
	
	public final static String yyyyMM_F="yyyy-MM";
	public final static String yyyyMMdd_F="yyyy-MM-dd";
	public final static String yyyyMMddHHmmss_F="yyyy-MM-dd HH:mm:ss";
	public final static String yyyyMMddHHmmsssss_F="yyyy-MM-dd HH:mm:ss,SSS";

	public final static String yyyy="yyyy";
	public final static String yyyyMM="yyyyMM";
	public final static String yyyyMMdd="yyyyMMdd";
	public final static String yyyyMMddHH="yyyyMMddHH";
	public final static String yyyyMMddHHmmss="yyyyMMddHHmmss";
	public final static String yyyyMMddHHmmsssss="yyyyMMddHHmmssSSS";
	/**
	 * 取得系统时间
	 */
    public static String getSysTime(String pattern) {
        return formatSysTime(new SimpleDateFormat(pattern));
    }

    /**
     * 格式化系统时间
     */
    private static String formatSysTime(SimpleDateFormat format) {
        String str = format.format(Calendar.getInstance().getTime());
        return str;
    }

    public static String format(Date date, String pattern) {
    	SimpleDateFormat format = new SimpleDateFormat(pattern);
        String str = format.format(date);
		return str;
    }

    public static Date getDate(int days) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime(); 
    }

    public static Date getHour(int hours) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime(); 
    }

    public static boolean validTime(String str, String pattern) {
    	DateFormat formatter = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = (Date) formatter.parse(str);
		} catch (ParseException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			return false;
		}
		return str.equals(formatter.format(date));
	}

    public static Date format(String str, String pattern) {
		try {
	    	DateFormat formatter = new SimpleDateFormat(pattern);
			return (Date) formatter.parse(str);
		} catch (ParseException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			return null;
		}
	}

    public static String getSysYear() {
        return getSysTime(yyyy);
    }

    public static String getSysTime() {
        return getSysTime(yyyyMMddHHmmss_F);
    }

    public static String getSysTimeS() {
        return getSysTime(yyyyMMddHHmmsssss_F);
    }

    public static String getSysTimeLong() {
        return getSysTime(yyyyMMddHHmmss);
    }

    public static String getSysTimeSLong() {
        return getSysTime(yyyyMMddHHmmsssss);
    }

    public static String getSysdate() {
        return getSysTime(yyyyMMdd_F);
    }

    public static String getSysyearmonthInt() {
        return getSysTime(yyyyMM);
    }

    public static String getSysdateInt() {
        return getSysTime(yyyyMMdd);
    }

    public static String getSysdateTimeStart() {
        return getSysdate() + " 00:00:00";
    }

    public static String getSysdateTimeEnd() {
        return getSysdate() + " 23:59:59";
    }

    public static String getSysDateLocal() {
        return getSysTime(yyyyMMdd_C);
    }

    public static String getTimeFormat(String str) {
        return format(format(str, yyyyMMddHHmmss), yyyyMMddHHmmss_F);
    }

    public static String getDateFormat(String str) {
        return format(format(str, yyyyMMddHHmmss_F), yyyyMMdd_F);
    }

    public static String getDateFormatLocal(String str) {
        return format(format(str, yyyyMMddHHmmss_F), yyyyMMdd_C);
    }
    /**
     * 获取前一天日期格式
     */
    public static String getTheDayBefore() {
        return format(getDate(-1), yyyyMMdd);
    }
    /**
     * 获取前一天日期格式
     */
    public static String getTheDayBefore_F() {
        return format(getDate(-1), yyyyMMdd_F);
    }

    public static String getDateFormat(int days) {
        return format(getDate(days), yyyyMMdd_F);
    }

    public static String getDateFormatLocal(int days) {
        return format(getDate(days), yyyyMMdd_C);
    }

    public static String getTimeFormatHour(int hours) {
        return format(getHour(hours), yyyyMMddHHmmss_F);
    }

	public static Date getHourTime(int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
//		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getHourTime(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
//		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getTomorrowHourTime(int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getTomorrowHourTime(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getThismonthTime(int day, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getNextmonthTime(int day, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getMinute(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minute);
		Date time = calendar.getTime();
		return time;
	}

	public static Date getSecond(int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, second);
		Date time = calendar.getTime();
		return time;
	}

	public static long subtract(String start) {
	    return format(getSysTimeS(), yyyyMMddHHmmsssss_F).getTime() - format(start, yyyyMMddHHmmsssss_F).getTime();
	}

	public static long subtract(String end, String start) {
	    return format(end, yyyyMMddHHmmsssss_F).getTime() - format(start, yyyyMMddHHmmsssss_F).getTime();
	}

	public static String getGMTTime(String TimeZoneFormat) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
		String time = format.format(Calendar.getInstance().getTime());
		return time;
	}

	public static String getGMTTime() {
		return getGMTTime("GMT");
	}

	public static String getGMT8Time() {
		return getGMTTime("GMT+08:00");
	}

    public static String formatGMTTime(String str, String pattern, String TimeZoneFormat) {
		DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		Date date = null;
		try {
			date = (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
		String time = format.format(date);
		return time;
	}

    public static String formatTime(String str, String strpattern, String pattern, String TimeZoneFormat) {
		DateFormat dateFormat = new SimpleDateFormat(strpattern, Locale.ENGLISH);
		Date date = null;
		try {
			date = (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
		String time = format.format(date);
		return time;
	}

    public static String formatGMTTime(String str, String pattern) {
    	return formatGMTTime(str, pattern, "GMT");
	}

    public static String formatGMT8Time(String str, String pattern) {
    	return formatGMTTime(str, pattern, "GMT+08:00");
	}

    public static String formatGMTTime(String str) {
    	return formatGMTTime(str, yyyyMMddHHmmss_F, "GMT");
	}

    public static String formatGMT8Time(String str) {
    	return formatGMTTime(str, yyyyMMddHHmmss_F, "GMT+08:00");
	}

}