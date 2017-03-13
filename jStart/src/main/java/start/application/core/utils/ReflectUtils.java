package start.application.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import start.application.core.exceptions.ApplicationException;
import start.application.core.ioc.PackingValueImpl;

public class ReflectUtils {

	private static PackingValueImpl mPackingValueImpl= new PackingValueImpl();
	
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

	public static void iocObjectParameter(Object instance,Map<String,String> params){
		iocObjectParameter(instance, params,mPackingValueImpl);
	}
	
	public static void iocObjectParameter(Object instance,Map<String,String> params,PackingValueImpl impl){
		Map<String,String> sParams=new HashMap<String,String>();
		Map<String,Map<String,String>> cParams=new HashMap<String,Map<String,String>>();
		for(String key:params.keySet()){
			String value=params.get(key);
			int index=key.indexOf(46);
			if(index>0){
				String newParamName=key.substring(0,index);
				Map<String,String> newParams=cParams.get(newParamName);
				if(newParams==null){
					newParams=new HashMap<String,String>();
				}
				newParams.put(key.substring(index+1),value);
				cParams.put(newParamName, newParams);
			}else{
				sParams.put(key,value);
			}
		}
		
		Class<?> prototype=instance.getClass();
		while (true) {
			if (prototype == null||prototype.equals(Object.class)) {
				break;
			}
			for(Method method:prototype.getDeclaredMethods()){
				String methodName=method.getName();
				if(methodName.startsWith("set")){
					if(method.getParameterTypes().length!=1){
						continue;
					}
					Class<?> type=method.getParameterTypes()[0];
					String name=methodName.substring(3,4).toLowerCase()+methodName.substring(4);
					if(sParams.containsKey(name)){
						Field field=null;
						try {
							field=prototype.getDeclaredField(name);
						} catch (NoSuchFieldException | SecurityException e) {
						}
						Object value=impl.getValue(field,null,type,sParams.get(name));
						if(value==null){
							continue;
						}
						try {
							method.invoke(instance, value);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new ApplicationException(e);
						}
					}else if(cParams.containsKey(name)){
						Object childObj=impl.newInstance(type, name);
						iocObjectParameter(childObj,cParams.get(name));
						try {
							method.invoke(instance, childObj);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new ApplicationException(e);
						}
					}
				}
			}
			prototype = prototype.getSuperclass();
		}
	}
	
	/**
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
//		Class<?> superClass=c.getSuperclass();
//		if(superClass==null){
//			return false;
//		}
//		if(superClass.equals(cls)){
//			return true;
//		}else{
//			return isSuperClass(superClass, cls);
//		}
		return cls.isAssignableFrom(c);
	}

}
