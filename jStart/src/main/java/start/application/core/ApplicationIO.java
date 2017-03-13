package start.application.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import start.application.core.config.ConstantConfig;
import start.application.core.constant.Constant;
import start.application.core.constant.DataTypeValidation;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StringHelper;
import start.application.core.utils.VerifyCheck;
import start.application.orm.annotation.Temporal;
import start.application.orm.annotation.verify.FormatType;
import start.application.orm.annotation.verify.VerifyValueEmpty;
import start.application.orm.annotation.verify.VerifyValueEnum;
import start.application.orm.annotation.verify.VerifyValueFormat;
import start.application.orm.annotation.verify.VerifyValueLength;
import start.application.orm.annotation.verify.VerifyValueRegex;
import start.application.orm.annotation.verify.VerifyValueTimeFormat;
import start.application.orm.exceptions.VerifyException;

/**
 * 数据的读写
 * 
 * @author Start
 *
 */
public final class ApplicationIO {

	/**
	 * 判断字段定义的数据类型容器是否支持
	 * 
	 * @param entityName
	 * @param field
	 */
	public static boolean isDataTypeSupport(Class<?> type) {
		String typeName = type.getName();
		if (type.isEnum()) {
			return true;
		} else if (type.isArray()) {
			if (DataTypeValidation.isSupportDataTypeArray.contains(typeName)) {
				// 数组类型只支持字符串数组
				return true;
			}
		} else if (DataTypeValidation.isSupportDataType.contains(typeName)) {
			return true;
		}
		return false;
	}

	/**
	 * 外部值转化到内部字段类型
	 */
	public static Object read(Field field, String value) {
		return read(field, null,field.getType(), value);
	}

	public static Object read(Field field, Method method, Class<?> type,String value) {
		if(!isDataTypeSupport(type)){
			return null;
		}
		isVerifyField(field, value);
		isVerifyMethod(method, value);
		String typeName = type.getName();
		if (StringHelper.isEmpty(value)) {
			if (DataTypeValidation.isString.contains(typeName)) {
				return "";
			} else {
				return null;
			}
		}
		if (DataTypeValidation.isDate.contains(typeName)) {
			String format = ConstantConfig.DATAFORMAT;
			if (field != null) {
				if (field.isAnnotationPresent(Temporal.class)) {
					Temporal temporal = field.getAnnotation(Temporal.class);
					format = temporal.format();
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.parse(value);
			} catch (ParseException e) {
				throw new ApplicationException(e);
			}
		} else if (type.isEnum()) {
			int index = Integer.parseInt(value);
			if (type.getEnumConstants().length <= index) {
				throw new IllegalArgumentException("枚举类型" + type + "下标数组越界:" + value);
			}
			return type.getEnumConstants()[index];
		} else if (type.isArray()) {
			// 字符串用逗号分隔转成数组
			String[] values = value.split(Constant.COMMA);
			String[] arrays = (String[]) Array.newInstance(String.class, values.length);
			for (int i = 0; i < values.length; i++) {
				Array.set(arrays, i, String.valueOf(values[i]));
			}
			return arrays;
		} else if (DataTypeValidation.isBoolean.contains(typeName)) {
			if ("1".equals(value) || "0".equals(value)) {
				return !"0".equals(value);
			} else {
				return Boolean.parseBoolean(value.toLowerCase());
			}
		} else if (DataTypeValidation.isShort.contains(typeName)) {
			return Short.parseShort(value);
		} else if (DataTypeValidation.isInteger.contains(typeName)) {
			return Integer.parseInt(value);
		} else if (DataTypeValidation.isLong.contains(typeName)) {
			return Long.parseLong(value);
		} else if (DataTypeValidation.isFloat.contains(typeName)) {
			return Float.parseFloat(value);
		} else if (DataTypeValidation.isDouble.contains(typeName)) {
			return Double.parseDouble(value);
		} else if (DataTypeValidation.isString.contains(typeName)) {
			return String.valueOf(value);
		}
		return value;
	}
	
	/**
	 * 验证字段类型
	 * 
	 * @param field
	 * @param value
	 */
	public static void isVerifyField(Field field, String value) {
		if (field == null) {
			return;
		}
		verify(field.getAnnotation(VerifyValueEmpty.class),value);
		verify(field.getAnnotation(VerifyValueLength.class),value);
		verify(field.getAnnotation(VerifyValueEnum.class),field.getType(),value);
		verify(field.getAnnotation(VerifyValueTimeFormat.class),value);
		verify(field.getAnnotation(VerifyValueFormat.class),value);
		verify(field.getAnnotation(VerifyValueRegex.class),value);
	}

	/**
	 * 验证set方法的字段类型
	 * @param method
	 * @param value
	 */
	public static void isVerifyMethod(Method method, String value) {
		if(method==null){
			return;
		}
		verify(method.getAnnotation(VerifyValueEmpty.class),value);
		verify(method.getAnnotation(VerifyValueLength.class),value);
		verify(method.getAnnotation(VerifyValueEnum.class),method.getParameterTypes()[0],value);
		verify(method.getAnnotation(VerifyValueTimeFormat.class),value);
		verify(method.getAnnotation(VerifyValueFormat.class),value);
		verify(method.getAnnotation(VerifyValueRegex.class),value);
	}
	
	/**
	 * 验证是否为空
	 * @param length
	 * @param value
	 */
	public static void verify(VerifyValueEmpty verify,String value){
		if (verify != null) {
			if (StringHelper.isEmpty(value)) {
				throw new VerifyException(verify.message());
			}
		}
	}
	
	/**
	 * 验证长度
	 * @param length
	 * @param value
	 */
	public static void verify(VerifyValueLength verify,String value){
		if (verify != null) {
			if (StringHelper.isEmpty(value)) {
				throw new VerifyException(verify.message());
			}
			if (value.length() >= verify.min() && value.length() <= verify.max()) {
				throw new VerifyException(verify.message());
			}
		}
	}
	
	/**
	 * 验证枚举类型
	 * @param enumv
	 * @param type
	 * @param value
	 */
	public static void verify(VerifyValueEnum verify,Class<?> type,String value){
		if (verify != null) {
			if (StringHelper.isEmpty(value)) {
				throw new VerifyException(verify.message());
			}
			if (type.isEnum()) {
				if(!VerifyCheck.isNumeric(value)){
					throw new VerifyException(verify.message());
				}
				if (type.getEnumConstants().length <= Integer.parseInt(value)) {
					throw new VerifyException(verify.message());
				}
			} else {
				throw new VerifyException(verify.message());
			}
		}
	}
	
	/**
	 * 验证时间日期格式
	 * @param timeFormat
	 * @param value
	 */
	public static void verify(VerifyValueTimeFormat verify,String value){
		if (verify != null) {
			if (StringHelper.isEmpty(value)) {
				throw new VerifyException(verify.message());
			}
			SimpleDateFormat sdf = new SimpleDateFormat(verify.format());
			try {
				sdf.parse(value);
			} catch (ParseException e) {
				throw new VerifyException(verify.message());
			}
		}
	}
	
	/**
	 * 验证数据格式
	 * @param format
	 * @param value
	 */
	public static void verify(VerifyValueFormat verify,String value){
		if (verify != null) {
			if (StringHelper.isEmpty(value)) {
				throw new VerifyException(verify.message());
			}
			if(verify.type()==FormatType.MOBILE){
				
			}else if(verify.type()==FormatType.MAIL){
				
			}else if(verify.type()==FormatType.IDCARD){
				
			}
		}
	}
	
	/**
	 * 验证正则表达式
	 * @param regex
	 * @param value
	 */
	public static void verify(VerifyValueRegex verify,String value){
		if (verify != null) {
			Pattern pattern = Pattern.compile(verify.regex());
			Matcher matcher = pattern.matcher(value);
			// 字符串是否与正则表达式相匹配
			if (!matcher.matches()) {
				throw new VerifyException(verify.message());
			}
		}
	}
	
}
