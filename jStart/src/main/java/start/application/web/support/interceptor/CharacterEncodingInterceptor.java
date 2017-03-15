package start.application.web.support.interceptor;

import java.io.UnsupportedEncodingException;

import start.application.core.config.ConstantConfig;
import start.application.web.action.ActionSupport;
import start.application.web.exceptions.ActionException;
import start.application.web.interceptor.InterceptorHandler;

public class CharacterEncodingInterceptor extends InterceptorHandler {

	@Override
	public void intercept(ActionSupport support) {
		try{
			// 请求编码设置防止乱码
			if (support.request().getCharacterEncoding() == null) {
				support.request().setCharacterEncoding(ConstantConfig.ENCODING);
			}
		} catch (UnsupportedEncodingException e) {
			throw new ActionException(e);
		}finally{
			doInterceptor(support);
		}
	}

}
