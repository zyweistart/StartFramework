package start.application.context;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.config.ConfigImpl;
import start.application.context.config.ConfigInfo;
import start.application.context.config.ConstantConfig;
import start.application.context.config.CustomTag;
import start.application.core.Constant;
import start.application.core.beans.BeanContextFactory;
import start.application.core.beans.BeanInfo;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.ReflectUtils;
import start.application.core.utils.StringHelper;
import start.application.orm.AnnotationConfigEntityContext;
import start.application.orm.entity.EntityInfo;

public class Container implements Closeable {
	
	private final static Logger log=LoggerFactory.getLogger(Container.class);
	
	private static Map<String, Object> singletonBeans = new HashMap<String, Object>();
	
	/**
	 * 容器单例Bean
	 * @return
	 */
	public static Map<String, Object> getSingletonBeans() {
		return singletonBeans;
	}
	
	/**
	 * 容器初始化时调用该方法来加载容器对象
	 */
	public void init() {
		//1、解析配置文件
		ConfigInfo configInfo=new ConfigInfo(new ConfigImpl() {
			
			private List<BeanInfo> beans = new ArrayList<BeanInfo>();
			
			@Override
			public void read(String tagName, Map<String, String> attributes) {
				if(ConfigInfo.CONSTANT.equals(tagName)){
					//注册常量
					for(String key:attributes.keySet()){
						ContextObject.registerConstant(key, attributes.get(key));
					}
				}
			}

			@Override
			public void readBean(BeanInfo bean) {
				//注册Bean
				beans.add(bean);
			}

			@Override
			public void readCustom(CustomTag custom) {
				if(ConfigInfo.CONSTANT.equalsIgnoreCase(custom.getName())){
					String name=custom.getAttributes().get("name");
					String value=null;
					if(custom.getAttributes().containsKey("value")){
						value=custom.getAttributes().get("value");
					}else{
						value=custom.getTextContent();
					}
					//注册常量
					ContextObject.registerConstant(name, value);
				}else if(ConfigInfo.BEAN.equalsIgnoreCase(custom.getName())){
					BeanInfo bean=new BeanInfo();
					bean.getAttributes().putAll(custom.getAttributes());
					for(CustomTag child:custom.getChildTags()){
						if("property".equals(child.getName())){
							Map<String,String> attributes=child.getAttributes();
							String name=attributes.get("name");
							if(attributes.containsKey("value")){
								String value=attributes.get("value");
								bean.getValues().put(name, value);
							}else if(attributes.containsKey("ref")){
								String ref=attributes.get("ref");
								bean.getRefs().put(name, ref);
							}else{
								bean.getValues().put(name, child.getTextContent());
							}
						}
					}
					//注册Bean
					beans.add(bean);
				}else{
					//注册自定义标签
					ContextObject.registerCustom(custom.getName(), custom);
				}
			}

			@Override
			public void finish() {
				//读取完成后执行注册加载操作
				for(BeanInfo bean:beans){
					ContextObject.registerBean(bean);
				}
			}
			
		});
		configInfo.loadConfigFile();
		if(StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)){
			log.warn("扫描的类路径为空，请配置CLASSSCANPATH常量,需要扫描的类路径");
			return;
		}
		//2、扫描包下所有的类
		for (String packageName : ConstantConfig.CLASSSCANPATH.split(Constant.COMMA)) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				//2.1注册Bean
				BeanInfo bean=AnnotationConfigApplicationContext.analysisAnnotation(clasz);
				if(bean!=null){
					ContextObject.registerBean(bean);
					continue;
				}
				//2.2注册实体类
				EntityInfo entity =AnnotationConfigEntityContext.buildEntity(clasz);
				if(entity!=null){
					ContextObject.registerEntity(entity);
					continue;
				}
			}
		}
		//3、初始化Bean容器
		BeanContextFactory.init();
	}
	
	/**
	 * 容器关闭时调用该方法来释放连接资源
	 */
	@Override
	public void close() {
		for(String name:getSingletonBeans().keySet()){
			if(ContextObject.isBeanExistence(name)){
				BeanInfo bean=ContextObject.getBean(name);
				Object instance=getSingletonBeans().get(name);
				ReflectUtils.invokeMethod(instance,bean.getPrototype(),bean.getDestory());
			}
		}
	}
	
}
