package start.application.core.context;

import start.application.core.beans.factory.ApplicationContext;
import start.application.core.beans.factory.Aware;

public abstract class LoaderHandler extends AbstractLoaderHandler implements LoaderContext,Aware {

	public void doLoadContext(ApplicationContext applicationContext,Class<?> prototype){
		if(getHandler()!=null){
			getHandler().load(applicationContext,prototype);
		}
	}
	
}
