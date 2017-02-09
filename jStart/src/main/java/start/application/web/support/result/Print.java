package start.application.web.support.result;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.web.exceptions.ActionException;
import start.application.web.result.ActionResult;
import start.application.web.result.ActionResultInvocation;

public final class Print implements ActionResult {
	
	private final static Logger log=LoggerFactory.getLogger(Print.class);

	private String message;
	
	private String contentType="text/html;charset=utf-8";
	
	public Print(String message){
		this.message=message;
	}
	
	public Print(String message,String contentType){
		this.message=message;
		this.contentType=contentType;
	}
	
	@Override
	public void doExecute(ActionResultInvocation invocation) {
		HttpServletResponse response=invocation.getAction().response();
		response.setContentType(contentType);
		PrintWriter pw=null;
		try {
			pw=response.getWriter();
			pw.print(message);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new ActionException(e);
		}finally{
			pw=null;
		}
	}

}