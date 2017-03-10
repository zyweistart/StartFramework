package start.application.web;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.config.ConstantConfig;
import start.application.web.action.Action;
import start.application.web.action.ActionResult;
import start.application.web.action.ActionSupport;
import start.application.web.annotation.Controller;
import start.application.web.context.WebApplicationContext;
import start.application.web.interceptor.InterceptorHandler;


/**
 * URL解析触发类
 * @author zhenyao
 *
 */
public final class ActionDispatcher implements Closeable {
	
	private final static Logger log=LoggerFactory.getLogger(ActionDispatcher.class);
	
	private static WebApplicationContext mWebApplicationContext;
	
	static{
		mWebApplicationContext=new WebApplicationContext();
		long start=System.currentTimeMillis();
		mWebApplicationContext.start();
		long value=System.currentTimeMillis()-start;
		log.info("解析配置文件及扫描包总花费时间："+value+" ms");
	}
	
	public ActionDispatcher() {
		
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
		// 请求编码设置防止乱码
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(ConstantConfig.ENCODING);
		}
		Action action = (Action)mWebApplicationContext.getBean(bean.getName());
		ActionSupport support=new ActionSupport(
				action,
				request,
				response);
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
			// 返回值必须实现了ActionResult接口
			result.doExecute(support);
		}
	}

	@Override
	public void close() throws IOException {
		mWebApplicationContext.close();
	}
	
}