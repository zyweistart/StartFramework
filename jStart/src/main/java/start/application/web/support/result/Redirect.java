package start.application.web.support.result;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.web.action.ActionSupport;


/**
 * 重定向
 */
public final class Redirect extends View {
	
	private final static Logger log=LoggerFactory.getLogger(Redirect.class);

	public Redirect(String dispatcherPage) {
		super(dispatcherPage);
	}

	@Override
	public void doExecute(ActionSupport support) {
		HttpServletResponse response=support.response();
		try {
			response.sendRedirect(getDispatcherPage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
		}
	}

}