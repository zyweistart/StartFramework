package start.application.core.beans;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StackTraceInfo;

public class BeanContextFactory implements BeanProvider {
	
	private final static Logger log=LoggerFactory.getLogger(BeanContextFactory.class);
	
	private static String prototype="start.application.core.beans.BeanContext";
	
	public static void setPrototype(String prototypeName){
		BeanContextFactory.prototype=prototypeName;
	}

	@Override
	public BeanBuilder produce() {
		try {
			return (BeanBuilder) Class.forName(BeanContextFactory.prototype).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new ApplicationException(e);
		}
	}
	
}
