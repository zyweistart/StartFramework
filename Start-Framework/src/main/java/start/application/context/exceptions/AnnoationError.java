package start.application.context.exceptions;

/**
 * 注解定义出错，出错则无法继续
 * 
 * @author Start
 */
public class AnnoationError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnnoationError(String message) {
		super(message);
	}

}