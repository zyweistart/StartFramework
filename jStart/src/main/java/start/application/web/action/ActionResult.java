package start.application.web.action;

/**
 * Action方法返回基接口
 */
public interface ActionResult {
	/**
	 * Action返回后执行的方法，直接返回可return null,异常返回可再次返回子类对象
	 */
	void doExecute(ActionSupport support) throws Exception;

}