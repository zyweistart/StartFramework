package start.application.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.ApplicationIO;

public class PackingValueImpl {

	public Object getValue(Field field,Method method,Class<?> type,String key){
		return ApplicationIO.read(null, method, type, key);
	}
	
}
