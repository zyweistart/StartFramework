package start.application.core.context;

import start.application.core.annotation.Component;
import start.application.core.annotation.Repository;
import start.application.core.annotation.Service;
import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.ApplicationContext;

public class BeanLoaderContext extends LoaderHandler{

	@Override
	public void load(ApplicationContext applicationContext,Class<?> prototype) {
		//组件不归入Bean容器管理
		Component component = prototype.getAnnotation(Component.class);
		if (component != null) {
			BeanDefinition bean=new BeanDefinition();
			bean.setPrototype(prototype.getName());
			applicationContext.registerBeanDoManagerCenter(bean);
			return;
		}
		//注册Bean
		BeanDefinition bean=analysisBean(prototype);
		if(bean!=null){
			applicationContext.registerBeanDoManagerCenter(bean);
			return;
		}
		this.doLoadContext(applicationContext,prototype);
	}
	
	/**
	 * 解析Bean对象
	 * @param prototype
	 * @return
	 */
	public static BeanDefinition analysisBean(Class<?> prototype){
		BeanDefinition bean=null;
		// 服务层
		Service service = prototype.getAnnotation(Service.class);
		if (service != null) {
			bean=new BeanDefinition();
			bean.setName(service.value());
			bean.setPrototype(prototype.getName());
		}
		// 数据访问层
		Repository repository = prototype.getAnnotation(Repository.class);
		if (repository != null) {
			bean=new BeanDefinition();
			bean.setName(repository.value());
			bean.setPrototype(prototype.getName());
		}
		return bean;
	}

}
