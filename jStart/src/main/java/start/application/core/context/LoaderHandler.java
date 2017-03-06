package start.application.core.context;

public abstract class LoaderHandler extends AbstractLoaderHandler implements LoaderContext {

	public void doLoadContext(Class<?> prototype){
		if(getHandler()!=null){
			getHandler().load(prototype);
		}
	}
	
}
