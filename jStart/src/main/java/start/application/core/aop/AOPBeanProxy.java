package start.application.core.aop;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

public interface AOPBeanProxy {
	
	/**
	 * 执行条件，只有返回为真时才会执行拦截操作
	 */
	boolean condition(Object obj, Method method, Object[] args, MethodProxy proxy);
	
	/**
	 * 执行之前
	 * @param proxy
	 * @param method
	 * @param args
	 */
	void doBefore(Object obj, Method method, Object[] args, MethodProxy proxy);
	
	/**
	 * 执行之后
	 * @param proxy
	 * @param method
	 * @param args
	 */
	void doAfter(Object obj, Method method, Object[] args, MethodProxy proxy);
	
	/**
	 * 执行方法出现异常
	 * @param proxy
	 * @param method
	 * @param args
	 * @param e
	 */
	void doException(Object obj, Method method, Object[] args, MethodProxy proxy,Throwable e);
	
	/**
	 * 执行结束
	 * @param proxy
	 * @param method
	 * @param args
	 */
	void doFinally(Object obj, Method method, Object[] args, MethodProxy proxy);
	
	
}
