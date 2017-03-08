package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.context.ContextObject;
import start.application.core.beans.factory.CacheBean;
import start.application.core.beans.factory.ClosedBean;
import start.application.core.beans.factory.DisposableBean;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ReflectUtils;

public class DefaultBeanBuilder extends BeanBuilder implements CacheBean {

	private Map<String,Object> cacheContext=new HashMap<String,Object>();

	public Map<String, Object> getCacheContext() {
		return cacheContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@Override
	public Object getCache(BeanDefinition bean){
		return getCacheContext().get(bean.getName());
	}
	
	@Override
	public void beforeBean(BeanDefinition bean) throws Exception {
		
	}
	
	@Override
	public Object getBean(BeanDefinition bean){
		try {
			Object instance=bean.getPrototype().newInstance();
			if(!(instance instanceof BeanBuilder)){
				//容器生成类不加入缓存队列
				getCacheContext().put(bean.getName(), instance);
			}
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}

	@Override
	public void close() throws Exception {
		for(String name:getCacheContext().keySet()){
			Object instance=getCacheContext().get(name);
			if(instance instanceof ClosedBean){
				try {
					((ClosedBean)instance).close();
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
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
