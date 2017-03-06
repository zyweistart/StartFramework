package start.application.core.beans;

import java.util.List;

public interface BeanBuilder {

	/**
	 * 注册相应的类名到当前Bean生成中心
	 */
	List<String> register();
	
	/**
	 * 根据类名获取对象
	 * @param bean
	 * @return
	 */
	Object getBean(BeanDefinition bean);

}
