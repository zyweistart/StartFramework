package start.application.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.core.beans.BeanDefinition;
import start.application.web.action.Action;
import start.application.web.action.ActionResult;
import start.application.web.action.ActionSupport;
import start.application.web.annotation.Controller;
import start.application.web.context.ContextLoaderListener;
import start.application.web.context.WebApplicationContext;
import start.application.web.interceptor.InterceptorHandler;


/**
 * URL解析触发类
 * @author zhenyao
 *
 */
public final class ActionDispatcher {
	
	private WebApplicationContext mWebApplicationContext;
	
	public ActionDispatcher() {
		mWebApplicationContext=ContextLoaderListener.getmWebApplicationContext();
	}
	
	/**
	 * 解析
	 * 
	 * @param name
	 *            控制层的Action别名
	 */
	public void start(HttpServletRequest request, HttpServletResponse response,String name) throws Exception {
		BeanDefinition bean = mWebApplicationContext.getBeanDefinitionInfoByName(name);
		// 只允许访问@Controller的Bean
		if (!bean.getPrototype().isAnnotationPresent(Controller.class)) {
			throw new  IllegalAccessException(name+"无访问权限" );
		}
		Action action = (Action)mWebApplicationContext.getBean(bean.getName());
		ActionSupport support=new ActionSupport(request,response,action);
		if(!mWebApplicationContext.getInterceptors().isEmpty()){
			//责任链模式执行拦截器
			InterceptorHandler handler=null;
			Iterator<String> interceptors = mWebApplicationContext.getInterceptors().iterator();
			while(interceptors.hasNext()){
				InterceptorHandler currentHandler=(InterceptorHandler)mWebApplicationContext.getBean(interceptors.next());
				currentHandler.setHandler(handler);
				handler=currentHandler;
			}
			//执行拦截器
			handler.intercept(support);
		}
		//执行Action
		ActionResult result=action.execute(support);
		if (result != null) {
			result.doExecute(support);
		}
	}
	
}