package start.application.core.beans;

import start.application.core.exceptions.ApplicationException;

public class BeanContext implements BeanBuilder {
	
	@Override
	public Object getBean(BeanDefinition bean){
		try {
			return bean.getPrototype().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}
	
}
