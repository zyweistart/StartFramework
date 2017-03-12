package start.application.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import start.application.core.ApplicationIO;
import start.application.core.exceptions.ApplicationException;
import start.application.core.ioc.PackingValueImpl;

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

	public static void iocObjectParameter(Object instance,Map<String,String> params){
		iocObjectParameter(instance, params,new PackingValueImpl());
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
		for(Method method:prototype.getMethods()){
			String methodName=method.getName();
			if(methodName.startsWith("set")){
				if(method.getParameterTypes().length!=1){
					continue;
				}
				Class<?> type=method.getParameterTypes()[0];
				String name=methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				if(sParams.containsKey(name)){
					if(!ApplicationIO.isDataTypeSupport(type)){
						continue;
					}
					Object value=impl.getValue(null,method,type,sParams.get(name));
					if(value==null){
						continue;
					}
					try {
						method.invoke(instance, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ApplicationException(e);
					}
				}else if(cParams.containsKey(name)){
					Object childObj=null;
					try {
						childObj=type.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new ApplicationException(e);
					}
					iocObjectParameter(childObj,cParams.get(name));
					try {
						method.invoke(instance, childObj);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ApplicationException(e);
					}
				}
			}
		}
	}

//	public static void injectParameter(Object entity,Map<String,String> bundle) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ParseException{
//		Map<String,String> fieldMap=new HashMap<String,String>();
//		Map<String,Map<String,String>> clsMap=new HashMap<String,Map<String,String>>();
//		for(String key:bundle.keySet()){
//			String[] params=key.split("\\.");
//			if(params.length==1){
//				fieldMap.put(key, bundle.get(key));
//			}else if(params.length==2){
//				Map<String,String> vMap=clsMap.get(params[0]);
//				if(vMap==null){
//					vMap=new HashMap<String,String>();
//				}
//				vMap.put(params[1], bundle.get(key));
//				clsMap.put(params[0], vMap);
//			}else{
//				String message=Message.getMessage(Message.PM_4006, key);
//				throw new ActionException(message);
//			}
//		}
//		injectObject(entity, fieldMap, clsMap);
//	}
//	
//	private static void injectObject(Object entity,Map<String,String> fieldMap,Map<String,Map<String,String>> clsMap) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ParseException{
//		Field[] fields=entity.getClass().getDeclaredFields();
//		for(Field field:fields){
//			if(!ApplicationIO.isDataTypeSupport(field.getType())){
//				continue;
//			}
//			//已注入没有注解的字段
////			if(field.getAnnotations().length>0){
////				continue;
////			}
//			String fieldName=field.getName();
//			String value=fieldMap.get(fieldName);
//			if(value!=null){
//				field.setAccessible(true);
//				field.set(entity, ApplicationIO.read(field,value));
//			}else if(clsMap!=null&&clsMap.size()>0){
//				Map<String,String> vData=clsMap.get(fieldName);
//				if(vData!=null){
//					Object cEntity=field.getType().newInstance();
//					injectObject(cEntity, vData, null);
//					field.setAccessible(true);
//					field.set(entity, cEntity);
//				}
//			}
//		}
//	}
	
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
