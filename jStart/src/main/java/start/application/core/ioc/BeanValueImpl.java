package start.application.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.GenerateBeanManager;

public class BeanValueImpl extends PackingValueImpl{
	
	private GenerateBeanManager manager;
	
	public BeanValueImpl(GenerateBeanManager manager){
		this.manager=manager;
	}

	@Override
	public Object getValue(Field field,Method method,Class<?> type,String key) {
		return manager.getBean(key);
	}
	
}
