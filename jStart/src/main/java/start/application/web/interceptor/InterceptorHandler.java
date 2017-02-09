package start.application.web.interceptor;

import start.application.web.action.ActionSupport;

/**
 * 拦截器类需要继承该对象
 * 
 * @author zhenyao
 *
 */
public abstract class InterceptorHandler extends AbstractInterceptorHandler implements Interceptor {

	public void doInterceptor(ActionSupport action) throws Exception{
		if(getHandler()!=null){
			getHandler().intercept(action);
		}
	}
	
}
