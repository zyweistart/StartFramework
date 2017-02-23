package start.application.core.beans;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StackTraceInfo;

public class BeanContext implements BeanBuilder {
	
	private final static Logger log=LoggerFactory.getLogger(BeanContext.class);

	@Override
	public Object getBean(BeanInfo bean){
		try {
			return bean.getPrototype().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new ApplicationException(e);
		}
	}
	
}
