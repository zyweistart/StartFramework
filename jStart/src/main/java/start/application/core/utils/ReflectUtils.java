package start.application.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import start.application.core.exceptions.ApplicationException;

public class ReflectUtils {

	public static void invokeMethod(Object instance, String methodName) {
		if (StringHelper.isEmpty(methodName)) {
			return;
		}
		try {
			Method method = instance.getClass().getMethod(methodName);
			method.invoke(instance);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ApplicationException(e);
		}
	}

	/*
	 * 判断类是否实现了某接口
	 */
	public static boolean isInterface(Class<?> c, Class<?> szInterface) {
		Class<?>[] face = c.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].equals(szInterface)) {
				return true;
			} else {
				Class<?>[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face1[x].equals(szInterface)) {
						return true;
					} else if (isInterface(face1[x], szInterface)) {
						return true;
					}
				}
			}
		}
		if (null != c.getSuperclass()) {
			return isInterface(c.getSuperclass(), szInterface);
		}
		return false;
	}
	
	public static boolean isSuperClass(Class<?> c, Class<?> cls) {
		Class<?> superClass=c.getSuperclass();
		if(superClass==null){
			return false;
		}
		if(superClass.equals(cls)){
			return true;
		}else{
			return isSuperClass(superClass, cls);
		}
	}

}
