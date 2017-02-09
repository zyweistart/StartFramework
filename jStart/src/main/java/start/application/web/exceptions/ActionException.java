package start.application.web.exceptions;

import start.application.core.exceptions.ApplicationException;

/**
 * Action异常
 * @author Start
 */
public class ActionException extends ApplicationException {
	private static final long serialVersionUID = 1L;
	
	public ActionException(Throwable e) {
		super(e);
	}
	
	public ActionException(String message) {
		super(message);
	}
}