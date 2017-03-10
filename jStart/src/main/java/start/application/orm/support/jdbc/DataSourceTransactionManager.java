package start.application.orm.support.jdbc;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import start.application.core.AOPBeanProxy;

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
		session.beginTrans();
	}

	@Override
	public void doAfter(Object obj, Method method, Object[] args, MethodProxy proxy) {
		session.commitTrans();
	}

	@Override
	public void doException(Object obj, Method method, Object[] args, MethodProxy proxy, Throwable e) {
		session.rollbackTrans();
	}

	@Override
	public void doFinally(Object obj, Method method, Object[] args, MethodProxy proxy) {
	}
	
}
