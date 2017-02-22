package start.application.web.interceptor;

import start.application.web.action.ActionSupport;
import start.application.web.result.ActionResult;
import start.application.web.result.ActionResultInvocation;

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
		}else{
			ActionResult result=action.execute();
			if (result != null) {
				// 返回值必须实现了ActionResult接口
				ActionResultInvocation invocation = new ActionResultInvocation();
				invocation.setAction(action);
				result.doExecute(invocation);
			}
		}
	}
	
}
