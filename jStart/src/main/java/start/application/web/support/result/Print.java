package start.application.web.support.result;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.web.action.ActionResult;
import start.application.web.action.ActionSupport;

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
	public void doExecute(ActionSupport support) {
		HttpServletResponse response=support.response();
		response.setContentType(contentType);
		PrintWriter pw=null;
		try {
			pw=response.getWriter();
			pw.print(message);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
		}finally{
			if(pw!=null){
				pw.close();
				pw=null;
			}
		}
	}

}