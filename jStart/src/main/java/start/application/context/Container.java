package start.application.context;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.config.ConfigInfo;
import start.application.context.config.ConstantConfig;
import start.application.core.Constant;
import start.application.core.beans.BeanInfo;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.ReflectUtils;
import start.application.core.utils.StringHelper;
import start.application.orm.AnnotationConfigEntityContext;
import start.application.orm.entity.EntityInfo;

public class Container implements Closeable {
	
	private final static Logger log=LoggerFactory.getLogger(Container.class);
	
	private static Map<String, Object> singletonBeans = new ConcurrentHashMap<String, Object>();
	
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
		ConfigInfo configInfo=new ConfigInfo();
		configInfo.loadDefaultConfigFile();
		//1.1注册常量
		for(String key:configInfo.getConstants().keySet()){
			ContextObject.registerConstant(key, configInfo.getConstants().get(key));
		}
		//1.2注册Bean
		for(String name:configInfo.getBeans().keySet()){
			ContextObject.registerBean(configInfo.getBeans().get(name));
		}
		//1.3注册自定义标签
		for(String tagName:configInfo.getCustom().keySet()){
			for(Map<String,String> values:configInfo.getCustom().get(tagName)){
				ContextObject.registerCustom(tagName, values);
			}
		}
		if(StringHelper.isEmpty(ConstantConfig.CLASSSCANPATH)){
			log.warn("扫描的类路径为空");
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
	}
	
	/**
	 * 容器关闭时调用该方法来释放连接资源
	 */
	@Override
	public void close() {
		for(String name:getSingletonBeans().keySet()){
			BeanInfo bean=ContextObject.getBean(name);
			Object instance=getSingletonBeans().get(name);
			ReflectUtils.invokeMethod(instance,bean.getPrototype(),bean.getDestory());
		}
	}
	
}
