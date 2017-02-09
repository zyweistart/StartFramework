package start.application.core.beans;

public class BeanContextFactory {
	
	private BeanContextFactory(){}
	
	private static class BeanFactory {
		private static BeanContext instance = new BeanContext();
	}

	public static BeanContext getInstance() {
		return BeanFactory.instance;
	}
	
	/**
	 * 设置Bean创建容器
	 */
	public static void setContext(BeanContext context){
		BeanFactory.instance=context;
	}
	
}
