package start.application.core.beans;

import start.application.context.ContextObject;

public abstract class BeanBuilder{
	
	/**
	 * 注册Bean对象到管理中心
	 * @param beanName 名称
	 * @param className  全类名
	 */
	public void registerBeanCenter(String beanName,String className){
		BeanDefinition bd=new BeanDefinition();
		bd.setName(beanName);
		bd.setPrototype(className);
		bd.setContextName(this);
		ContextObject.registerBean(bd);
	}
	
	/**
	 * 属性设置之后调用,一身只调用一次
	 * @throws Exception
	 */
	public abstract void afterPropertiesSet() throws Exception;
	
	/**
	 * 每次获取数据之前调用
	 * @throws Exception
	 */
	public abstract void beforeBean(BeanDefinition bean) throws Exception;
	
	/**
	 * 获取实例对象
	 * @param bean
	 * @return
	 */
	public abstract Object getBean(BeanDefinition bean);

	/**
	 * 用于关闭不可用的连接资源，单例下可调用多次
	 * @throws Exception
	 */
	public abstract void close() throws Exception;
	
	/**
	 * 释放对象，只调用一次
	 * @throws Exception
	 */
	public abstract void destroy() throws Exception;
}
