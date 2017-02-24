package start.application.web.support.interceptor;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.core.utils.StringHelper;
import start.application.web.action.ActionSupport;
import start.application.web.exceptions.ActionException;
import start.application.web.interceptor.InterceptorHandler;

/**
 * 非multipart/form-data提交方式拦截,各参数设置
 */
public class ParametersInterceptor extends InterceptorHandler {

	private final static String MULTIPARTFORMDATA="multipart/form-data";
	private final static Logger log=LoggerFactory.getLogger(ParametersInterceptor.class);
	
	@Override
	public void intercept(ActionSupport support) throws Exception{
		HttpServletRequest request=support.request();
		try{
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
				RequestParameterInject.injectParameter(support.getAction(),params);
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | ParseException e) {
			log.error(StackTraceInfo.getTraceInfo()+e.getMessage());
			throw new ActionException(e);
		}finally{
			// 继续执行下一个拦截器
			doInterceptor(support);
		}
	}

}