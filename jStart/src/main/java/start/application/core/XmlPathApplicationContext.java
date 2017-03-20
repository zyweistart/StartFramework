package start.application.core;

import java.io.IOException;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.ApplicationContext;
import start.application.core.config.ConfigInfo;
import start.application.core.config.ConstantConfig;
import start.application.core.constant.Constant;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.StringHelper;

public class XmlPathApplicationContext implements ApplicationContext {
	
	private final static Logger log=LoggerFactory.getLogger(XmlPathApplicationContext.class);

	private String[] configFiles;
	
	private GenerateBeanManager beanFactory;
	
	public XmlPathApplicationContext() {
		//默认配置文件
		this("StartConfig.xml");
	}

	public XmlPathApplicationContext(String... configFiles) {
		this.configFiles = configFiles;
	}

	public void start() {
		buildBeanFactory();
		// 1、解析配置文件
		ConfigInfo configInfo = new ConfigInfo(new XmlConfigAnalysis(this));
		for(String xmlfile:configFiles){
			configInfo.loadConfigFile(xmlfile);
		}
		//2、扫描类路径
		if (StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)) {
			log.warn("扫描的类路径为空，请配置CLASSSCANPATH常量,需要扫描的类路径");
		}else{
			String[] classpaths=ConstantConfig.CLASSSCANPATH.split(Constant.COMMA);
			AnnotationConfigContext annotationConfig=new AnnotationConfigContext(this);
			for (String packageName :classpaths) {
				for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
					annotationConfig.load(clasz);
				}
			}
		}
	}
	
	/**
	 * 生成创建Bean工厂实例
	 */
	public void buildBeanFactory() {
		if(beanFactory==null){
			beanFactory=new GenerateBeanManager();
		}
	}

	@Override
	public boolean isBeanDefinitionExistence(String prototypeString) {
		return beanFactory.isBeanDefinitionExistence(prototypeString);
	}

	@Override
	public void registerBeanDoManagerCenter(BeanDefinition bean) {
		beanFactory.registerBeanDoManagerCenter(bean);
	}

	@Override
	public BeanDefinition getBeanDefinitionInfoByName(String name) {
		return beanFactory.getBeanDefinitionInfoByName(name);
	}

	@Override
	public BeanDefinition getBeanDefinitionInfoByClass(String prototypeString) {
		return beanFactory.getBeanDefinitionInfoByClass(prototypeString);
	}

	@Override
	public Object getBean(String name) {
		return beanFactory.getBean(name);
	}

	@Override
	public <T> T getBean(Class<T> prototype) {
		return beanFactory.getBean(prototype);
	}

	@Override
	public void close() throws IOException {
		beanFactory.close();
	}
	
}
