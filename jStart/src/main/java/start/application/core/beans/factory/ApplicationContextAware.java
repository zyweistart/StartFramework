package start.application.core.beans.factory;

public interface ApplicationContextAware {
	
	/**
	 * 设置当前容器对象到Bean实例中
	 * @param context
	 */
	void setApplicationContext(ApplicationContext context);
	
}
