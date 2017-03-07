package start.application.context;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.core.Message;
import start.application.core.annotation.Constant;
import start.application.core.annotation.Qualifier;
import start.application.core.annotation.Resource;
import start.application.core.beans.BeanBuilder;
import start.application.core.beans.BeanBuilderFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.config.ConstantConfig;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;

/**
 * 容器参数注入
 * @author Start
 */
public class ApplicationContext implements Closeable{
	
	private Map<String,Object> contextObjectHolder=new HashMap<String,Object>();
	
	public Object getBean(String name){
		BeanDefinition bean=ContextObject.getBean(name);
		return getBean(bean);
	}

	public Object getBean(Class<?> prototype){
		BeanDefinition bean=ContextObject.getBeanInfo(prototype.getName());
		if(bean==null){
			//如果当前类对象不是定义的Bean对象则创建一个临时的Bean对象
			bean=AnnotationContext.analysis(prototype);
			if(bean==null){
				bean=new BeanDefinition();
				bean.setName(prototype.getName());
				bean.setPrototype(prototype.getName());
			}
		}
		return getBean(bean);
	}
	
	public Object getBean(String name,Class<?> prototype){
		if(ContextObject.isBeanExistence(name)){
			return getBean(ContextObject.getBean(name));
		}else{
			BeanDefinition bean=AnnotationContext.analysis(prototype);
			if(bean==null){
				bean=new BeanDefinition();
				bean.setName(name);
				bean.setPrototype(prototype.getName());
			}
			return getBean(bean);
		}
	}
	
	private Object getBean(BeanDefinition bean){
		Object instance=null;
		//判断是否为单例模式
		if(bean.isSingleton()){
			instance=Container.getSingletonBeans().get(bean.getName());
		}else{
			instance=getContextObjectHolder().get(bean.getName());
		}
		if(instance!=null){
			//如果已经存在实例则直接返回
			return instance;
		}
		//构造函数注入
		for (Constructor<?> constructor : bean.getPrototype().getConstructors()) {
			List<Object> initTargs=new ArrayList<Object>();
			for(Parameter param:constructor.getParameters()){
				if(param.isAnnotationPresent(Qualifier.class)){
					Qualifier qualifier=param.getAnnotation(Qualifier.class);
					if(qualifier.value().isEmpty()){
						initTargs.add(getBean(param.getName(),param.getType()));
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
		if (instance == null) {
			//如果构造函数未注册则创造一个实例
			BeanBuilder builder=BeanBuilderFactory.getContext(bean.getContextName());
			if(builder==null){
				try {
					instance =bean.getPrototype().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new ApplicationException(e);
				}
			}else{
				instance = builder.getBean(bean);
			}
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
				//常量
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
				//对象
				Resource resource=field.getAnnotation(Resource.class);
				if (resource!=null) {
					field.setAccessible(true);
					try {
						if(resource.value().isEmpty()){
							field.set(instance,getBean(field.getName(),field.getType()));
						}else{
							field.set(instance,getBean(resource.value(),field.getType()));
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
				String value=bean.getValues().get(name);
				if(value!=null){
					Class<?> type=method.getParameterTypes()[0];
					try {
						method.invoke(instance, ApplicationIO.read(null,type,ConstantConfig.get(bean.getValues().get(name))));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ApplicationException(e);
					}
					continue;
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
		//执行初始化方法
		ReflectUtils.invokeMethod(instance,bean.getInit());
		if(bean.isSingleton()){
			Container.getSingletonBeans().put(bean.getName(), instance);
		}else{
			getContextObjectHolder().put(bean.getName(), instance);
		}
		return instance;
	}
	
	@Override
	public void close() {
		for(String name:getContextObjectHolder().keySet()){
			if(ContextObject.isBeanExistence(name)){
				BeanDefinition bean=ContextObject.getBean(name);
				Object instance=getContextObjectHolder().get(name);
				ReflectUtils.invokeMethod(instance,bean.getDestory());
			}
		}
		this.contextObjectHolder=null;
	}

	public Map<String, Object> getContextObjectHolder() {
		return contextObjectHolder;
	}
	
}