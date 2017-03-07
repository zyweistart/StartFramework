package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;

public class BeanBuilderFactory {
	
	private final static Logger log=LoggerFactory.getLogger(BeanBuilderFactory.class);

	private static Map<String,BeanBuilder> CONTEXTMAP=new HashMap<String,BeanBuilder>();
	
	public static void registerContext(Class<?> prototype){
		String name=prototype.getName();
		if(CONTEXTMAP.containsKey(name)){
			throw new IllegalArgumentException(name+"容器对象已存在!");
		}
		try {
			CONTEXTMAP.put(name, (BeanBuilder)prototype.newInstance());
			log.info("BeanBuilder容器对象："+prototype.getName()+",初始化完成");
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
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
