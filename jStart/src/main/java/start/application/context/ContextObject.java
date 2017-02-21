package start.application.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.context.annotation.Entity;
import start.application.core.Message;
import start.application.core.beans.BeanInfo;
import start.application.orm.entity.EntityInfo;

public class ContextObject {
	
	private static Map<String,String> constants=new HashMap<String,String>();
	private static Map<String, String> beanPrototypes = new HashMap<String, String>();
	private static Map<String, BeanInfo> beans = new HashMap<String, BeanInfo>();
	private static Map<String, EntityInfo> entitys = new HashMap<String, EntityInfo>();
	private static List<String> interceptors=new ArrayList<String>();
	
	/**
	 * 注册全局常量值
	 * @param key
	 * @param value
	 */
	public static void registerConstant(String key,String value){
		if(key!=null&&value!=null){
			if(constants.containsKey(key)){
				throw new IllegalArgumentException("常量："+key+" 已存在，参数异常!");
			}else{
				constants.put(key, value);
			}
		}
	}
	
	/**
	 * 注册Bean对象 
	 * @param bean
	 */
	public static void registerBean(BeanInfo bean){
		if(bean!=null){
			if (beanPrototypes.containsKey(bean.getName())) {
				String message=Message.getMessage(Message.PM_3000, bean.getName());
				throw new IllegalArgumentException(message);
			}else{
				beanPrototypes.put(bean.getName(), bean.getPrototypeString());
			}
			if (beans.containsKey(bean.getPrototypeString())) {
				String message=Message.getMessage(Message.PM_3000, bean.getPrototypeString());
				throw new IllegalArgumentException(message);
			}else{
				beans.put(bean.getPrototypeString(), bean);
			}
		}
	}
	
	/**
	 * 拦截器列表
	 */
	public static void registerInterceptors(String name) {
		if(name!=null){
			if(getBean(name)==null){
				throw new IllegalArgumentException("拦截器:"+name+"未注册为Bean对象");
			}
			interceptors.add(name);
		}
	}
	
	/**
	 * 注册实体类
	 * @param entity
	 */
	public static void registerEntity(EntityInfo entity){
		if(entity!=null){
			String name=entity.getEntityName();
			if(entitys.containsKey(name)){
				String message=Message.getMessage(Message.PM_3000, name);
				throw new IllegalArgumentException(message);
			}else{
				entitys.put(name, entity);
			}
		}
	}
	
	/**
	 * 获取常量
	 */
	public static String getConstant(String key) {
		String value=constants.get(key);
		if(value==null){
			throw new NullPointerException("不存在:"+key+"对应的常量值");
		}
		return value;
	}

	/**
	 * 获取Bean 
	 * @param name BeanName  
	 * @return
	 */
	public static BeanInfo getBean(String name) {
		String prototypeString=beanPrototypes.get(name);
		if(prototypeString==null){
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
		BeanInfo bean=beans.get(prototypeString);
		if(bean==null){
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
		return bean;
	}
	
	/**
	 * 获取Bean
	 * @param prototypeString 类名
	 * @return
	 */
	public static BeanInfo getBeanInfo(String prototypeString) {
		return beans.get(prototypeString);
	}
	
	/**
	 * 获取拦截器对象列表
	 */
	public static List<String> getInterceptors() {
		return interceptors;
	}
	
	/**
	 * 获取实体类
	 * @param name
	 * @return
	 */
	public static EntityInfo getEntity(Class<?> prototype) {
		Entity entity = prototype.getAnnotation(Entity.class);
		if (entity != null) {
			return getEntity(entity.value());
		}else{
			throw new NullPointerException(Message.getMessage(Message.PM_1003, prototype.getName()));
		}
	}
	
	/**
	 * 获取实体类
	 * @param name
	 * @return
	 */
	public static EntityInfo getEntity(String name) {
		if (name != null) {
			return entitys.get(name);
		}else{
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
	}
	
}
