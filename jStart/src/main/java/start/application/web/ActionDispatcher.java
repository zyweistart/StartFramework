package start.application.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.context.ApplicationContext;
import start.application.context.ContextObject;
import start.application.context.annotation.Controller;
import start.application.context.config.ConstantConfig;
import start.application.core.beans.BeanDefinition;
import start.application.web.action.Action;
import start.application.web.action.ActionResult;
import start.application.web.action.ActionSupport;
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
		BeanDefinition bean = ContextObject.getBean(name);
		// 只允许访问@Controller的Bean
		if (!bean.getPrototype().isAnnotationPresent(Controller.class)) {
			throw new  IllegalAccessException(name+"无访问权限" );
		}
		// 请求编码设置防止乱码
		if (this.mRequest.getCharacterEncoding() == null) {
			this.mRequest.setCharacterEncoding(ConstantConfig.ENCODING);
		}
		try(ApplicationContext application=new ApplicationContext()){
			Action action = (Action)application.getBean(bean.getName());
			ActionSupport support=new ActionSupport();
			support.setAction(action);
			support.setRequest(this.mRequest);
			support.setResponse(this.mResponse);
			support.setFilterHostConfig(this.mFilterHostConfig);
			support.setApplicationContext(application);
			if(!ContextObject.getInterceptors().isEmpty()){
				//责任链模式执行拦截器
				InterceptorHandler handler=null;
				Iterator<String> interceptors = ContextObject.getInterceptors().iterator();
				while(interceptors.hasNext()){
					InterceptorHandler currentHandler=(InterceptorHandler)application.getBean(interceptors.next());
					currentHandler.setHandler(handler);
					handler=currentHandler;
				}
				//执行拦截器
				handler.intercept(support);
			}
			//执行Action
			ActionResult result=action.execute(support);
			if (result != null) {
				// 返回值必须实现了ActionResult接口
				result.doExecute(support);
			}
		}
	}
	
}