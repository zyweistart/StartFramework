package start.application.core.exceptions;

/**
 * 应用异常
 * @author Start
 */
public class ApplicationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ApplicationException(Throwable e) {
		super(e);
	}
	
	public ApplicationException(String message) {
		super(message);
	}
	
}