package start.application.support;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.Constant;
import start.application.core.config.ConfigInfo;
import start.application.core.config.ConstantConfig;
import start.application.core.utils.StringHelper;

public class XmlPathApplicationContext extends GenerateBeanManager {
	
	private final static Logger log=LoggerFactory.getLogger(XmlPathApplicationContext.class);

	private String[] configFiles;
	
	public XmlPathApplicationContext() {
		//默认配置文件
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
		ConfigInfo configInfo = new ConfigInfo(new XmlConfigAnalysis());
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
	}
	
}
