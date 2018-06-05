package com.emt.shoppay.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 不能支付日期判断
 * 
* @ClassName: CalendarUtil 
* @Description: 判断当前日期是否能够支付
* @author huangdafei
* @date 2016年11月14日 上午9:32:13 
*
 */
public class CalendarUtil {
	
	/**
	 * 验证不能支付时间
	* @Title: checkNonPaymentDate 
	* @Description: 验证当前时间是否在一个月的最后一天的10点到次日凌晨2点
	* @param @param date
	* @param @param cal
	* @param @return  参数说明 
	* @return boolean    返回类型 
	* @throws
	 */
	public static boolean checkNonPaymentDate(Date date, Calendar cal){
		int day = cal.get(Calendar.DATE);
		int hours = date.getHours();
		
		Integer lastDay = getLastdayOfMonth(date);
		System.out.println("当月最后一天=" + lastDay);
		
		Boolean isStart = (day == lastDay) && (hours >= 22);
		Boolean isEnd = (day == 1) && (hours < 2);

		if(isStart || isEnd){
			return true;
		}
		return false;
	}

	/**
	 * 一月最后一天
	 * 
	 * @param date
	 * @return
	 */
	private static Integer getLastdayOfMonth(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 0);

		// 一个月最后一天
		calendar.add(Calendar.MONTH, 1); // 加一个月
		calendar.set(Calendar.DATE, 1); // 设置为该月第一天
		calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
		String lastDay = df.format(calendar.getTime());
		return Integer.valueOf(lastDay);
	}

	/**
	 * 次月第一天
	 * 
	 * @return
	 */
	private static String getFirstDayOfMonth(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 0);

		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = df.format(calendar.getTime());
		StringBuffer str = new StringBuffer().append(day_first);
		return str.toString();

	}
	
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		
		boolean bool = checkNonPaymentDate(date, cal);
		System.out.println("boolean == " + bool);
		
	}

}
