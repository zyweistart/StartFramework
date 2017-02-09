package start.application.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.context.ApplicationContext;
import start.application.web.interceptor.InterceptorHandler;
import start.application.web.result.ActionResult;
import start.application.web.result.ActionResultInvocation;
import start.application.web.utils.ApplicationMap;
import start.application.web.utils.FilterHostConfig;
import start.application.web.utils.RequestMap;
import start.application.web.utils.SessionMap;

public abstract class ActionSupport extends InterceptorHandler implements Action{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterHostConfig filterHostConfig;
	private ApplicationContext applicationContext;
	
	private RequestMap requestMap;
	private SessionMap<String, Object> sessionMap;
	private ApplicationMap applicationMap;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setFilterHostConfig(FilterHostConfig filterHostConfig) {
		this.filterHostConfig = filterHostConfig;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public HttpServletRequest request() {
		return request;
	}

	public HttpServletResponse response() {
		return response;
	}

	public FilterHostConfig filterHostConfig() {
		return filterHostConfig;
	}
	
	public Object getBean(String name){
		return applicationContext.getBean(name);
	}

	/**
	 * 请求对象域
	 */
	public RequestMap RequestMap() {
		if (requestMap == null) {
			requestMap = new RequestMap(request());
		}
		return requestMap;
	}

	/**
	 * Session会话对象域
	 */
	public SessionMap<String, Object> SessionMap() {
		if (sessionMap == null) {
			sessionMap = new SessionMap<String, Object>(request());
		}
		return sessionMap;
	}

	/**
	 * 应用程序域
	 */
	public ApplicationMap ApplicationMap() {
		if (applicationMap == null) {
			applicationMap = new ApplicationMap(request().getServletContext());
		}
		return applicationMap;
	}

	@Override
	public void intercept(ActionSupport action) throws Exception {
		ActionResult result=action.execute();
		if (result != null) {
			// 返回值必须实现了ActionResult接口
			ActionResultInvocation invocation = new ActionResultInvocation();
			invocation.setAction(action);
			result.doExecute(invocation);
		}
	}

}