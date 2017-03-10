package start.application.web.context;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;

public class ContextLoaderListener implements ServletContextListener {
	
	private final static Logger log=LoggerFactory.getLogger(ContextLoaderListener.class);

	private WebApplicationContext mWebApplicationContext;
	
	public ContextLoaderListener() {
		mWebApplicationContext=new WebApplicationContext();
	}

	/**
	 * Initialize the root web application context.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		long start=System.currentTimeMillis();
		this.mWebApplicationContext.start();
		long value=System.currentTimeMillis()-start;
		log.info("解析配置文件及扫描包总花费时间："+value+" ms");
	}


	/**
	 * Close the root web application context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			this.mWebApplicationContext.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
