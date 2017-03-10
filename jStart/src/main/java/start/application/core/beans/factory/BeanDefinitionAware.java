package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

/**
 * 
 * @author zhenyao
 *
 */
public interface BeanDefinitionAware {
	
	/**
	 * 设置当前Bean的定义信息到实例对象中
	 * @param beanDefinition
	 */
	void setBeanDefinition(BeanDefinition beanDefinition);
	
}
