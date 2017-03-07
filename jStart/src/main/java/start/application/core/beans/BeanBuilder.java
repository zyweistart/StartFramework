package start.application.core.beans;

import start.application.context.ContextObject;

public abstract class BeanBuilder {
	
	public void registerBeanManager(String beanName,String className){
		BeanDefinition bd=new BeanDefinition();
		bd.setName(beanName);
		bd.setPrototype(className);
		bd.setContextName(this);
		ContextObject.registerBean(bd);
	}
	
	/**
	 * 根据类名获取对象
	 * @param bean
	 * @return
	 */
	public abstract Object getBean(BeanDefinition bean);

}
