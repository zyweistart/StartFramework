package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.core.exceptions.ApplicationException;

public class BeanDefinition {
	
	private String name;
	
	private String prototype;
	
	private String init;
	
	private String destory;
	
	private Map<String,String> values;
	
	private Map<String,String> refs;
	
	private String beanContextName;
	
	private boolean sington=true;

	public String getName() {
		if(name==null){
			//默认name为当前类的全名
			name=getClassName();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getPrototype() {
		try {
			return Class.forName(getClassName());
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		}
	}
	
	public String getClassName() {
		return prototype;
	}

	public void setClass(String prototype) {
		this.prototype = prototype;
	}

	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public String getDestory() {
		return destory;
	}

	public void setDestory(String destory) {
		this.destory = destory;
	}
	
	public boolean isSington() {
		return sington;
	}

	public void setSington(boolean sington) {
		this.sington = sington;
	}

	public Map<String,String> getValues() {
		if(values==null){
			values=new HashMap<String,String>();
		}
		return values;
	}

	public void setValues(Map<String,String> values) {
		this.values = values;
	}

	public Map<String,String> getRefs() {
		if(refs==null){
			refs=new HashMap<String,String>();
		}
		return refs;
	}

	public void setRefs(Map<String,String> refs) {
		this.refs = refs;
	}

	public  String getBeanContextName() {
		return beanContextName;
	}

	public void setBeanContextName( String beanContextName) {
		this.beanContextName = beanContextName;
	}
	
}
