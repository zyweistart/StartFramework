package start.application.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.ApplicationIO;
import start.application.core.exceptions.ApplicationException;

public class PackingValueImpl {
	
	public Object newInstance(Class<?> type,String name){
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}

	public Object getValue(Field field,Method method,Class<?> type,String key){
		return ApplicationIO.read(field, method, type, key);
	}
	
}
