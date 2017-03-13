package start.application.orm.support;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import start.application.core.config.ConstantConfig;
import start.application.core.constant.DataTypeValidation;
import start.application.core.utils.StringHelper;
import start.application.orm.annotation.Temporal;

public abstract class AbstractEntityManager {
	
	public abstract void persist(Object entity);
	public abstract long merge(Object entity);
	public abstract long remove(Object entity);
	public abstract <T> T load(Class<T> prototype, Serializable primaryKeyValue);
	
	/**
	 * 内部字段类型转化输出到外部
	 */
	@SuppressWarnings("rawtypes")
	public Object write(Field field, Object value) {
		if (StringHelper.isEmpty(value)) {
			return "";
		}
		String tarValue = String.valueOf(value);
		String typeName = field.getType().getName();
		if (DataTypeValidation.isDate.contains(typeName)) {
			String format = ConstantConfig.DATAFORMAT;
			if (field.isAnnotationPresent(Temporal.class)) {
				Temporal temporal = field.getAnnotation(Temporal.class);
				format = temporal.format();
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(value);
		} else if (field.getType().isEnum()) {
			// 字符找索引
			return ((Enum) value).ordinal();
		} else if (field.getType().isArray()) {
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
	
}
