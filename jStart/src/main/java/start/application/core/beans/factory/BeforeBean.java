package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

public interface BeforeBean {
	
	/**
	 * 每次获取数据之前调用
	 * @throws Exception
	 */
	void beforeBean(BeanDefinition bean) throws Exception;
}
