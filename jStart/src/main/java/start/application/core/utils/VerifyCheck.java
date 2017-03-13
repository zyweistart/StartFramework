package start.application.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VerifyCheck {
	
	public static boolean regex(String str, String regex) {
		return str.matches(regex);
	}
	/**
	 * 检测是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否为数字
	 */
	public static boolean isNumber(String number) {
		number = StringHelper.nullToStrTrim(number);
		if (StringHelper.isBlank(number) || number.split(".").length > 2 || !number.replace(".", "").matches("[0-9]+")) {
			return false;
		}
		try {
			Double.valueOf(number);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 电话号码
	 */
	public static boolean isPhone(String phone) {
		phone = StringHelper.nullToStrTrim(phone);
		if (phone.length() != 11 && phone.length() != 12) {
			return false;
		}
		String regex = "[0-9]+";
		return phone.matches(regex);
	}
	
	/**
	 * 检测移动手机号码
	 */
	public static boolean isMobile(String mobile){
		mobile = StringHelper.nullToStrTrim(mobile);
		if (mobile.length() != 11) {
			return false;
		}
		String regex = "^(1[3,5,8][0-9])\\d{8}$";
		return mobile.matches(regex);
	}
	
	/**
	 * 检测身份证号
	 */
	public static boolean isIDCard(String str){
		return true;
	}
	
	/**
	 * 检测电子邮件
	 */
	public static boolean isMail(String email){
		if (StringHelper.isEmpty(email)) {
			return false;
		}
		email = StringHelper.nullToStrTrim(email);
		if (email.length() < 5) {
			return false;
		}
		String regex = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
		return email.matches(regex);
	}
	
	/**
	 * 检测IP地址
	 */
	public static boolean isIPAddress(String ip){
		if (StringHelper.isEmpty(ip) || StringHelper.isBlank(ip)) {
			return false;
		}
		if (ip.length() < 7) {
			return false;
		}
		if (!regex(ip, "[0-9.]+")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 检测MAC地址
	 */
	public static boolean isMacAddress(String mac){
		if (StringHelper.isEmpty(mac) || StringHelper.isBlank(mac)) {
			return false;
		}
		if (mac.length() != 17) {
			return false;
		}
		return regex(mac,
				"[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}");
	}
	
	/**
	 * 检测MD5
	 */
	public static boolean isMD5(String md5){
		if(StringHelper.isEmpty(md5)){
			return false;
		}
		if (md5.length() != 32) {
			return false;
		}
		return regex(md5, "[0-9A-Fa-f]+");
	}
	
	/**
	 * 判断是否为中文
	 */
	public static boolean isChinese(String str) {
		boolean isChinese = false;
		if (!StringHelper.isEmpty(str)) {
			if (!str.substring(0, 1).matches("[\\u4e00-\\u9fbb]+")) {
				return isChinese;
			} else {
				return true;
			}
		}
		return isChinese;
	}
	
	/**
	 * 金额
	 */
	public static boolean isMoney(String money) {
		money = StringHelper.nullToStrTrim(money);
		if (money.length() > 20) {
			return false;
		}
		if (StringHelper.isBlank(money) || money.split(".").length > 2 || !money.replace(".", "").matches("[0-9]+")) {
			return false;
		}
		if (money.indexOf(".") > 0) {
			String decimal = money.substring(money.indexOf(".") + 1);
			if (decimal.length() >= 3) {
				if (decimal.substring(2).replaceAll("0", "").length() > 0) {
					return false;
				}
			}
		}
		double moneyD = 0.0D;
		try {
			moneyD = Double.valueOf(money);
		} catch (NumberFormatException e) {
			return false;
		}
		if (moneyD != 0.0D && (moneyD < 0.01D || moneyD > 1000000000.00D)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 检测时间格式
	 */
	public static boolean checkTime(String time,String format) {
		if (time.length() != 14) {
			return false;
		}
		if (!regex(time, "[2][0-9]+")) {
			return false;
		}
		DateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
		try {
			Date date = (Date) formatter.parse(time);
			return time.equals(formatter.format(date));
		} catch (ParseException e) {
			return false;
		}
	}
	
}
