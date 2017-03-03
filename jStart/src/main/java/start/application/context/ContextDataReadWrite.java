package start.application.context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import start.application.context.config.ConstantConfig;
import start.application.core.Constant;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StringHelper;
import start.application.orm.annotation.Temporal;

/**
 * 数据的读写
 * @author Start
 *
 */
public final class ContextDataReadWrite {

	/**
	 * 判断字段定义的数据类型容器是否支持
	 * @param entityName
	 * @param field
	 */
	public static boolean isDataTypeSupport(Field field){
		String typeName=field.getType().getName();
		if(field.getType().isEnum()){
			return true;
		}else if(field.getType().isArray()){
			if(DataTypeValidation.isSupportDataTypeArray.contains(typeName)){
				//数组类型只支持字符串数组
				return true;
			}
		}else if(DataTypeValidation.isSupportDataType.contains(typeName)){
			return true;
		}
		return false;
	}
	
	/**
	 * 外部值转化到内部字段类型
	 */
	public static Object convertReadIn(Field field,String value){
		return convertReadIn(field, field.getType(),value);
	}
	
	public static Object convertReadIn(Field field,Class<?> type,String value){
		String typeName=type.getName();
		if(StringHelper.isEmpty(value)){
			if(DataTypeValidation.isString.contains(typeName)){
				return "";
			}else{
				return null;
			}
		}
		if(DataTypeValidation.isDate.contains(typeName)){
			String format=ConstantConfig.DATAFORMAT;
			if(field!=null){
				if(field.isAnnotationPresent(Temporal.class)){
					Temporal temporal=field.getAnnotation(Temporal.class);
					format=temporal.format();
				}
			}
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			try {
				return sdf.parse(value);
			} catch (ParseException e) {
				throw new ApplicationException(e);
			}
		}else if(type.isEnum()){
			int index=Integer.parseInt(value);
			if(type.getEnumConstants().length<=index){
				throw new IllegalArgumentException("枚举类型"+type+"下标数组越界:"+value);
			}
			return type.getEnumConstants()[index];
		}else if(type.isArray()){
			//字符串用逗号分隔转成数组
			String[] values=value.split(Constant.COMMA);
			String[] arrays=(String[])Array.newInstance(String.class, values.length);
			for(int i=0;i<values.length;i++){
				Array.set(arrays, i, String.valueOf(values[i]));
			}
			return arrays;
		}else if(DataTypeValidation.isBoolean.contains(typeName)){
			if("1".equals(value)||"0".equals(value)){
				return !"0".equals(value);
			}else{
				return Boolean.parseBoolean(value.toLowerCase());
			}
		}else if(DataTypeValidation.isShort.contains(typeName)){
			return Short.parseShort(value);
		}else if(DataTypeValidation.isInteger.contains(typeName)){
			return Integer.parseInt(value);
		}else if(DataTypeValidation.isLong.contains(typeName)){
			return Long.parseLong(value);
		}else if(DataTypeValidation.isFloat.contains(typeName)){
			return Float.parseFloat(value);
		}else if(DataTypeValidation.isDouble.contains(typeName)){
			return Double.parseDouble(value);
		}else if(DataTypeValidation.isString.contains(typeName)){
			return String.valueOf(value);
		}
		return value;
	}
	
	/**
	 * 内部字段类型转化输出到外部
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertWriteOut(Field field,Object value){
		if(StringHelper.isEmpty(value)){
			return "";
		}
		String tarValue=String.valueOf(value);
		String typeName=field.getType().getName();
		if(DataTypeValidation.isDate.contains(typeName)){
			String format=ConstantConfig.DATAFORMAT;
			if(field.isAnnotationPresent(Temporal.class)){
				Temporal temporal=field.getAnnotation(Temporal.class);
				format=temporal.format();
			}
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			return sdf.format(value);
		}else if(field.getType().isEnum()){
			//字符找索引
			return ((Enum)value).ordinal();
		}else if(field.getType().isArray()){
			//数组转成字符串用逗号分隔
			List<String> lists=new ArrayList<String>();
			for(Object o : (Object[])value){
				lists.add(String.valueOf(o));
			}
			return StringHelper.listToString(lists);
		}else if(DataTypeValidation.isBoolean.contains(typeName)){
			return Boolean.parseBoolean(tarValue)?Constant.TRUE:Constant.FALSE;
		}else if(DataTypeValidation.isShort.contains(typeName)||
				DataTypeValidation.isInteger.contains(typeName)||
				DataTypeValidation.isLong.contains(typeName)||
				DataTypeValidation.isFloat.contains(typeName)||
				DataTypeValidation.isDouble.contains(typeName)||
				DataTypeValidation.isString.contains(typeName)){
			return tarValue;
		}
		return tarValue;
	}
	
}
