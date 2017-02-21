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

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.annotation.Constant;
import start.application.context.annotation.Qualifier;
import start.application.context.annotation.Resource;
import start.application.context.config.ConstantConfig;
import start.application.core.Message;
import start.application.core.beans.BeanContextFactory;
import start.application.core.beans.BeanInfo;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;
import start.application.core.utils.StackTraceInfo;

/**
 * 容器参数注入
 * @author Start
 */
public class ApplicationContext implements Closeable{
	
	private final static Logger log=LoggerFactory.getLogger(ApplicationContext.class);
	
	private Map<String,Object> mContextObjectHolder=new HashMap<String,Object>();

	public Object getBean(String name){
		return getBean(ContextObject.getBean(name));
	}
	
	public Object getBean(Object instance){
		return injectionObject(instance, ContextObject.getBeanInfo(instance.getClass().getName()));
	}
	
	public Object getBean(BeanInfo bean){
		Object instance=null;
		//判断是否为单例模式
		if(bean.isSingleton()){
			instance=Container.getSingletonBeans().get(bean.getName());
		}else{
			instance=mContextObjectHolder.get(bean.getName());
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
					initTargs.add(getBean(qualifier.value()));
				}else{
					if(initTargs.size()>0){
						String message=Message.getMessage(Message.PM_3017, bean.getName());
						throw new ApplicationException(message);
					}
					break;
				}
			}
			if(initTargs.size()>0){
				try {
					instance=constructor.newInstance(initTargs.toArray());
					break;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
					throw new ApplicationException(e);
				}
			}
		}
		if (instance == null) {
			//如果构造函数未注册则创造一个实例
			try {
				instance = BeanContextFactory.getInstance().getBean(bean);
			} catch (Exception e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new ApplicationException(e);
			}
		}
		injectionObject(instance, bean);
		//执行初始化方法
		ReflectUtils.invokeMethod(instance,bean.getPrototype(),bean.getInit());
		if(bean.isSingleton()){
			Container.getSingletonBeans().put(bean.getName(), instance);
		}else{
			mContextObjectHolder.put(bean.getName(), instance);
		}
		return instance;
	}
	
	/**
	 * 实例化对象注入
	 * @param instance
	 * @return
	 */
	private Object injectionObject(Object instance,BeanInfo bean){
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
						field.set(instance,ContextDataReadWrite.convertReadIn(field, ConstantConfig.getString(name)));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
						throw new ApplicationException(e);
					}
					continue;
				}
				//对象
				Resource resource=field.getAnnotation(Resource.class);
				if (resource!=null) {
					String name=resource.value().isEmpty()?field.getName():resource.value();
					field.setAccessible(true);
					try {
						field.set(instance,getBean(name));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
						throw new ApplicationException(e);
					}
					continue;
				}
			}
			cClass = cClass.getSuperclass();
		}
		//当前对象为BeanInfo则注入设置的常量值
		if(bean!=null){
			for(Method method:bean.getPrototype().getMethods()){
				String methodName=method.getName();
				if(methodName.startsWith("set")){
					String name=methodName.substring(3, 4).toLowerCase()+methodName.substring(4, methodName.length());
					String value=bean.getValues().get(name);
					if(value!=null){
						Class<?> type=method.getParameterTypes()[0];
						try {
							method.invoke(instance, ContextDataReadWrite.convertReadIn(null,type,ConstantConfig.get(bean.getValues().get(name))));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
							throw new ApplicationException(e);
						}
						continue;
					}
					value=bean.getRefs().get(name);
					if(value!=null){
						try {
							method.invoke(instance, getBean(bean.getRefs().get(name)));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
							throw new ApplicationException(e);
						}
						continue;
					}
				}
			}
		}
		return instance;
	}
	
	@Override
	public void close() {
		for(String name:mContextObjectHolder.keySet()){
			BeanInfo bean=ContextObject.getBean(name);
			Object instance=mContextObjectHolder.get(name);
			ReflectUtils.invokeMethod(instance,bean.getPrototype(),bean.getDestory());
		}
	}
	
}