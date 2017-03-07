package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ApplicationContext;

public class BeanBuilderFactory {
	
	private final static Logger log=LoggerFactory.getLogger(BeanBuilderFactory.class);

	private static Map<String,BeanBuilder> CONTEXTMAP=new HashMap<String,BeanBuilder>();
	
	public static void registerContext(Class<?> prototype){
		String name=prototype.getName();
		if(CONTEXTMAP.containsKey(name)){
			throw new IllegalArgumentException(name+"容器对象已存在!");
		}
		try(ApplicationContext application=new ApplicationContext();) {
			CONTEXTMAP.put(name, (BeanBuilder)application.getBean(prototype));
			log.info("BeanBuilder容器对象："+prototype.getName()+",初始化完成");
		}
	}
	
	public static BeanBuilder getContext(String contextName){
		if(contextName==null){
			return null;
		}
		if(CONTEXTMAP.containsKey(contextName)){
			return CONTEXTMAP.get(contextName);
		}
		return null;
	}
	
}
