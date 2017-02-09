package start.application.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.exceptions.ApplicationException;

public class ReflectUtils {
	
	private final static Logger log=LoggerFactory.getLogger(ReflectUtils.class);
	
	public static void invokeMethod(Object instance,Class<?> prototype,String methodName){
		if(StringHelper.isEmpty(methodName)){
			return;
		}
		try {
			Method method=prototype.getMethod(methodName);
			method.invoke(instance);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new ApplicationException(e);
		}
	}
	
}
