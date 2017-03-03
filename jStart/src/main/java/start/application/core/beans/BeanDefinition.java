package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.core.annotation.Scope;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.StringHelper;

public class BeanDefinition {
	
	public BeanDefinition(){
		values=new HashMap<String,String>();
		refs=new HashMap<String,String>();
		attributes=new HashMap<String,String>();
	}
	
	private String name;
	
	private String prototype;
	
	private String init;
	
	private String destory;
	
	private Boolean singleton;
	
	private Map<String,String> values;
	
	private Map<String,String> refs;
	
	private Map<String,String> attributes;

	public String getName() {
		if(name==null){
			name=getAttributes().get("name");
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getPrototype() {
		try {
			return Class.forName(getPrototypeString());
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		}
	}
	
	public String getPrototypeString() {
		if(prototype==null){
			prototype=getAttributes().get("class");
		}
		return prototype;
	}

	public void setPrototype(String prototype) {
		this.prototype = prototype;
	}

	public String getInit() {
		if(init==null){
			init=getAttributes().get("init");
		}
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public String getDestory() {
		if(destory==null){
			destory=getAttributes().get("destory");
		}
		return destory;
	}

	public void setDestory(String destory) {
		this.destory = destory;
	}

	public Map<String,String> getValues() {
		return values;
	}

	public void setValues(Map<String,String> values) {
		this.values = values;
	}

	public Map<String,String> getRefs() {
		return refs;
	}

	public void setRefs(Map<String,String> refs) {
		this.refs = refs;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public Boolean isSingleton(){
		if(singleton==null){
			if(getPrototype().isAnnotationPresent(Scope.class)){
				singleton=false;
			}else{
				String sing=getAttributes().get("singleton");
				if(StringHelper.isEmpty(sing)){
					//默认为单例
					singleton=true;
				}else{
					singleton=StringHelper.nullToBoolean(sing);
				}
			}
		}
		return singleton;
	}

	public void setSingleton(Boolean singleton) {
		this.singleton = singleton;
	}
	
	
}
