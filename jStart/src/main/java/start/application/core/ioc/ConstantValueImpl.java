package start.application.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.config.ConstantConfig;

public class ConstantValueImpl extends PackingValueImpl {

	@Override
	public Object getValue(Field field,Method method,Class<?> type,String key) {
		return ConstantConfig.get(key);
	}

}
