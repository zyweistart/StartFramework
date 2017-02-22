package start.application.core.beans;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StackTraceInfo;

public class BeanContextFactory implements BeanProvider {
	
	private final static Logger log=LoggerFactory.getLogger(BeanContextFactory.class);
	
	private static Class<?> prototype=BeanContext.class;
	
	public static void setPrototype(Class<?> prototype){
		BeanContextFactory.prototype=prototype;
	}

	@Override
	public BeanBuilder produce() {
		try {
			return (BeanBuilder) BeanContextFactory.prototype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new ApplicationException(e);
		}
	}
	
}
