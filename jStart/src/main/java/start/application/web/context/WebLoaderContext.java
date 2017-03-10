package start.application.web.context;

import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.ApplicationContext;
import start.application.core.constant.Message;
import start.application.core.context.LoaderClassAnnotationHandler;
import start.application.core.exceptions.AnnoationError;
import start.application.core.utils.ReflectUtils;
import start.application.web.action.Action;
import start.application.web.annotation.Controller;

public class WebLoaderContext extends LoaderClassAnnotationHandler {
	
	@Override
	public void load(ApplicationContext applicationContext,Class<?> prototype) {
		//2.2注册Bean
		BeanDefinition bean=analysisBean(prototype);
		if(bean!=null){
			applicationContext.registerBeanDoManagerCenter(bean);
			return;
		}
		doLoaderAnnotation(applicationContext,prototype);
	}
	
	/**
	 * 解析Bean对象
	 * @param prototype
	 * @return
	 */
	public static BeanDefinition analysisBean(Class<?> prototype){
		BeanDefinition bean=null;
		// 控制层
		Controller controller = prototype.getAnnotation(Controller.class);
		if (controller != null) {
			if(!ReflectUtils.isInterface(prototype, Action.class)){
				String message = Message.getMessage(Message.PM_4005, prototype.getName());
				throw new AnnoationError(message);
			}
			bean=new BeanDefinition();
			bean.setName(controller.value());
			bean.setPrototype(prototype.getName());
		}
		return bean;
	}

}
