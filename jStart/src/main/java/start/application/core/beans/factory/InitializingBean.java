package start.application.core.beans.factory;

public interface InitializingBean {
	
	/**
	 * 属性设置之后调用,一身只调用一次
	 * @throws Exception
	 */
	void afterPropertiesSet() throws Exception;

}
