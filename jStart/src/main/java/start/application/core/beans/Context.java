package start.application.core.beans;

import start.application.core.beans.factory.DisposableBean;
import start.application.core.beans.factory.InitializingBean;

public interface Context extends InitializingBean,DisposableBean {
	
	/**
	 * 获取实例对象,重复调用
	 * @param bean
	 * @return
	 */
	public abstract Object newBean(BeanDefinition bean);
	
}
