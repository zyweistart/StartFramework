package start.application.context.config;

import java.util.Map;

import start.application.core.beans.BeanInfo;

public interface ConfigImpl {
	
	void read(String tagName,Map<String,String> attributes,Map<String,String> propertys);
	void readBean(BeanInfo bean);
	void finish();
	
}
