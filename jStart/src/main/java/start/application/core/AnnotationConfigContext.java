package start.application.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import start.application.core.beans.factory.ApplicationContext;
import start.application.core.context.BeanLoaderContext;
import start.application.core.context.LoaderHandler;
import start.application.orm.context.OrmLoaderContext;
import start.application.web.context.WebLoaderContext;

public class AnnotationConfigContext {

	private ApplicationContext mApplication;
	private LoaderHandler handler = null;
	private static List<LoaderHandler> loaders = new ArrayList<LoaderHandler>();
	
	static{
		// 1、Bean对象解析
		loaders.add(new BeanLoaderContext());
		// 2、Web控制器解析
		loaders.add(new WebLoaderContext());
		// 3、ORM实体解析
		loaders.add(new OrmLoaderContext());
	}
	
	public AnnotationConfigContext(ApplicationContext applicationContext){
		this.mApplication=applicationContext;
		Iterator<LoaderHandler> interceptors = loaders.iterator();
		while (interceptors.hasNext()) {
			LoaderHandler currentHandler = interceptors.next();
			currentHandler.setHandler(handler);
			handler = currentHandler;
		}
	}
	
	public void load(Class<?> prototype){
		// 不解析接口类
		if (prototype.isInterface()) {
			return;
		}
		handler.load(this.mApplication,prototype);
		handler.reset();
	}
	
}
