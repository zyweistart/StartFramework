package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

public class Context {
	
	private static Map<String,BeanBuilder> CONTEXTMAP=new HashMap<String,BeanBuilder>();
	
	public void registerContext(BeanBuilder context){
		String name=context.getClass().getName();
		if(CONTEXTMAP.containsKey(name)){
			throw new IllegalArgumentException(name+"容器对象已存在!");
		}
		CONTEXTMAP.put(name, context);
	}
	
	public BeanBuilder getContext(String name){
		if(CONTEXTMAP.containsKey(name)){
			return CONTEXTMAP.get(name);
		}
		throw new NullPointerException("找不到"+name+"容器对象！");
	}
	
}
