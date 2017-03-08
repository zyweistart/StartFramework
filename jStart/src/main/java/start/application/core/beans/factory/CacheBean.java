package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

public interface CacheBean {

	/**
	 * 获取缓存Bean对象
	 * @param bean
	 * @return
	 */
	Object getCache(BeanDefinition bean);
	
}
