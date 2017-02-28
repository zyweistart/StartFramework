package start.application.core.beans;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;

public class BeanContextFactory {
	
	private final static Logger log=LoggerFactory.getLogger(BeanContextFactory.class);
	
	private static BeanBuilder builder;
	
	public static void init(){
		init(BeanContext.class);
	}
	
	public static void init(Class<?> prototype){
		if(builder==null){
			try {
				builder=(BeanBuilder)prototype.newInstance();
				log.info("BeanBuilder容器对象："+prototype.getName()+",初始化完成");
			} catch (InstantiationException | IllegalAccessException e) {
				throw new ApplicationException(e);
			}
		}
	}

	public static BeanBuilder getInstanceBuilder() {
		if(builder==null){
			throw new NullPointerException("请调用init方法，初始化Bean容器");
		}
		return builder;
	}
	
}
