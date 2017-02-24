package start.application.web.interceptor;

import start.application.web.action.ActionSupport;

/**
 * 拦截器基接口
 */
public interface Interceptor {

	void intercept(ActionSupport support) throws Exception;

}