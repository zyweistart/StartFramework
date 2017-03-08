package start.application.core.beans.factory;

public interface DisposableBean {
	
	/**
	 * 释放对象，只调用一次
	 * @throws Exception
	 */
	void destroy() throws Exception;

}
