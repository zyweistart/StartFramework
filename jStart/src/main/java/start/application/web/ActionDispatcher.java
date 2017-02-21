package start.application.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.context.ApplicationContext;
import start.application.context.ContextObject;
import start.application.context.annotation.Controller;
import start.application.context.config.ConstantConfig;
import start.application.core.Message;
import start.application.core.beans.BeanInfo;
import start.application.web.action.ActionSupport;
import start.application.web.exceptions.ActionException;
import start.application.web.interceptor.InterceptorHandler;
import start.application.web.utils.FilterHostConfig;


/**
 * URL解析触发类
 * @author zhenyao
 *
 */
public final class ActionDispatcher {
	
	private final HttpServletRequest mRequest;
	private final HttpServletResponse mResponse;
	private final FilterHostConfig mFilterHostConfig;

	public ActionDispatcher(HttpServletRequest request, HttpServletResponse response, FilterHostConfig fhostConfig) {
		this.mRequest = request;
		this.mResponse = response;
		this.mFilterHostConfig = fhostConfig;
	}

	/**
	 * 解析
	 * 
	 * @param name
	 *            控制层的Action别名
	 */
	public void start(String name) throws Exception {
		BeanInfo bean = ContextObject.getBean(name);
		// 只允许访问@Controller的Bean
		if (!bean.getPrototype().isAnnotationPresent(Controller.class)) {
			throw new  IllegalAccessException(name+"无访问权限" );
		}
		// 请求编码设置防止乱码
		if (this.mRequest.getCharacterEncoding() == null) {
			this.mRequest.setCharacterEncoding(ConstantConfig.ENCODING);
		}
		ApplicationContext application=null;
		try{
			application=new ApplicationContext();
			Object obj =application.getBean(bean.getName());
			if (obj instanceof ActionSupport) {
				ActionSupport action = (ActionSupport) obj;
				action.setRequest(this.mRequest);
				action.setResponse(this.mResponse);
				action.setFilterHostConfig(this.mFilterHostConfig);
				action.setApplicationContext(application);
				doInterceptor(action);
			} else {
				String message = Message.getMessage(Message.PM_4005);
				throw new ActionException(message);
			}
		}finally{
			application.close();
		}
	}
	
	/**
	 * 责任链模式执行拦截器
	 */
	private void doInterceptor(ActionSupport action) throws Exception {
		InterceptorHandler handler=action;
		Iterator<String> interceptors = ContextObject.getInterceptors().iterator();
		while(interceptors.hasNext()){
			InterceptorHandler currentHandler=(InterceptorHandler) action.getBean(interceptors.next());
			currentHandler.setHandler(handler);
			handler=currentHandler;
		}
		handler.intercept(action);
	}
	
}