package start.application.plugins.mybatis;

import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class MybatisCglibProxy implements MethodInterceptor {

	private SqlSession session;
	
	public Object getProxyBean(Class<?> prototype,SqlSessionFactory sessionFactory) {
		this.session=sessionFactory.openSession();
		
		session.getMapper(prototype);
		
		
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(prototype);
		enhancer.setCallback(this);
		return enhancer.create();
	}

	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		try{
			Object result = methodProxy.invokeSuper(object, args);
			return result;
		}finally{
			this.session.commit();
			this.session.close();
		}
	}

}
