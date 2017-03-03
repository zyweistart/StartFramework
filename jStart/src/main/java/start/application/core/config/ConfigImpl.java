package start.application.core.config;

public interface ConfigImpl {
	
	/**
	 * 读取配置信息
	 * @param xml
	 */
	void read(XmlTag xml);
	/**
	 * 读取完成
	 */
	void finish();
	
}
