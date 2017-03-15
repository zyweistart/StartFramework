package start.application.core.io;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.core.config.ConstantConfig;
import start.application.core.constant.Constant;
import start.application.core.constant.DataTypeValidation;
import start.application.core.exceptions.ApplicationException;
import start.application.core.io.verify.VerifyValueCustom;
import start.application.core.io.verify.annotation.VerifyCheckCustom;
import start.application.core.io.verify.annotation.VerifyValueEmpty;
import start.application.core.io.verify.annotation.VerifyValueEnum;
import start.application.core.io.verify.annotation.VerifyValueFormat;
import start.application.core.io.verify.annotation.VerifyValueFormat.FormatType;
import start.application.core.io.verify.annotation.VerifyValueLength;
import start.application.core.io.verify.annotation.VerifyValueNotNull;
import start.application.core.io.verify.annotation.VerifyValueRegex;
import start.application.core.io.verify.annotation.VerifyValueTimeFormat;
import start.application.core.utils.StringHelper;
import start.application.core.utils.VerifyCheck;
import start.application.orm.annotation.Temporal;
import start.application.orm.exceptions.VerifyException;

/**
 * 数据的读写
 * 
 * @author Start
 *
 */
public final class ApplicationIO {
	
	private static PackingValueImpl mPackingValueImpl= new PackingValueImpl();
	
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
	
	public static void iocObjectParameter(Object instance,Map<String,String> params){
		iocObjectParameter(instance, params,mPackingValueImpl);
	}
	
	public static void iocObjectParameter(Object instance,Map<String,String> params,PackingValueImpl impl){
		Map<String,String> sParams=new HashMap<String,String>();
		Map<String,Map<String,String>> cParams=new HashMap<String,Map<String,String>>();
		for(String key:params.keySet()){
			String value=params.get(key);
			int index=key.indexOf(46);
			if(index>0){
				String newParamName=key.substring(0,index);
				Map<String,String> newParams=cParams.get(newParamName);
				if(newParams==null){
					newParams=new HashMap<String,String>();
				}
				newParams.put(key.substring(index+1),value);
				cParams.put(newParamName, newParams);
			}else{
				sParams.put(key,value);
			}
		}
		
		Class<?> prototype=instance.getClass();
		while (true) {
			if (prototype.equals(Object.class)||prototype == null) {
				break;
			}
			for(Method method:prototype.getDeclaredMethods()){
				String methodName=method.getName();
				if(methodName.startsWith("set")){
					if(method.getParameterTypes().length!=1){
						continue;
					}
					Class<?> type=method.getParameterTypes()[0];
					String name=methodName.substring(3,4).toLowerCase()+methodName.substring(4);
					if(sParams.containsKey(name)){
						Field field=null;
						try {
							field=prototype.getDeclaredField(name);
						} catch (NoSuchFieldException | SecurityException e) {
						}
						Object value=impl.getValue(field,method,type,sParams.get(name));
						if(value==null){
							continue;
						}
						try {
							method.invoke(instance, value);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new ApplicationException(e);
						}
					}else if(cParams.containsKey(name)){
						Object childObj=impl.newInstance(type, name);
						iocObjectParameter(childObj,cParams.get(name));
						try {
							method.invoke(instance, childObj);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new ApplicationException(e);
						}
					}else{
						//验证当前字段是否不能为空
						Object value=sParams.get(name);
						if(value==null){
							try {
								Field field=prototype.getDeclaredField(name);
								ApplicationIO.verify(field.getAnnotation(VerifyValueNotNull.class),null);
							} catch (NoSuchFieldException | SecurityException e) {
							}
							ApplicationIO.verify(method.getAnnotation(VerifyValueNotNull.class),null);
						}
					}
				}
			}
			prototype = prototype.getSuperclass();
		}
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
		if(value==null){
			return null;
		}
		isVerifyField(field, value);
		isVerifyMethod(method, value);
		String typeName = type.getName();
//		if (StringHelper.isEmpty(value)) {
//			if (DataTypeValidation.isString.contains(typeName)) {
//				return "";
//			} else {
//				return null;
//			}
//		}
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
	
	public static Object write(Field field, Object value) {
		return write(field,field.getType(),value);
	}
	
	/**
	 * 内部字段类型转化输出到外部
	 */
	@SuppressWarnings("rawtypes")
	public static Object write(Field field,Class<?> type, Object value) {
		if(!isDataTypeSupport(type)){
			return null;
		}
		if (StringHelper.isEmpty(value)) {
			return "";
		}
		String tarValue = String.valueOf(value);
		String typeName = type.getName();
		if (DataTypeValidation.isDate.contains(typeName)) {
			String format = ConstantConfig.DATAFORMAT;
			if(field!=null){
				if (field.isAnnotationPresent(Temporal.class)) {
					Temporal temporal = field.getAnnotation(Temporal.class);
					format = temporal.format();
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(value);
		} else if (type.isEnum()) {
			// 字符找索引
			return ((Enum) value).ordinal();
		} else if (type.isArray()) {
			// 数组转成字符串用逗号分隔
			List<String> lists = new ArrayList<String>();
			for (Object o : (Object[]) value) {
				lists.add(String.valueOf(o));
			}
			return StringHelper.listToString(lists);
		} else if (DataTypeValidation.isBoolean.contains(typeName)) {
			return Boolean.parseBoolean(tarValue) ? 1 : 0;
		} else if (DataTypeValidation.isShort.contains(typeName) || DataTypeValidation.isInteger.contains(typeName)
				|| DataTypeValidation.isLong.contains(typeName) || DataTypeValidation.isFloat.contains(typeName)
				|| DataTypeValidation.isDouble.contains(typeName) || DataTypeValidation.isString.contains(typeName)) {
			return tarValue;
		}
		return tarValue;
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
		verify(field.getAnnotation(VerifyValueNotNull.class),value);
		verify(field.getAnnotation(VerifyValueEmpty.class),value);
		verify(field.getAnnotation(VerifyValueLength.class),value);
		verify(field.getAnnotation(VerifyValueEnum.class),field.getType(),value);
		verify(field.getAnnotation(VerifyValueTimeFormat.class),value);
		verify(field.getAnnotation(VerifyValueFormat.class),value);
		verify(field.getAnnotation(VerifyValueRegex.class),value);
		verify(field.getAnnotation(VerifyValueCustom.class),value);
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
		verify(method.getAnnotation(VerifyValueNotNull.class),value);
		verify(method.getAnnotation(VerifyValueEmpty.class),value);
		verify(method.getAnnotation(VerifyValueLength.class),value);
		verify(method.getAnnotation(VerifyValueEnum.class),method.getParameterTypes()[0],value);
		verify(method.getAnnotation(VerifyValueTimeFormat.class),value);
		verify(method.getAnnotation(VerifyValueFormat.class),value);
		verify(method.getAnnotation(VerifyValueRegex.class),value);
		verify(method.getAnnotation(VerifyValueCustom.class),value);
	}
	
	/**
	 * 验证是否为null
	 * @param length
	 * @param value
	 */
	public static void verify(VerifyValueNotNull verify,String value){
		if (verify != null) {
			if (value==null) {
				throw new VerifyException(verify.message());
			}
		}
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
			if (!(value.length() >= verify.min() && value.length() <= verify.max())) {
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
			if(!VerifyCheck.checkTime(value, verify.format())){
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
				if(!VerifyCheck.isMobile(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.MAIL){
				if(!VerifyCheck.isMail(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.PHONE){
				if(!VerifyCheck.isPhone(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.MD5){
				if(!VerifyCheck.isMD5(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.IP){
				if(!VerifyCheck.isIPAddress(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.MAC){
				if(!VerifyCheck.isMacAddress(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.CHINESE){
				if(!VerifyCheck.isChinese(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.NUMBER){
				if(!VerifyCheck.isNumber(value)){
					throw new VerifyException(verify.message());
				}
			}else if(verify.type()==FormatType.MONEY){
				if(!VerifyCheck.isMoney(value)){
					throw new VerifyException(verify.message());
				}
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
			if(!VerifyCheck.regex(value, verify.regex())){
				throw new VerifyException(verify.message());
			}
		}
	}
	
	/**
	 * 自定义校验
	 * @param regex
	 * @param value
	 */
	public static void verify(VerifyValueCustom verify,String value){
		if (verify != null) {
			VerifyCheckCustom check=null;
			try {
				check=(VerifyCheckCustom)verify.check().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new ApplicationException(e);
			}
			if(!check.check(value)){
				throw new VerifyException(verify.message());
			}
		}
	}
	
}
