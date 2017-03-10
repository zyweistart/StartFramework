package start.application.orm.context;

import java.util.HashMap;
import java.util.Map;

import start.application.core.constant.Message;
import start.application.orm.annotation.Entity;
import start.application.orm.entity.EntityInfo;

public class ContextCacheEntity {
	private static Map<String, EntityInfo> entitys = new HashMap<String, EntityInfo>();

	/**
	 * 注册实体类
	 * 
	 * @param entity
	 */
	public static void registerEntity(EntityInfo entity) {
		if (entity != null) {
			String name = entity.getEntityName();
			if (entitys.containsKey(name)) {
				String message = Message.getMessage(Message.PM_3000, name);
				throw new IllegalArgumentException(message);
			} else {
				entitys.put(name, entity);
			}
		}
	}

	/**
	 * 获取实体类
	 * 
	 * @param name
	 * @return
	 */
	public static EntityInfo getEntity(Class<?> prototype) {
		Entity entity = prototype.getAnnotation(Entity.class);
		if (entity != null) {
			return getEntity(entity.value());
		} else {
			throw new NullPointerException(Message.getMessage(Message.PM_1003, prototype.getName()));
		}
	}

	/**
	 * 获取实体类
	 * 
	 * @param name
	 * @return
	 */
	public static EntityInfo getEntity(String name) {
		if (name != null) {
			return entitys.get(name);
		} else {
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
	}

}
