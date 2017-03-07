package start.application.core.beans.factory;

public interface DisposableBean {
	
	/**
	 * 释放对象
	 * @throws Exception
	 */
	void destroy() throws Exception;

}
