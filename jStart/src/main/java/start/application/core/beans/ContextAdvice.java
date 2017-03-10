package start.application.core.beans;

import start.application.core.beans.factory.ApplicationContext;
import start.application.core.beans.factory.ApplicationContextAware;

public abstract class ContextAdvice implements Context,ApplicationContextAware{
	
	private ApplicationContext mApplication;
	
	/**
	 * 注册Bean对象到管理中心
	 * @param beanName 名称
	 * @param className  全类名
	 */
	public void registerBeanCenter(String beanName,String className){
		BeanDefinition bd=new BeanDefinition();
		bd.setName(beanName);
		bd.setPrototype(className);
		bd.setBeanContextName(this.mApplication.getBeanDefinitionInfoByClass(this.getClass().getName()).getName());
		this.mApplication.registerBeanDoManagerCenter(bd);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.mApplication=applicationContext;
	}
	
}
