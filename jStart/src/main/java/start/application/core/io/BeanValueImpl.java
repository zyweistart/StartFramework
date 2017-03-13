package start.application.core.io;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import start.application.core.GenerateBeanManager;

public class BeanValueImpl extends PackingValueImpl{
	
	private GenerateBeanManager manager;
	
	public BeanValueImpl(GenerateBeanManager manager){
		this.manager=manager;
	}
	
	public Object newInstance(Class<?> type,String name){
		if(this.manager.isBeanDefinitionExistence(type.getName())){
			return this.manager.getBean(type);
		}else{
			return super.newInstance(type, name);
		}
	}

	@Override
	public Object getValue(Field field,Method method,Class<?> type,String key) {
		return manager.getBean(key);
	}
	
}
