package start.application.core.context;

import start.application.core.beans.factory.ApplicationContext;

public abstract class LoaderHandler extends AbstractLoaderHandler implements LoaderContext {

	public void doLoadContext(ApplicationContext applicationContext,Class<?> prototype){
		if(getHandler()!=null){
			getHandler().load(applicationContext,prototype);
		}
	}
	
}
