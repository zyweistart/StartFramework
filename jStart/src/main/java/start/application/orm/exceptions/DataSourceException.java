package start.application.orm.exceptions;

import start.application.core.exceptions.ApplicationException;

/**
 * 获取数据源异常
 * @author Start
 */
public class DataSourceException extends ApplicationException {

	private static final long serialVersionUID = 1L;
	
	public DataSourceException(Throwable e){
		super(e);
	}
	
	public DataSourceException(String message){
		super(message);
	}
}