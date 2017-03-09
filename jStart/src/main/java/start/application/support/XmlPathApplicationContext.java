package start.application.support;

import java.io.Closeable;
import java.io.IOException;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.Constant;
import start.application.core.config.ConfigInfo;
import start.application.core.config.ConstantConfig;
import start.application.core.utils.StringHelper;

public class XmlPathApplicationContext implements Closeable {
	
	private final static Logger log=LoggerFactory.getLogger(XmlPathApplicationContext.class);

	private String[] configFiles;
	private BeanManager mBeanManager;
	
	public XmlPathApplicationContext() {
		this(new String[] { "StartConfig" });
	}

	public XmlPathApplicationContext(String configFile) {
		this(new String[] { configFile });
	}

	public XmlPathApplicationContext(String... configFiles) {
		this.configFiles = configFiles;
	}

	public void start() {
		// 1、解析配置文件
		ConfigInfo configInfo = new ConfigInfo(new XmlApplicationConfigAnalysis());
		for(String xmlfile:configFiles){
			configInfo.loadConfigFile(xmlfile);
		}
		if (StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)) {
			log.warn("扫描的类路径为空，请配置CLASSSCANPATH常量,需要扫描的类路径");
			return;
		}
		//2、扫描类路径
		ScannerClassPath scanner=new ScannerClassPath(ConstantConfig.CLASSSCANPATH.split(Constant.COMMA));
		scanner.doScanner();
		//3、创建Bean管理容器
		mBeanManager=new BeanManager();
	}
	
	public Object getBean(String name){
		return mBeanManager.getBean(name);
	}

	public Object getBean(Class<?> prototype){
		return mBeanManager.getBean(prototype);
	}

	@Override
	public void close() throws IOException {
		mBeanManager.close();
	}
	
}
