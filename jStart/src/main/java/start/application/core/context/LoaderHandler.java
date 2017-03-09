package start.application.core.context;

import start.application.core.Aware;

public abstract class LoaderHandler extends AbstractLoaderHandler implements LoaderContext,Aware {

	public void doLoadContext(Class<?> prototype){
		if(getHandler()!=null){
			getHandler().load(prototype);
		}
	}
	
}
