package start.application.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.context.ApplicationContext;
import start.application.context.ContextObject;
import start.application.context.annotation.Controller;
import start.application.context.config.ConstantConfig;
import start.application.core.beans.BeanInfo;
import start.application.web.action.ActionSupport;
import start.application.web.interceptor.InterceptorHandler;
import start.application.web.result.ActionResult;
import start.application.web.result.ActionResultInvocation;
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
			ActionSupport action = (ActionSupport) obj;
			action.setRequest(this.mRequest);
			action.setResponse(this.mResponse);
			action.setFilterHostConfig(this.mFilterHostConfig);
			action.setApplicationContext(application);
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
				handler.intercept(action);
			}
			//执行Action
			ActionResult result=action.execute();
			if (result != null) {
				// 返回值必须实现了ActionResult接口
				ActionResultInvocation invocation = new ActionResultInvocation();
				invocation.setAction(action);
				result.doExecute(invocation);
			}
		}finally{
			if(application!=null){
				application.close();
				application=null;
			}
		}
	}
	
}