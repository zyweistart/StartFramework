package start.application.core.utils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import start.application.commons.codec.MD5;
import start.application.core.Constant;

public class StringHelper {

	public static boolean isEmpty(Object str){
		if(str==null||"".equals(str)){
			return true;
		}
		return false;
	}
	
	public static boolean isBlank(String str) {
    	int length = 0;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * 字符NULL转""
	 */
    public static String nullToStr(String str) {
    	if (str == null) {
        	str = "";
        }
        return str;
    }
    
    /**
     * 字符NULL转""并去掉左右空格
     */
    public static String nullToStrTrim(String str) {
    	if (str == null) {
        	str = "";
        }
        return str.trim();
    }
    
    public static String nullToUnknown(String str) {
        if (isEmpty(str)) {
        	str = Constant.UNKNOWN;
        }
        return str.trim();
    }
    
    /**
     * 0=false
     * 1=true
     */
    public static boolean nullToBoolean(String str) {
    	if(isEmpty(str)){
    		return false;
    	}
    	if("0".equals(str)){
    		return false;
    	}else if("1".equals(str)){
    		return true;
    	}
        return Boolean.valueOf(str.trim());
    }
    
    public static int nullToInt(String str) {
    	if(isEmpty(str)){
    		return 0;
    	}
        return Integer.valueOf(str.trim());
    }

    public static long nullToLong(String str) {
    	if(isEmpty(str)){
    		return 0;
    	}
        return Long.valueOf(str.trim());
    }
    
    public static double nullToDouble(String str) {
        if(isEmpty(str)){
    		return 0;
    	}
        return Double.valueOf(str.trim());
    }

    public static String listToString(List<String> lists){
    	return listToString(lists, Constant.COMMA);
    }
    
	public static String listToString(List<String> lists,String comma){
		StringBuilder strBuilder=new StringBuilder();
		for(String str : lists){
			strBuilder.append(str);
			strBuilder.append(comma);
		}
		if(strBuilder.length()>0){
			strBuilder.deleteCharAt(strBuilder.length()-1);
		}
		return strBuilder.toString();
	}
	
	public static String mapToString(Map<String,String> map,String comma){
		StringBuilder strBuilder=new StringBuilder();
		for(String key : map.keySet()){
			strBuilder.append(key+"="+map.get(key));
			strBuilder.append(comma);
		}
		if(strBuilder.length()>0){
			strBuilder.deleteCharAt(strBuilder.length()-1);
		}
		return strBuilder.toString();
	}

	/**
	 * 获取当前的UUID值
	 * 36位值：f6f457e6-cba4-49c8-88a8-1e5cde733907
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 生成随机数
	 */
	public static String random(){
		return random("");
	}
	
	/**
	 * 生成随机数
	 */
	public static String random(String input){
		Random ra=new Random();
		return MD5.md5(getUUID() + System.currentTimeMillis() + ra.nextInt(999999999) + input);
	}
	
}