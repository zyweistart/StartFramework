package start.application.orm;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ContextDataReadWrite;
import start.application.context.DataTypeValidation;
import start.application.context.annotation.Entity;
import start.application.context.exceptions.AnnoationError;
import start.application.context.exceptions.EntityDefinitionError;
import start.application.core.Message;
import start.application.core.utils.StackTraceInfo;
import start.application.orm.annotation.Column;
import start.application.orm.annotation.GeneratedValue;
import start.application.orm.annotation.Id;
import start.application.orm.annotation.Lob;
import start.application.orm.annotation.MappedSuperclass;
import start.application.orm.annotation.Table;
import start.application.orm.annotation.Temporal;
import start.application.orm.annotation.Transient;
import start.application.orm.entity.EntityInfo;
import start.application.orm.entity.EntityProperty;

public class AnnotationConfigEntityContext {
	
	private final static Logger log=LoggerFactory.getLogger(AnnotationConfigEntityContext.class);
	
	public static EntityInfo buildEntity(Class<?> prototype){
		Entity entity = prototype.getAnnotation(Entity.class);
		if (entity != null) {
			return buildEntity(entity.value(),prototype);
		}
		return null;
	}
	
	private static EntityInfo buildEntity(String entityName,Class<?> prototype){
		Class<?> clasz=prototype;
		EntityInfo entityInfo = new EntityInfo();
		// 实体名称
		entityInfo.setEntityName(entityName);
		// 是否存在@Table表名注解如果不存在则默认表名使用实体类的名称
		Table table = clasz.getAnnotation(Table.class);
		if (table != null) {
			// 表名
			entityInfo.setTableName(table.value());
		} else {
			// 如果为注解@Table则表名默认为实体名
			entityInfo.setTableName(entityInfo.getEntityName());
		}
		while (true) {
			if (clasz == null) {
				break;
			}
			if (!clasz.isAnnotationPresent(Entity.class)&&
					!clasz.isAnnotationPresent(MappedSuperclass.class)) {
				break;
			}
			// 只获取当前类中定义的字段
			Field[] fields = clasz.getDeclaredFields();
			for (Field field : fields) {
				// 只对1:public、2:private、4:protected修饰的成员有效果
				if (field.getModifiers() != 1 && field.getModifiers() != 2
						&& field.getModifiers() != 4) {
					continue;
				}
				if(!ContextDataReadWrite.isDataTypeSupport(field)){
					String message=Message.getMessage(Message.PM_5019,entityInfo.getEntityName(),
							field.getName(), field.getType().getName());
					throw new AnnoationError(message);
				}
				EntityProperty propertyMember = new EntityProperty();
				propertyMember.setField(field);
				// 在数据表中对应的字段列的名称
				propertyMember.setFieldName(field.getName());
				// 字段的返回类型的简单名称例：java.lang.String返回String
				propertyMember.setReturnTypeName(field.getType().getName());
				PropertyDescriptor pd = null;
				try {
					// 根据JavaBean的字段名称获取实体对象中get,set方法
					pd = new PropertyDescriptor(field.getName(), clasz);
					// 当前字段的get方法
					propertyMember.setGet(pd.getReadMethod());
					// 当前有字段的set方法
					propertyMember.setSet(pd.getWriteMethod());
				} catch (IntrospectionException e) {
					log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
					throw new EntityDefinitionError(e);
				}
				// 如果该字段不映射则跳出
				Transient trans = field.getAnnotation(Transient.class);
				if (trans != null) {
					entityInfo.getTransientPropertyMembers().add(propertyMember);
					continue;
				}
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					if (id.value() == GeneratedValue.UID) {
						if (!DataTypeValidation.isString
								.contains(propertyMember.getReturnTypeName())) {
							String message=Message.getMessage(Message.PM_3016,
									entityName, field.getName());
							throw new AnnoationError(message);
						}
					}
					if (entityInfo.getPrimaryKeyMember() == null) {
						if (!id.name().isEmpty()) {
							propertyMember.setFieldName(id.name());
						}
						entityInfo.setPrimaryKeyMember(propertyMember);
					} else {
						String message=Message.getMessage(Message.PM_3002,
								entityInfo.getEntityName());
						throw new AnnoationError(message);
					}
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					if (DataTypeValidation.isDate
							.contains(propertyMember.getReturnTypeName())) {
						String message=Message.getMessage(Message.PM_3012,
								entityName, field.getName());
						throw new AnnoationError(message);
					}
					if (!column.name().isEmpty()) {
						propertyMember.setFieldName(column.name());
					}
					entityInfo.getPropertyMembers().add(propertyMember);
					continue;
				}
				Temporal temporal = field.getAnnotation(Temporal.class);
				if (temporal != null) {
					if (!DataTypeValidation.isString
							.contains(propertyMember.getReturnTypeName())
							&& !DataTypeValidation.isDate
									.contains(propertyMember.getReturnTypeName())) {
						String message=Message.getMessage(Message.PM_3013,
								entityName, field.getName());
						throw new AnnoationError(message);
					}
					if (!temporal.name().isEmpty()) {
						propertyMember.setFieldName(temporal.name());
					}
					entityInfo.getPropertyMembers().add(propertyMember);
					continue;
				} else {
					if (DataTypeValidation.isDate
							.contains(propertyMember.getReturnTypeName())) {
						String message=Message.getMessage(Message.PM_3011,
								entityName, field.getName());
						throw new AnnoationError(message);
					}
				}
				Lob lob = field.getAnnotation(Lob.class);
				if (lob != null) {
					if (!DataTypeValidation.isString
							.contains(propertyMember.getReturnTypeName())) {
						String message=Message.getMessage(Message.PM_3014,
								entityName, field.getName());
						throw new AnnoationError(message);
					}
					if (!lob.name().isEmpty()) {
						propertyMember.setFieldName(lob.name());
					}
				}
				entityInfo.getPropertyMembers().add(propertyMember);
			}
			// 获取超类
			clasz = clasz.getSuperclass();
		}
		// 实体主键为必须
		if (entityInfo.getPrimaryKeyMember() == null) {
			String message=Message.getMessage(Message.PM_3001, entityInfo.getEntityName());
			throw new AnnoationError(message);
		}
		return entityInfo;
	}
	
}
