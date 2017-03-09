package start.application.core.beans;

import java.util.HashMap;
import java.util.Map;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ApplicationContext;

public class BeanContextFactory {
	
	private final static Logger log=LoggerFactory.getLogger(BeanContextFactory.class);

	private static ContextAdvice mDefaultBeanBuilder;
	private static Map<String,ContextAdvice> mBeanBuilderCollection;
	
	static{
		mDefaultBeanBuilder=new DefaultBeanContext();
		mBeanBuilderCollection=new HashMap<String,ContextAdvice>();
	}
	
	public static void registerContext(BeanDefinition bean){
		String name=bean.getName();
		if(mBeanBuilderCollection.containsKey(name)){
			throw new IllegalArgumentException(name+"容器对象已存在!");
		}
		ContextAdvice builder =(ContextAdvice)ApplicationContext.getBean(bean);
		mBeanBuilderCollection.put(name, builder);
		log.info("自定义BeanBuilder容器对象："+name+"，加载成功~~~");
	}
	
	/**
	 * 获取bean所对应的容器工厂
	 * @param contextName
	 * @return
	 */
	public static ContextAdvice getBeanContext(String beanContextName){
		if(beanContextName==null){
			//返回默认的生成容器
			return mDefaultBeanBuilder;
		}
		return mBeanBuilderCollection.get(beanContextName);
	}
	
	/**
	 * 容器关闭时调用
	 * @throws Exception
	 */
	public static void destory() throws Exception{
		mDefaultBeanBuilder.destroy();
		for(ContextAdvice instance:mBeanBuilderCollection.values()){
			instance.destroy();
		}
	}
	
}
