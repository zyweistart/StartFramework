package start.application.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import start.application.core.Message;
import start.application.core.beans.BeanBuilder;
import start.application.core.beans.BeanBuilderFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.config.XmlTag;
import start.application.core.utils.ReflectUtils;
import start.application.orm.annotation.Entity;
import start.application.orm.entity.EntityInfo;
import start.application.web.interceptor.InterceptorHandler;

public class ContextObject {
	
	private final static Log log = LogFactory.getLog(ContextObject.class);
	
	private static Map<String,String> constants=new HashMap<String,String>();
	private static Map<String, String> beanPrototypes = new HashMap<String, String>();
	private static Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();
	private static Map<String, EntityInfo> entitys = new HashMap<String, EntityInfo>();
	private static List<String> interceptors=new ArrayList<String>();
	private static Map<String,List<XmlTag>> xmlTags=new HashMap<String,List<XmlTag>>();
	
	/**
	 * 注册全局常量值
	 * @param key
	 * @param value
	 */
	public static void registerConstant(String key,String value){
		if(key!=null&&value!=null){
			constants.put(key, value);
		}
	}
	
	public static void registerBean(BeanDefinition bean){
		registerBean(bean, false);
	}
	
	/**
	 * 注册Bean对象,不定义name不加入Bean容器
	 * @param bean
	 * @param analysis 是否分析当前类信息
	 */
	public static void registerBean(BeanDefinition bean,boolean analysis){
		if(bean!=null){
			if(bean.getName()!=null){
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
			if(analysis){
				if(ReflectUtils.isSuperClass(bean.getPrototype(), BeanBuilder.class)){
//					BeanBuilderFactory.init(bean.getPrototype());
					BeanBuilderFactory.registerContext(bean.getPrototype());
					log.info("自定义BeanBuilder类："+bean.getPrototypeString()+"，加载成功!");
				}
				
				if(ReflectUtils.isSuperClass(bean.getPrototype(),InterceptorHandler.class)){
					registerInterceptors(bean.getName());
					log.info("自定义Interceptor类："+bean.getPrototypeString()+"，加载成功!");
				}
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
	 * 注册自定义标签
	 * @param tagName
	 * @param values
	 */
	public static void registerCustom(String name,XmlTag xmlTag){
		List<XmlTag> tagValues=xmlTags.get(name);
		if(tagValues==null){
			tagValues=new ArrayList<XmlTag>();
		}
		tagValues.add(xmlTag);
		xmlTags.put(name, tagValues);
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
	 * 判断当前Bean对象是否存在
	 * @param name
	 * @return
	 */
	public static boolean isBeanExistence(String name){
		String prototypeString=beanPrototypes.get(name);
		if(prototypeString==null){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 获取Bean 
	 * @param name BeanName  
	 * @return
	 */
	public static BeanDefinition getBean(String name) {
		String prototypeString=beanPrototypes.get(name);
		if(prototypeString==null){
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
		BeanDefinition bean=beans.get(prototypeString);
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
	public static BeanDefinition getBeanInfo(String prototypeString) {
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
	
	/**
	 * 获取自定义标签数据
	 * @param tagName
	 * @return
	 */
	public static List<XmlTag> getCustom(String tagName){
		List<XmlTag>values=xmlTags.get(tagName);
		if(values==null){
			throw new NullPointerException("未定义"+tagName+"对应的数据");
		}
		return values;
	}
	
}
