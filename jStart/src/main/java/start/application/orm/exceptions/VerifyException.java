package start.application.orm.exceptions;

import start.application.core.exceptions.ApplicationException;

/**
 * 验证异常
 * @author Start
 */
public class VerifyException extends ApplicationException {
	
	private static final long serialVersionUID = 1L;
	
	public VerifyException(Throwable e) {
		super(e);
	}
	
	public VerifyException(String message) {
		super(message);
	}
	
}