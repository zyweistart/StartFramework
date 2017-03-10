package start.application.core;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ApplicationIO;
import start.application.core.annotation.Constant;
import start.application.core.annotation.Qualifier;
import start.application.core.annotation.Resource;
import start.application.core.beans.BeanDefinition;
import start.application.core.beans.ContextAdvice;
import start.application.core.beans.factory.ApplicationContext;
import start.application.core.beans.factory.ApplicationContextAware;
import start.application.core.beans.factory.BeanDefinitionAware;
import start.application.core.beans.factory.BeforeBean;
import start.application.core.beans.factory.DisposableBean;
import start.application.core.beans.factory.InitializingBean;
import start.application.core.config.ConstantConfig;
import start.application.core.constant.Message;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;

public class GenerateBeanManager implements ApplicationContext,Closeable {
	
	private final static Logger log=LoggerFactory.getLogger(GenerateBeanManager.class);
	
	private Map<String, String> beanDefinitionDictionaries = new HashMap<String, String>();
	private Map<String, BeanDefinition> beanDefinitions = new HashMap<String, BeanDefinition>();
	private ConcurrentMap<String,Object> cacheContext=new ConcurrentHashMap<String,Object>();

	public Object getCacheContext(String name) {
		return cacheContext.get(name);
	}
	
	public void putCacheContext(String name,Object beanObj) {
		cacheContext.putIfAbsent(name, beanObj);
	}
	
	@Override
	public void registerBeanDoManagerCenter(BeanDefinition bean){
		if (beanDefinitionDictionaries.containsKey(bean.getName())) {
			String message=Message.getMessage(Message.PM_3000, bean.getName());
			throw new IllegalArgumentException(message);
		}else{
			beanDefinitionDictionaries.put(bean.getName(), bean.getPrototypeString());
		}
		if (beanDefinitions.containsKey(bean.getPrototypeString())) {
			String message=Message.getMessage(Message.PM_3000, bean.getPrototypeString());
			throw new IllegalArgumentException(message);
		}else{
			beanDefinitions.put(bean.getPrototypeString(), bean);
		}
		if(ReflectUtils.isSuperClass(bean.getPrototype(), ContextAdvice.class)){
			//直接创建组件
			getBean(bean);
			log.info("自定义ContextAdvice容器生成对象："+bean.getName()+"，加载成功~~~");
		}
	}
	
	@Override
	public BeanDefinition getBeanDefinitionInfoByName(String name) {
		String prototypeString=beanDefinitionDictionaries.get(name);
		if(prototypeString==null){
			throw new NullPointerException(Message.getMessage(Message.PM_1003, name));
		}
		return getBeanDefinitionInfoByClass(prototypeString);
	}
	
	@Override
	public BeanDefinition getBeanDefinitionInfoByClass(String prototypeString) {
		if(!beanDefinitions.containsKey(prototypeString)){
			throw new NullPointerException(Message.getMessage(Message.PM_1003, prototypeString));
		}
		return beanDefinitions.get(prototypeString);
	}

	@Override
	public Object getBean(String name){
		return getBean(getBeanDefinitionInfoByName(name));
	}

	@Override
	public Object getBean(Class<?> prototype){
		return getBean(getBeanDefinitionInfoByClass(prototype.getName()));
	}
	
	private Object getBean(BeanDefinition bean){
		if(bean.getBeanContextName()!=null){
			//当前对象是否需要使用其它容器来创建
			ContextAdvice context=(ContextAdvice)getBean(bean.getBeanContextName());
			return context.newBean(bean);
		}
		//从缓存中直接获取已创建的对象
		Object instance=getCacheContext(bean.getName());
		//常量值是否更新
		boolean isNewObject=true;
		if(instance!=null){
			//如果已存在实例则常量值不重新赋值
			isNewObject=false;
		}
		if(instance==null){
			//构造函数注入
			for (Constructor<?> constructor : bean.getPrototype().getConstructors()) {
				List<Object> paramValues=new ArrayList<Object>();
				for(Parameter param:constructor.getParameters()){
					if(param.isAnnotationPresent(Qualifier.class)){
						Qualifier qualifier=param.getAnnotation(Qualifier.class);
						Class<?> type=param.getType();
						if(qualifier.value().isEmpty()){
							if(type.isInterface()){
								paramValues.add(getBean(param.getName()));
							}else{
								paramValues.add(getBean(type));
							}
						}else{
							paramValues.add(getBean(qualifier.value()));
						}
					}else{
						if(!paramValues.isEmpty()){
							String message=Message.getMessage(Message.PM_3017, bean.getName());
							throw new ApplicationException(message);
						}
						break;
					}
				}
				if(!paramValues.isEmpty()){
					try {
						instance=constructor.newInstance(paramValues.toArray());
						break;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new ApplicationException(e);
					}
				}
			}
		}
		if (instance == null) {
			//如果构造函数未注册则创造一个实例
			try {
				instance=bean.getPrototype().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new ApplicationException(e);
			}
			//把对象加入缓存列表
			putCacheContext(bean.getName(),instance);
		}
		//字段注入
		Class<?> cClass=instance.getClass();
		while (true) {
			if (cClass == null||cClass.equals(Object.class)) {
				break;
			}
			for (Field field : cClass.getDeclaredFields()) {
				if (field.getModifiers() != 1 && field.getModifiers() != 2
						&& field.getModifiers() != 4) {
					continue;
				}
				if(isNewObject){
					//设置常量
					Constant constant=field.getAnnotation(Constant.class);
					if (constant!=null) {
						String name=constant.value().isEmpty()?field.getName():constant.value();
						field.setAccessible(true);
						try {
							field.set(instance,ApplicationIO.read(field, ConstantConfig.getString(name)));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new ApplicationException(e);
						}
						continue;
					}
				}
				//设置对象
				Resource resource=field.getAnnotation(Resource.class);
				if (resource!=null) {
					field.setAccessible(true);
					try {
						Class<?> type=field.getType();
						if(resource.value().isEmpty()){
							if(type.isInterface()){
								field.set(instance,getBean(field.getName()));
							}else{
								field.set(instance,getBean(type));
							}
						}else{
							field.set(instance,getBean(resource.value()));
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new ApplicationException(e);
					}
					continue;
				}
			}
			cClass = cClass.getSuperclass();
		}
		//当前对象为BeanInfo则注入设置的常量值
		for(Method method:bean.getPrototype().getMethods()){
			String methodName=method.getName();
			if(methodName.startsWith("set")){
				String name=methodName.substring(3, 4).toLowerCase()+methodName.substring(4, methodName.length());
				String value=null;
				if(isNewObject){
					value=bean.getValues().get(name);
					if(value!=null){
						Class<?> type=method.getParameterTypes()[0];
						try {
							method.invoke(instance, ApplicationIO.read(null,type,ConstantConfig.get(bean.getValues().get(name))));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
							throw new ApplicationException(e);
						}
						continue;
					}
				}
				value=bean.getRefs().get(name);
				if(value!=null){
					try {
						method.invoke(instance, getBean(bean.getRefs().get(name)));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ApplicationException(e);
					}
					continue;
				}
			}
		}
		//如果为缓存对象则不重复执行初始化方法
		if(isNewObject){
			if(instance instanceof ApplicationContextAware){
				try {
					((ApplicationContextAware)instance).setApplicationContext(this);
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
			if(instance instanceof BeanDefinitionAware){
				try {
					((BeanDefinitionAware)instance).setBeanDefinition(bean);
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
			//执行初始化方法
			if(instance instanceof InitializingBean){
				try {
					((InitializingBean)instance).afterPropertiesSet();
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
			//执行初始化方法
			ReflectUtils.invokeMethod(instance,bean.getInit());
		}
		//每次获取对象时都会调用该方法
		if(instance instanceof BeforeBean){
			try {
				((BeforeBean)instance).beforeinvoking(bean);
			} catch (Exception e) {
				throw new ApplicationException(e);
			}
		}
		return instance;
	}

	@Override
	public void close() throws IOException {
		for(String name:cacheContext.keySet()){
			BeanDefinition bean=getBeanDefinitionInfoByName(name);
			Object instance=cacheContext.get(name);
			ReflectUtils.invokeMethod(instance,bean.getDestory());
			if(instance instanceof DisposableBean){
				try {
					((DisposableBean)instance).destroy();
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
		}
	}
	
}
