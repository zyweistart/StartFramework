package start.application.core.beans;

public interface Context {

	/**
	 * 获取实例对象,重复调用
	 * @param bean
	 * @return
	 */
	Object newBean(BeanDefinition bean);
	
}
