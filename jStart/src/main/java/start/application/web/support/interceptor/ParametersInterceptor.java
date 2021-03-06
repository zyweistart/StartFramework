package start.application.web.support.interceptor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import start.application.core.io.ApplicationIO;
import start.application.core.utils.StringHelper;
import start.application.web.action.ActionSupport;
import start.application.web.interceptor.InterceptorHandler;

/**
 * 非multipart/form-data提交方式拦截,各参数设置
 */
public class ParametersInterceptor extends InterceptorHandler {

	private final static String MULTIPARTFORMDATA="multipart/form-data";
	
	@Override
	public void intercept(ActionSupport support){
		try{
			HttpServletRequest request=support.request();
			if(!MULTIPARTFORMDATA.equals(request.getContentType())){
				Map<String,String> params=new HashMap<String,String>();
				Enumeration<String> enumerations = request.getParameterNames();
				while (enumerations.hasMoreElements()){
					String parameterName = enumerations.nextElement();
					String[] parameterValues=request.getParameterValues(parameterName);
					if(parameterValues.length==1){
						params.put(parameterName, parameterValues[0]);
					}else{
						params.put(parameterName, StringHelper.listToString(Arrays.asList(parameterValues)));
					}
				}
				ApplicationIO.iocObjectParameter(support.getAction(),params);
			}
		}finally{
			// 继续执行下一个拦截器
			doInterceptor(support);
		}
	}

}