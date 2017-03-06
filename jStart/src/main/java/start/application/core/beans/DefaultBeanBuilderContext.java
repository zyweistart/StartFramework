package start.application.core.beans;

import java.util.List;

import start.application.core.exceptions.ApplicationException;

/**
 * 默认Bean生成容器
 * @author zhenyao
 *
 */
public class DefaultBeanBuilderContext implements BeanBuilder {

	@Override
	public List<String> register() {
		//默认Bean容器无须返回注册类列表
		return null;
	}
	
	@Override
	public Object getBean(BeanDefinition bean){
		try {
			return bean.getPrototype().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}
	
}
