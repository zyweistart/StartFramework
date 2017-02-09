package start.application.core.beans;

public class BeanContext {

	public Object getBean(BeanInfo bean) throws Exception{
		//如果构造函数未注册则创造一个实例
		return  bean.getPrototype().newInstance();
	}
	
}
