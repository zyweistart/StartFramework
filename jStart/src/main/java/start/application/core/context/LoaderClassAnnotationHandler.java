package start.application.core.context;

import start.application.core.beans.factory.ApplicationContext;

public abstract class LoaderClassAnnotationHandler extends AbstractLoaderClassAnnotationHandler implements LoaderClassAnnotation {

	public void doLoaderAnnotation(ApplicationContext applicationContext,Class<?> prototype){
		if(getHandler()!=null){
			getHandler().load(applicationContext,prototype);
		}
	}
	
}
