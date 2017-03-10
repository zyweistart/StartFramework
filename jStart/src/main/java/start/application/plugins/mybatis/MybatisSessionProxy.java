package start.application.plugins.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;

import start.application.core.exceptions.ApplicationException;

public class MybatisSessionProxy implements InvocationHandler {
	
	private SqlSession session;
	private Object targetObject;

	public MybatisSessionProxy(SqlSession session,Object targetObject) {
		this.session=session;
		this.targetObject = targetObject;
	}

	public Object invoke(Object proxy, Method method, Object[] args) {
		Object resultObj=null;
		try{
			resultObj=method.invoke(targetObject, args);
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new ApplicationException(e);
		}finally{
//			session.close();
		}
		return resultObj;
	}
	
}
