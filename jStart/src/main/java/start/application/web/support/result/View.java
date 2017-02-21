package start.application.web.support.result;

import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.web.result.ActionResult;
import start.application.web.result.ActionResultInvocation;


/**
 * 转向
 */
public class View implements ActionResult {
	
	private final static Logger log=LoggerFactory.getLogger(View.class);
	
	private String dispatcherPage;
	
	public View(String dispatcherPage){
		this.dispatcherPage=dispatcherPage;
	}
	
	@Override
	public void doExecute(ActionResultInvocation invocation) {
		try{
			HttpServletRequest request=invocation.getAction().request();
			HttpServletResponse response=invocation.getAction().response();
			//设置Request作用域的值
			for(Method method:invocation.getAction().getClass().getMethods()){
				String methodName=method.getName();
				if(methodName.startsWith("get")||methodName.startsWith("is")){
					String fieldName=null;
					if(methodName.startsWith("is")){
						fieldName=methodName.substring(2,3).toLowerCase()+methodName.substring(3);
					}else  if(methodName.startsWith("get")){
						fieldName=methodName.substring(3,4).toLowerCase()+methodName.substring(4);
					}
					if(fieldName!=null){
						//只添加不存在的键值
						if(request.getAttribute(fieldName)==null){
							request.setAttribute(fieldName,method.invoke(invocation.getAction()));
						}
					}
				}
			}
			RequestDispatcher requestDispatcher=request.getRequestDispatcher(getDispatcherPage());
			requestDispatcher.forward(request,response);
		}catch(Exception e){
			e.printStackTrace();
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
		}
	}

	public String getDispatcherPage() {
		return dispatcherPage;
	}
	
}
