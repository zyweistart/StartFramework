package start.application.core.beans.factory;

import start.application.core.beans.BeanDefinition;

public interface ApplicationContext {
	
	/**
	 * 判断Bean是否已经定义
	 * @param bean
	 */
	boolean  isBeanDefinitionExistence(String prototypeString);
	
	/**
	 * 注册Bean到管理中心
	 * @param bean
	 */
	void registerBeanDoManagerCenter(BeanDefinition bean);
	
	/**
	 * 根据Bean名称获取Bean定义信息
	 * @param name 定义的名称
	 * @return
	 */
	BeanDefinition getBeanDefinitionInfoByName(String name);
	
	/**
	 * 根据Bean对应的类全名获取定义信息
	 * @param prototypeString 类全名
	 * @return
	 */
	BeanDefinition getBeanDefinitionInfoByClass(String prototypeString);
	
	/**
	 * 根据Name创建生成Bean实例
	 * @param name
	 * @return
	 */
	Object getBean(String name);
	
	/**
	 * 根据类创建生成Bean实例
	 * @param name
	 * @return
	 */
	<T> T  getBean(Class<T> prototype);
	
	
}
