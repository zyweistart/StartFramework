package start.application.orm.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public final class EntityProperty{
	/**
	 * 字段
	 */
	private Field field;
	/**
	 * 当前的成员方法
	 */
	private Method get;
	/**
	 * 当前成员方法的Set方法
	 */
	private Method set;
	/**
	 * 对应的字段成员
	 */
	private String fieldName;
	/**
	 * 返回类型
	 */
	private String returnTypeName;

	public Field getField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}

	public Method getGet() {
		return get;
	}

	public void setGet(Method get) {
		this.get = get;
	}

	public Method getSet() {
		return set;
	}

	public void setSet(Method set) {
		this.set = set;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName.toUpperCase();
	}

	public String getReturnTypeName() {
		return returnTypeName;
	}

	public void setReturnTypeName(String returnTypeName) {
		this.returnTypeName = returnTypeName;
	}
	
}
