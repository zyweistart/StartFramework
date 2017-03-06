package start.application.context;

import start.application.core.beans.BeanDefinition;
import start.application.core.context.BeanLoaderContext;
import start.application.web.context.WebLoaderContext;

public class AnnotationContext {
	
	/**
	 * 解析Bean对象
	 * @param prototype
	 * @return
	 */
	public static BeanDefinition analysis(Class<?> prototype){
		BeanDefinition bean=WebLoaderContext.analysisBean(prototype);
		if(bean!=null){
			return bean;
		}
		return BeanLoaderContext.analysisBean(prototype);
	}

	
}
