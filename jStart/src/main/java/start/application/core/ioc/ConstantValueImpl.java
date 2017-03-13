package start.application.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.ApplicationIO;
import start.application.core.GenerateBeanManager;
import start.application.core.config.ConstantConfig;

public class ConstantValueImpl extends BeanValueImpl {

	public ConstantValueImpl(GenerateBeanManager manager) {
		super(manager);
	}

	@Override
	public Object getValue(Field field,Method method,Class<?> type,String key) {
		return ApplicationIO.read(field,method,type,ConstantConfig.get(key));
	}

}
