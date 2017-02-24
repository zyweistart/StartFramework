package start.application.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.Container;

public class ContextLoaderListener extends ContextLoader implements ServletContextListener {
	
	private final static Logger log=LoggerFactory.getLogger(ContextLoaderListener.class);

	private Container mContainer;
	
	public ContextLoaderListener() {
		mContainer=new Container();
	}

	/**
	 * Initialize the root web application context.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		long start=System.currentTimeMillis();
		mContainer.init();
		long value=System.currentTimeMillis()-start;
		log.info("解析配置文件及扫描包总花费时间："+value+" ms");
	}


	/**
	 * Close the root web application context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		mContainer.close();
	}
	
}
