package start.application.core.aop;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import start.application.core.exceptions.ApplicationException;

public class BeanProxyInterceptor implements MethodInterceptor {
	
	private Set<AOPBeanProxy> proxys=new HashSet<AOPBeanProxy>();
	
	/**
	 * 创建代理对象
	 * @param target
	 * @return
	 */
	public Object getInstance(Class<?> prototype) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(prototype);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	
	/**
	 * 创建代理对象
	 */
	public Object getInstance(Class<?> prototype,Class<?>[] types,Object[] values) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(prototype);
		enhancer.setCallback(this);
		return enhancer.create(types,values);
	}

	public void addProxy(AOPBeanProxy proxy){
		proxys.add(proxy);
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
		Object resultObj=null;
		try{
			for(AOPBeanProxy p:proxys){
				p.doBefore(obj, method, args, proxy);
			}
			resultObj=proxy.invokeSuper(obj, args);
			for(AOPBeanProxy p:proxys){
				p.doAfter(resultObj, method, args, proxy);
			}
		}catch(Throwable e){
			for(AOPBeanProxy p:proxys){
				p.doException(resultObj, method, args, proxy, e);
			}
			throw new ApplicationException(e);
		}finally{
			for(AOPBeanProxy p:proxys){
				p.doFinally(resultObj, method, args, proxy);
			}
		}
		return resultObj;
	}

}