package start.application.core.context;

import start.application.core.annotation.Component;
import start.application.core.annotation.Repository;
import start.application.core.annotation.Service;
import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.ApplicationContext;

public class BeanLoaderContext extends LoaderClassAnnotationHandler{

	@Override
	public void load(ApplicationContext applicationContext,Class<?> prototype) {
		//注册Bean
		BeanDefinition bean=analysisBean(prototype);
		if(bean!=null){
			applicationContext.registerBeanDoManagerCenter(bean);
			return;
		}
		this.doLoaderAnnotation(applicationContext,prototype);
	}
	
	/**
	 * 解析Bean对象
	 * @param prototype
	 * @return
	 */
	public static BeanDefinition analysisBean(Class<?> prototype){
		BeanDefinition bean=null;
		// 组件
		Component component = prototype.getAnnotation(Component.class);
		if (component != null) {
			bean=new BeanDefinition();
			if(!component.value().isEmpty()){
				bean.setName(component.value());
			}
			bean.setClass(prototype.getName());
		}
		// 服务层
		Service service = prototype.getAnnotation(Service.class);
		if (service != null) {
			bean=new BeanDefinition();
			bean.setName(service.value());
			bean.setClass(prototype.getName());
		}
		// 数据访问层
		Repository repository = prototype.getAnnotation(Repository.class);
		if (repository != null) {
			bean=new BeanDefinition();
			bean.setName(repository.value());
			bean.setClass(prototype.getName());
		}
		return bean;
	}

}
