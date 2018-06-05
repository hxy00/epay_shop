package com.emt.shoppay.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd",
			"yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @category 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * @category 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * @category 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * @category 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * @category 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss",
	 *           "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss",
	 *           "yyyy/MM/dd HH:mm", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
	 *           "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @category 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * @category 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * @category 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * @category 为时间(天,时:分:秒.毫秒)
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60
				* 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "."
				+ sss;
	}

	/**
	 * @category 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	/**
	 * @category 获取两个日期之间的分钟
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDateMinutes(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60);
	}

	public static String DateToString(Date date, String pattern) {
		String strDateTime = null;
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		strDateTime = date == null ? null : formater.format(date);
		return strDateTime;
	}

	public static String DateToString(Date date) {
		String _pattern = "yyyy-MM-dd";
		return date == null ? null : DateToString(date, _pattern);
	}

	public static String DateTimeToYYYYMMDDhhmmss() {
		String _pattern = "yyyyMMddHHmmss";
		return DateToString(getCurrentDate(), _pattern);
	}

	public static String DateTimeToYYYYMMDDhhmmssSSS() {
		String _pattern = "yyyyMMddHHmmssSSS";
		return DateToString(getCurrentDate(), _pattern);
	}

	public static String DateTimeToYYYYMMDD() {
		String _pattern = "yyyyMMdd";
		return DateToString(getCurrentDate(), _pattern);
	}

    public static String DateTimeToYYYYMM(Date date) {
        String _pattern = "yyyyMM";
        return date ==null ? DateToString(getCurrentDate(), _pattern): DateToString(date, _pattern);
    }
	   
	public static String DateTimeToHHMMssSSS() {
		String _pattern = "HHMMssSSS";
		return DateToString(getCurrentDate(), _pattern);
	}

	public static String DateTimeToYYYYMMDDhhmmss(Date date) {
		String _pattern = "yyyyMMddHHmmss";
		return date == null ? null : DateToString(date, _pattern);
	}

	public static String DateTimeToString() {
		return DateTimeToString(new Date());
	}

	public static String DateTimeToString(Date date) {
		String _pattern = "yyyy-MM-dd HH:mm:ss";
		if(date==null){
		    date=new Date();
		}
		return date == null ? null : DateToString(date, _pattern);
	}

	public static Date StringToDate(String str, String pattern) {
		Date dateTime = null;
		try {
			if (str != null && !str.equals("")) {
				SimpleDateFormat formater = new SimpleDateFormat(pattern);
				dateTime = formater.parse(str);
			}
		} catch (Exception ex) {
		}
		return dateTime;
	}

	public static Date YYYYMMDDhhmmssToDate(String str) {
		return StringToDate(str, "yyyyMMddHHmmss");
	}

	public static Date StringToDate(String str) {
		String _pattern = "yyyy-MM-dd";
		return StringToDate(str, _pattern);
	}

	public static Date StringToDateTime(String str) {
		String _pattern = "yyyy-MM-dd HH:mm:ss";
		return StringToDate(str, _pattern);
	}

	public static Timestamp StringToDateHMS(String str) throws Exception {
		Timestamp time = null;
		time = Timestamp.valueOf(str);
		return time;
	}

	public static Date YmdToDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	public static String communityDateToString(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("MM/dd HH:mm:ss");
		String strDateTime = date == null ? null : formater.format(date);
		return strDateTime;
	}

	public static Date getMaxDateOfDay(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(11, calendar.getActualMaximum(11));
			calendar.set(12, calendar.getActualMaximum(12));
			calendar.set(13, calendar.getActualMaximum(13));
			calendar.set(14, calendar.getActualMaximum(14));
			return calendar.getTime();
		}
	}

	public static Date getMinDateOfDay(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(11, calendar.getActualMinimum(11));
			calendar.set(12, calendar.getActualMinimum(12));
			calendar.set(13, calendar.getActualMinimum(13));
			calendar.set(14, calendar.getActualMinimum(14));
			return calendar.getTime();
		}
	}

	public static Date getAfterDay(Date date, int afterDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, afterDays);
		return cal.getTime();
	}

	/**
	 * @category day
	 * @param date1
	 * @param date2
	 * @return
	 */

	public static int DateDiff(Date date1, Date date2) {
		int i = (int) ((date1.getTime() - date2.getTime()) / 3600 / 24 / 1000);
		return i;
	}

	/**
	 * @category min
	 * @param date1
	 * @param date2
	 * @return
	 */

	public static int MinDiff(Date date1, Date date2) {
		int i = (int) ((date1.getTime() - date2.getTime()) / 1000 / 60);
		return i;
	}

	// second

	public static int TimeDiff(Date date1, Date date2) {
		int i = (int) ((date1.getTime() - date2.getTime()));
		return i;
	}

	public static Date getCurrentDate() {
		// System.currentTimeMillis();
		return new Date();
	}

	public static java.sql.Date getCurrentSqlDate() {
		// System.currentTimeMillis();
		return new java.sql.Date(System.currentTimeMillis());
	}
	
	public static String formatDate(String date){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
		try {
			Date fdate = df.parse(date);
			return new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(fdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		// System.out.println(formatDate(parseDate("2010/3/6")));
		// System.out.println(getDate("yyyy年MM月dd日 E"));
		// long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		// System.out.println(time/(24*60*60*1000));
	}
}
