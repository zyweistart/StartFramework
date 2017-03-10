package start.application.core.context;

import start.application.core.beans.factory.ApplicationContext;

public interface LoaderContext {
	
	void load(ApplicationContext applicationContext,Class<?> prototype) ;
	
}
