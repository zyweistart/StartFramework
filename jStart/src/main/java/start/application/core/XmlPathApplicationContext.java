package start.application.core;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.beans.factory.Container;
import start.application.core.config.ConfigInfo;
import start.application.core.config.ConstantConfig;
import start.application.core.constant.Constant;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.StringHelper;

public class XmlPathApplicationContext extends GenerateBeanManager implements Container {
	
	private final static Logger log=LoggerFactory.getLogger(XmlPathApplicationContext.class);

	private String[] configFiles;
	
	public XmlPathApplicationContext() {
		//默认配置文件
		this("StartConfig");
	}

	public XmlPathApplicationContext(String... configFiles) {
		this.configFiles = configFiles;
	}

	@Override
	public void start() {
		// 1、解析配置文件
		ConfigInfo configInfo = new ConfigInfo(new XmlConfigAnalysis(this));
		for(String xmlfile:configFiles){
			configInfo.loadConfigFile(xmlfile);
		}
		if (StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)) {
			log.warn("扫描的类路径为空，请配置CLASSSCANPATH常量,需要扫描的类路径");
			return;
		}
		//2、扫描类路径
		String[] classpaths=ConstantConfig.CLASSSCANPATH.split(Constant.COMMA);
		AnnotationConfigContext annotationConfig=new AnnotationConfigContext(this);
		for (String packageName :classpaths) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				annotationConfig.load(clasz);
			}
		}
	}
	
}
