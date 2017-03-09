package start.application.core.beans;

import start.application.context.ContextObject;

public abstract class ContextAdvice implements Context{
	
	/**
	 * 注册Bean对象到管理中心
	 * @param beanName 名称
	 * @param className  全类名
	 */
	public void registerBeanCenter(String beanName,String className){
		BeanDefinition bd=new BeanDefinition();
		bd.setName(beanName);
		bd.setPrototype(className);
		bd.setBeanContextName(this.getClass().getName());
		ContextObject.registerBean(bd);
	}
	
}
