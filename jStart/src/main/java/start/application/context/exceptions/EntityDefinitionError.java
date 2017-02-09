package start.application.context.exceptions;

/**
 * 实体类定义错误，出错则无法继续
 * 
 * @author Start
 */
public class EntityDefinitionError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityDefinitionError(Throwable e) {
		super(e);
	}

}