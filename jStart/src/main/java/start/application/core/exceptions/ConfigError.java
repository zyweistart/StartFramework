package start.application.core.exceptions;

/**
 * 配置文件出错，出错则无法继续
 * 
 * @author Start
 *
 */
public class ConfigError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigError(Throwable e) {
		super(e);
	}
	
	public ConfigError(String message) {
		super(message);
	}

}
