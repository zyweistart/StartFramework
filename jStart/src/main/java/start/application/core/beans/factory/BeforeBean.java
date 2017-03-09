package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

public interface BeforeBean {
	
	/**
	 * 每次从容器获取对象时都会调用该方法
	 * @throws Exception
	 */
	void beforeinvoking(BeanDefinition bean) throws Exception;
}
