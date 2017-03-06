package start.application.core.context;

public abstract class AbstractLoaderHandler {
	
	private LoaderContext firstHandler;
	private LoaderContext handler;

	public void reset() {
		setHandler(this.firstHandler);
	}

	public LoaderContext getHandler() {
		return handler;
	}

	public void setHandler(LoaderContext handler) {
		if(getHandler()==null){
			this.firstHandler=handler;
		}
		this.handler = handler;
	}
	
}
