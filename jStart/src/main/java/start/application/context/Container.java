package start.application.context;

import java.io.Closeable;

import start.application.context.config.ConfigInfo;
import start.application.context.config.ConstantConfig;
import start.application.core.Constant;
import start.application.core.beans.BeanInfo;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.ReflectUtils;
import start.application.orm.AnnotationConfigEntityContext;
import start.application.orm.entity.EntityInfo;

public class Container implements Closeable {
	
	/**
	 * 容器初始化时调用该方法来加载容器对象
	 */
	public void init() {
		//1、解析配置文件
		ConfigInfo configInfo=new ConfigInfo();
		configInfo.loadDefaultConfigFile();
		ContextObject.getConstants().putAll(configInfo.getConstants());
		for(String name:configInfo.getBeans().keySet()){
			ContextObject.putBeans(configInfo.getBeans().get(name));
		}
		ContextObject.getInterceptors().addAll(configInfo.getInterceptors());
		//2、扫描包下所有的类
		for (String packageName : ConstantConfig.CLASSSCANPATH.split(Constant.COMMA)) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				//Bean
				BeanInfo bean=AnnotationConfigApplicationContext.getBeanInfo(clasz);
				if(bean!=null){
					ContextObject.putBeans(bean);
					continue;
				}
				//Entity
				EntityInfo entity =AnnotationConfigEntityContext.buildEntity(clasz);
				if(entity!=null){
					ContextObject.putEntitys(entity);
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
		for(String name:ContextObject.getSingletonBeans().keySet()){
			BeanInfo bean=ContextObject.getBeans(name);
			Object instance=ContextObject.getSingletonBeans().get(name);
			ReflectUtils.invokeMethod(instance,bean.getPrototype(),bean.getDestory());
		}
	}
	
}
