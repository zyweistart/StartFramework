package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ApplicationContext;
import start.application.core.exceptions.ApplicationException;

public class BeanBuilderFactory {
	
	private final static Logger log=LoggerFactory.getLogger(BeanBuilderFactory.class);

	private static BeanBuilder mDefaultBeanBuilder;
	private static Map<String,BeanBuilder> mBeanBuilderCollection;
	
	static{
		mDefaultBeanBuilder=new DefaultBeanBuilder();
		mBeanBuilderCollection=new HashMap<String,BeanBuilder>();
	}
	
	public static void registerContext(BeanDefinition bean){
		String name=bean.getPrototypeString();
		if(mBeanBuilderCollection.containsKey(name)){
			throw new IllegalArgumentException(name+"容器对象已存在!");
		}
		BeanBuilder builder = (BeanBuilder)ApplicationContext.getBean(bean, mDefaultBeanBuilder);
		mBeanBuilderCollection.put(name, builder);
		try {
			builder.afterPropertiesSet();
			log.info("自定义BeanBuilder容器对象："+name+"，加载成功~~~");
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * 获取bean所对应的容器工厂
	 * @param contextName
	 * @return
	 */
	public static BeanBuilder getBeanContext(String contextName){
		if(contextName==null){
			//返回默认的生成容器
			return mDefaultBeanBuilder;
		}
		if(mBeanBuilderCollection.containsKey(contextName)){
			return mBeanBuilderCollection.get(contextName);
		}
		return null;
	}
	
	public static void close() throws Exception{
		mDefaultBeanBuilder.close();
		for(BeanBuilder instance:mBeanBuilderCollection.values()){
			instance.close();
		}
	}
	
	public static void destory() throws Exception{
		mDefaultBeanBuilder.destroy();
		for(BeanBuilder instance:mBeanBuilderCollection.values()){
			instance.destroy();
		}
	}
	
}
