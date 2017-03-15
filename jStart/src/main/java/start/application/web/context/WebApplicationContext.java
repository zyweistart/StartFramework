package start.application.web.context;

import java.util.ArrayList;
import java.util.List;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.utils.ReflectUtils;
import start.application.orm.OrmApplicationContext;
import start.application.web.interceptor.InterceptorHandler;

/**
 * Web容器
 * @author zhenyao
 *
 */
public class WebApplicationContext extends OrmApplicationContext {
	
	private final static Logger log=LoggerFactory.getLogger(WebApplicationContext.class);

	private List<String> interceptors=new ArrayList<String>();
	
	@Override
	public void registerBeanDoManagerCenter(BeanDefinition bean){
		super.registerBeanDoManagerCenter(bean);
		//拦截器
		if(ReflectUtils.isSuperClass(bean.getPrototype(),InterceptorHandler.class)){
			registerInterceptors(bean.getClassName());
		}
	}
	
	/**
	 * 获取拦截器对象列表
	 */
	public List<String> getInterceptors() {
		return interceptors;
	}
	
	/**
	 * 拦截器列表
	 */
	public void registerInterceptors(String name) {
		if(name!=null){
			if(!isBeanDefinitionExistence(name)){
				throw new IllegalArgumentException("拦截器:"+name+"未注册为Bean对象");
			}
			interceptors.add(name);
			log.info("Web拦截器Interceptor类："+name+"，加载成功~~~");
		}
	}
	
}
