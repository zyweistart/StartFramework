package start.application.orm.support.jdbc;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import start.application.core.aop.AOPBeanProxy;
import start.application.orm.annotation.Transaction;

public class DataSourceTransactionManager implements AOPBeanProxy {
	
	private SessionManager session;

	public SessionManager getSession() {
		return session;
	}

	public void setSession(SessionManager session) {
		this.session = session;
	}

	@Override
	public void doBefore(Object obj, Method method, Object[] args, MethodProxy proxy) {
		if(method.isAnnotationPresent(Transaction.class)){
			session.beginTrans();
		}
	}

	@Override
	public void doAfter(Object obj, Method method, Object[] args, MethodProxy proxy) {
		if(method.isAnnotationPresent(Transaction.class)){
			session.commitTrans();
		}
	}

	@Override
	public void doException(Object obj, Method method, Object[] args, MethodProxy proxy, Throwable e) {
		if(method.isAnnotationPresent(Transaction.class)){
			session.rollbackTrans();
		}
	}

	@Override
	public void doFinally(Object obj, Method method, Object[] args, MethodProxy proxy) {
		if(method.isAnnotationPresent(Transaction.class)){
		}
	}
	
}
