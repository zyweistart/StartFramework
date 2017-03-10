package start.application.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import start.application.context.ApplicationIO;
import start.application.core.constant.Message;
import start.application.core.exceptions.ApplicationException;
import start.application.web.exceptions.ActionException;

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

	public static void injectParameter(Object entity,Map<String,String> bundle) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ParseException{
		Map<String,String> fieldMap=new HashMap<String,String>();
		Map<String,Map<String,String>> clsMap=new HashMap<String,Map<String,String>>();
		for(String key:bundle.keySet()){
			String[] params=key.split("\\.");
			if(params.length==1){
				fieldMap.put(key, bundle.get(key));
			}else if(params.length==2){
				Map<String,String> vMap=clsMap.get(params[0]);
				if(vMap==null){
					vMap=new HashMap<String,String>();
				}
				vMap.put(params[1], bundle.get(key));
				clsMap.put(params[0], vMap);
			}else{
				String message=Message.getMessage(Message.PM_4006, key);
				throw new ActionException(message);
			}
		}
		injectObject(entity, fieldMap, clsMap);
	}
	
	private static void injectObject(Object entity,Map<String,String> fieldMap,Map<String,Map<String,String>> clsMap) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ParseException{
		Field[] fields=entity.getClass().getDeclaredFields();
		for(Field field:fields){
			if(!ApplicationIO.isDataTypeSupport(field)){
				continue;
			}
			//已注入没有注解的字段
//			if(field.getAnnotations().length>0){
//				continue;
//			}
			String fieldName=field.getName();
			String value=fieldMap.get(fieldName);
			if(value!=null){
				field.setAccessible(true);
				field.set(entity, ApplicationIO.read(field,value));
			}else if(clsMap!=null&&clsMap.size()>0){
				Map<String,String> vData=clsMap.get(fieldName);
				if(vData!=null){
					Object cEntity=field.getType().newInstance();
					injectObject(cEntity, vData, null);
					field.setAccessible(true);
					field.set(entity, cEntity);
				}
			}
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
