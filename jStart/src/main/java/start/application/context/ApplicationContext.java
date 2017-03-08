package start.application.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import start.application.core.Message;
import start.application.core.annotation.Constant;
import start.application.core.annotation.Qualifier;
import start.application.core.annotation.Resource;
import start.application.core.beans.BeanBuilder;
import start.application.core.beans.BeanBuilderFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.BeforeBean;
import start.application.core.beans.factory.CacheBean;
import start.application.core.beans.factory.InitializingBean;
import start.application.core.config.ConstantConfig;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;

/**
 * 容器参数注入
 * @author Start
 */
public class ApplicationContext{
	
	public static Object getBean(String name){
		return getBean(ContextObject.getBean(name));
	}

	public static Object getBean(Class<?> prototype){
		BeanDefinition bean=ContextObject.getBeanInfo(prototype.getName());
		if(bean==null){
			bean=new BeanDefinition();
			bean.setName(prototype.getName());
			bean.setPrototype(prototype.getName());
		}
		return getBean(bean);
	}
	
	public static Object getBean(String name,Class<?> prototype){
		if(ContextObject.isBeanExistence(name)){
			return getBean(ContextObject.getBean(name));
		}else{
			BeanDefinition bean=new BeanDefinition();
			bean.setName(name);
			bean.setPrototype(prototype.getName());
			return getBean(bean);
		}
	}
	
	public static Object getBean(BeanDefinition bean){
		BeanBuilder builder=BeanBuilderFactory.getBeanContext(bean.getContextName());
		try {
			builder.beforeBean(bean);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		Object instance=getBean(bean,builder);
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
		//
		if(instance instanceof BeforeBean){
			try {
				((BeforeBean)instance).beforeBean(bean);
			} catch (Exception e) {
				throw new ApplicationException(e);
			}
		}
		return instance;
	}
	
	public static Object getBean(BeanDefinition bean,BeanBuilder builder){
		Object instance=null;
		if(builder instanceof CacheBean){
			instance=((CacheBean)builder).getCache(bean);
		}
		boolean isSetConstantValue=true;
		if(instance!=null){
			if(instance instanceof BeforeBean){
				try {
					((BeforeBean)instance).beforeBean(bean);
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
			//对于一些引用型的数据须重新设值
			isSetConstantValue=false;
			//如果已经存在实例则直接返回
//			return instance;
		}
		if(instance==null){
			//构造函数注入
			for (Constructor<?> constructor : bean.getPrototype().getConstructors()) {
				List<Object> initTargs=new ArrayList<Object>();
				for(Parameter param:constructor.getParameters()){
					if(param.isAnnotationPresent(Qualifier.class)){
						Qualifier qualifier=param.getAnnotation(Qualifier.class);
						Class<?> type=param.getType();
						if(qualifier.value().isEmpty()){
							if(ContextObject.getBeanInfo(type.getName())==null){
								initTargs.add(getBean(param.getName(),type));
							}else{
								initTargs.add(getBean(type));
							}
						}else{
							initTargs.add(getBean(qualifier.value(),param.getType()));
						}
					}else{
						if(!initTargs.isEmpty()){
							String message=Message.getMessage(Message.PM_3017, bean.getName());
							throw new ApplicationException(message);
						}
						break;
					}
				}
				if(!initTargs.isEmpty()){
					try {
						instance=constructor.newInstance(initTargs.toArray());
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
			instance=builder.getBean(bean);
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
				if(isSetConstantValue){
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
							if(ContextObject.getBeanInfo(type.getName())==null){
								field.set(instance,getBean(field.getName(),type));
							}else{
								field.set(instance,getBean(type));
							}
						}else{
							field.set(instance,getBean(resource.value(),type));
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
				if(isSetConstantValue){
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
		return instance;
	}
	
}