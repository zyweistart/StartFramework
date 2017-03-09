package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

public interface BeforeBean {
	
	/**
	 * 方法调用之前执行
	 * @throws Exception
	 */
	void beforeinvoking(BeanDefinition bean) throws Exception;
}
