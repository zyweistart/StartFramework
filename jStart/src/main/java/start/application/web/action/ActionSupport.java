package start.application.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.context.ApplicationContext;
import start.application.core.beans.BeanDefinition;
import start.application.web.utils.ApplicationMap;
import start.application.web.utils.FilterHostConfig;
import start.application.web.utils.RequestMap;
import start.application.web.utils.SessionMap;

public class ActionSupport{

	private Action action;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterHostConfig filterHostConfig;
	private ApplicationContext applicationContext;
	private BeanDefinition bean;
	
	private RequestMap requestMap;
	private SessionMap<String, Object> sessionMap;
	private ApplicationMap applicationMap;

	public ActionSupport(
			Action action,
			HttpServletRequest request,
			HttpServletResponse response,
			FilterHostConfig filterHostConfig,
			ApplicationContext applicationContext,
			BeanDefinition bean
			){
		this.action=action;
		this.request=request;
		this.response=response;
		this.filterHostConfig=filterHostConfig;
		this.applicationContext=applicationContext;
		this.bean=bean;
	}
	
	
	public Action getAction() {
		return action;
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
	
	public Object getBean(Class<?> prototype){
		return applicationContext.getBean(prototype);
	}

	public BeanDefinition getBean() {
		return bean;
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

}