package start.application.core.context;

public abstract class AbstractLoaderClassAnnotationHandler {
	
	private LoaderClassAnnotation firstHandler;
	private LoaderClassAnnotation handler;

	public void reset() {
		setHandler(this.firstHandler);
	}

	public LoaderClassAnnotation getHandler() {
		return handler;
	}

	public void setHandler(LoaderClassAnnotation handler) {
		if(getHandler()==null){
			this.firstHandler=handler;
		}
		this.handler = handler;
	}
	
}
