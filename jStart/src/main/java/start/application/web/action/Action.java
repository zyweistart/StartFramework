package start.application.web.action;

import start.application.web.result.ActionResult;

public interface Action {
	
	ActionResult execute() throws Exception;

}
