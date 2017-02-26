package start.application.core.beans.factory;

public interface InitializingBean {
	
	void afterPropertiesSet() throws Exception;

}
