package start.application.core.beans.factory;

public interface ClosedBean {
	
	/**
	 * 用于关闭不可用的连接资源，单例下可调用多次
	 * @throws Exception
	 */
	void close() throws Exception;
	
}
