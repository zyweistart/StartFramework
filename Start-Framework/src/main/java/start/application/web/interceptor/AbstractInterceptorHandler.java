package start.application.web.interceptor;

public abstract class AbstractInterceptorHandler {
	
	public Interceptor handler;

	public Interceptor getHandler() {
		return handler;
	}

	public void setHandler(Interceptor handler) {
		this.handler = handler;
	}
	
}
