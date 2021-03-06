package start.application.web.context;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;

public class ContextLoaderListener implements ServletContextListener {
	
	private final static Logger log=LoggerFactory.getLogger(ContextLoaderListener.class);

	private static WebApplicationContext mWebApplicationContext=new WebApplicationContext();

	public static WebApplicationContext getmWebApplicationContext() {
		return mWebApplicationContext;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		long start=System.currentTimeMillis();
		mWebApplicationContext.start();
		long value=System.currentTimeMillis()-start;
		log.info("解析配置文件及扫描包总花费时间："+value+" ms");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			mWebApplicationContext.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
