package start.application.core.beans;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import start.application.context.ContextObject;
import start.application.core.beans.factory.CacheBean;
import start.application.core.beans.factory.DisposableBean;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;

public class DefaultBeanContext extends ContextAdvice implements CacheBean {

	private ConcurrentMap<String,Object> cacheContext=new ConcurrentHashMap<String,Object>();

	public ConcurrentMap<String, Object> getCacheContext() {
		return cacheContext;
	}

	@Override
	public Object getCache(BeanDefinition bean){
		return getCacheContext().get(bean.getName());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	@Override
	public Object newBean(BeanDefinition bean){
		try {
			Object instance=bean.getPrototype().newInstance();
			//容器生成类不加入缓存队列
			getCacheContext().put(bean.getName(), instance);
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}
	
	@Override
	public void destroy() throws Exception {
		for(String name:getCacheContext().keySet()){
			BeanDefinition bean=ContextObject.getBean(name);
			Object instance=getCacheContext().get(name);
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
