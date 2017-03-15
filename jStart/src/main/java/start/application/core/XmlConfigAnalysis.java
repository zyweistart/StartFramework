package start.application.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import start.application.core.beans.BeanDefinition;
import start.application.core.beans.factory.ApplicationContext;
import start.application.core.config.ConfigImpl;
import start.application.core.config.ConfigInfo;
import start.application.core.config.XmlTag;
import start.application.core.context.ContextCacheObject;
import start.application.core.io.ApplicationIO;

/**
 * 应用配置文件解析
 * @author zhenyao
 *
 */
public class XmlConfigAnalysis implements ConfigImpl {

	public static final String BEAN="bean";
	public static final String CONSTANT="constant";
	private ApplicationContext mApplicationContext;
	
	public XmlConfigAnalysis(ApplicationContext application){
		this.mApplicationContext=application;
	}
	
	// 注册Bean对象
	private List<BeanDefinition> registerBeans = new ArrayList<BeanDefinition>();

	@Override
	public void read(XmlTag xml) {
		if (CONSTANT.equalsIgnoreCase(xml.getName())) {
			String name = xml.getAttributes().get("name");
			String value = null;
			if (xml.getAttributes().containsKey("value")) {
				value = xml.getAttributes().get("value");
			} else {
				value = xml.getTextContent();
			}
			// 注册常量
			ContextCacheObject.registerConstant(name, value);
		} else if (ConfigInfo.PROPERTIES.equalsIgnoreCase(xml.getName())) {
			for (String key : xml.getAttributes().keySet()) {
				// 注册常量
				ContextCacheObject.registerConstant(key, xml.getAttributes().get(key));
			}
		} else if (BEAN.equalsIgnoreCase(xml.getName())) {
			BeanDefinition bean = new BeanDefinition();
			ApplicationIO.iocObjectParameter(bean, xml.getAttributes());
			for (XmlTag child : xml.getChildTags()) {
				if ("property".equals(child.getName())) {
					Map<String, String> attributes = child.getAttributes();
					String name = attributes.get("name");
					if (attributes.containsKey("value")) {
						String value = attributes.get("value");
						bean.getValues().put(name, value);
					} else if (attributes.containsKey("ref")) {
						String ref = attributes.get("ref");
						bean.getRefs().put(name, ref);
					} else {
						bean.getValues().put(name, child.getTextContent());
					}
				}
			}
			// 注册Bean
			registerBeans.add(bean);
		} else {
			// 注册自定义标签
			ContextCacheObject.registerCustom(xml.getName(), xml);
		}
	}

	@Override
	public void finish() {
		// 读取完成后执行注册加载操作
		for (BeanDefinition bean : registerBeans) {
			this.mApplicationContext.registerBeanDoManagerCenter(bean);
		}
	}

}
