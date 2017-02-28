package start.application.web.support.interceptor;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.context.ContextDataReadWrite;
import start.application.core.Message;
import start.application.web.exceptions.ActionException;
import start.application.web.support.interceptor.fileupload.UpLoadFile;


/**
 * 提交请求时，给Action类注入参数的辅助类
 * @author Start
 */
public final class RequestParameterInject {
	
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
			if(!ContextDataReadWrite.isDataTypeSupport(field)){
				continue;
			}
			//已注入没有注解的字段
			if(field.getAnnotations().length>0){
				continue;
			}
			String fieldName=field.getName();
			String value=fieldMap.get(fieldName);
			if(value!=null){
				field.setAccessible(true);
				field.set(entity, ContextDataReadWrite.convertReadIn(field,value));
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
	
	public static void injectObject(Object entity,Map<String,List<UpLoadFile>> fieldMap) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields=entity.getClass().getDeclaredFields();
		for(Field field:fields){
			//返回类型判断
			String fieldName=field.getName();
			List<UpLoadFile> value=fieldMap.get(fieldName);
			if(value!=null){
				field.setAccessible(true);
				field.set(entity, value);
			}
		}
	}
	
}