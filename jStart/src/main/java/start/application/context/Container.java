package start.application.context;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.Constant;
import start.application.core.Plugin;
import start.application.core.beans.BeanBuilderFactory;
import start.application.core.beans.BeanDefinition;
import start.application.core.config.ConfigImpl;
import start.application.core.config.ConfigInfo;
import start.application.core.config.ConstantConfig;
import start.application.core.config.XmlTag;
import start.application.core.context.BeanLoaderContext;
import start.application.core.context.LoaderHandler;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.ReflectUtils;
import start.application.core.utils.StringHelper;
import start.application.orm.context.OrmLoaderContext;
import start.application.web.context.WebLoaderContext;

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
			//插件列表
			private Map<String,Plugin> plugins = new HashMap<String,Plugin>();
			//注册Bean对象
			private List<BeanDefinition> registerBeans = new ArrayList<BeanDefinition>();
			
			@Override
			public void read(XmlTag xml) {
				if(ConfigInfo.CONSTANT.equalsIgnoreCase(xml.getName())){
					String name=xml.getAttributes().get("name");
					String value=null;
					if(xml.getAttributes().containsKey("value")){
						value=xml.getAttributes().get("value");
					}else{
						value=xml.getTextContent();
					}
					//注册常量
					ContextObject.registerConstant(name, value);
				}else if(ConfigInfo.PROPERTIES.equalsIgnoreCase(xml.getName())){
					for(String key:xml.getAttributes().keySet()){
						//注册常量
						ContextObject.registerConstant(key, xml.getAttributes().get(key));
					}
				}else if(ConfigInfo.BEAN.equalsIgnoreCase(xml.getName())){
					BeanDefinition bean=new BeanDefinition();
					bean.getAttributes().putAll(xml.getAttributes());
					for(XmlTag child:xml.getChildTags()){
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
					registerBeans.add(bean);
				}else if(ConfigInfo.PLUGIN.equalsIgnoreCase(xml.getName())){
					String name=xml.getAttributes().get("name");
					String className=xml.getAttributes().get("class");
					try {
						plugins.put(name, (Plugin)(Class.forName(className).newInstance()));
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}else{
					//注册自定义标签
					ContextObject.registerCustom(xml.getName(), xml);
				}
			}

			@Override
			public void finish() {
				//读取完成后执行注册加载操作
				for(BeanDefinition bean:registerBeans){
					ContextObject.registerBean(bean,true);
				}
				for(String name:plugins.keySet()){
					 Plugin plugin=plugins.get(name);
					for(XmlTag xmlTag:ContextObject.getCustom(name)){
						plugin.execute(xmlTag);
					}
				}
			}
			
		});
		configInfo.loadConfigFile();
		if(StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)){
			log.warn("扫描的类路径为空，请配置CLASSSCANPATH常量,需要扫描的类路径");
			return;
		}
		
		List<LoaderHandler> loaders=new ArrayList<LoaderHandler>();
		//1、Web控制器解析
		loaders.add(new WebLoaderContext());
		//2、Bean对象解析 
		loaders.add(new BeanLoaderContext());
		//3、ORM实体解析
		loaders.add(new OrmLoaderContext());
		Iterator<LoaderHandler> interceptors = loaders.iterator();
		LoaderHandler handler=null;
		while(interceptors.hasNext()){
			LoaderHandler currentHandler=interceptors.next();
			currentHandler.setHandler(handler);
			handler=currentHandler;
		}
		//2、扫描包下所有的类
		for (String packageName : ConstantConfig.CLASSSCANPATH.split(Constant.COMMA)) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				handler.reset();
				handler.load(clasz);
			}
		}
		//3、初始化Bean容器
		BeanBuilderFactory.init();
	}
	
	/**
	 * 容器关闭时调用该方法来释放连接资源
	 */
	@Override
	public void close() {
		for(String name:getSingletonBeans().keySet()){
			if(ContextObject.isBeanExistence(name)){
				BeanDefinition bean=ContextObject.getBean(name);
				Object instance=getSingletonBeans().get(name);
				ReflectUtils.invokeMethod(instance,bean.getDestory());
			}
		}
	}
	
}
