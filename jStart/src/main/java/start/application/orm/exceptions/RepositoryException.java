package start.application.orm.exceptions;

import start.application.core.exceptions.ApplicationException;

/**
 * 持久化异常
 * @author Start
 */
public class RepositoryException extends ApplicationException {
	
	private static final long serialVersionUID = 1L;
	
	public RepositoryException(Throwable e) {
		super(e);
	}
	
	public RepositoryException(String message) {
		super(message);
	}
	
}