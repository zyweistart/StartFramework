package start.application.context;

import start.application.context.annotation.Controller;
import start.application.context.annotation.Repository;
import start.application.context.annotation.Scope;
import start.application.context.annotation.Service;
import start.application.core.beans.BeanInfo;

public class AnnotationConfigApplicationContext {
	
	public static BeanInfo getBeanInfo(Class<?> prototype){
		// 控制层
		Controller controller = prototype.getAnnotation(Controller.class);
		if (controller != null) {
			return buildBean(controller.value(),controller.init(),controller.destory(),prototype);
		}
		// 服务层
		Service service = prototype.getAnnotation(Service.class);
		if (service != null) {
			return buildBean(service.value(),service.init(),service.destory(),prototype);
		}
		// 数据访问层
		Repository repository = prototype.getAnnotation(Repository.class);
		if (repository != null) {
			return buildBean(repository.value(),repository.init(),repository.destory(),prototype);
		}
		return null;
	}
	
	public static BeanInfo buildBean(String name,String init,String destory,Class<?> prototype){
		BeanInfo bean=new BeanInfo();
		bean.setName(name);
		bean.setPrototype(prototype.getName());
		bean.setInit(init);
		bean.setDestory(destory);
		if(prototype.isAnnotationPresent(Scope.class)){
			bean.setSingleton(false);
		}
		return bean;
	}
	
}
