package start.application.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.annotation.Entity;
import start.application.context.exceptions.AnnoationError;
import start.application.core.Message;
import start.application.core.beans.BeanInfo;
import start.application.core.utils.StackTraceInfo;
import start.application.orm.entity.EntityInfo;

public class ContextObject {
	
	private final static Logger log=LoggerFactory.getLogger(ContextObject.class);
	
	private static Map<String,String> constants=new HashMap<String,String>();
	private static List<String> interceptors=new ArrayList<String>();
	private static Map<String, BeanInfo> beans = new HashMap<String, BeanInfo>();
	private static Map<String, EntityInfo> entitys = new HashMap<String, EntityInfo>();
	private static Map<String, Object> singletonBeans = new ConcurrentHashMap<String, Object>();
	
	/**
	 * 常量配置
	 */
	public static Map<String, String> getConstants() {
		return constants;
	}
	
	/**
	 * 拦截器列表
	 */
	public static List<String> getInterceptors() {
		return interceptors;
	}
	
	/**
	 * 容器Bean对象
	 */
	public static BeanInfo getBeans(String name) {
		return beans.get(name);
	}
	
	public static void putBeans(BeanInfo bean){
		if(bean!=null){
			String name=bean.getName();
			if (beans.containsKey(name)) {
				String message=Message.getMessage(Message.PM_3000, name);
				log.error(StackTraceInfo.getTraceInfo() + message);
				throw new AnnoationError(message);
			}else{
				beans.put(name,bean);
			}
		}
	}
	
	public static EntityInfo getEntitys(String name) {
		return entitys.get(name);
	}
	
	public static void putEntitys(EntityInfo entity){
		if(entity!=null){
			String name=entity.getEntityName();
			if(entitys.containsKey(name)){
				String message=Message.getMessage(Message.PM_3000, name);
				log.error(StackTraceInfo.getTraceInfo() + message);
				throw new AnnoationError(message);
			}else{
				entitys.put(name, entity);
			}
		}
	}

	/**
	 * 单例Bean对象
	 * @return
	 */
	public static Map<String, Object> getSingletonBeans() {
		return singletonBeans;
	}

	/**
	 * 实体类标有Entity的注解
	 */
	public static EntityInfo getEntitys(Class<?> prototype) {
		Entity entity = prototype.getAnnotation(Entity.class);
		if (entity != null) {
			return getEntitys(entity.value());
		}else{
			throw new AnnoationError(Message.getMessage(Message.PM_1003, prototype.getName()));
		}
	}
	
}
