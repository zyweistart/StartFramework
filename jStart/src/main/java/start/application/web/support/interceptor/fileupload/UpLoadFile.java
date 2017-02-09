package start.application.web.support.interceptor.fileupload;

import java.io.File;

/**
 * 文件上传辅助类
 */
public class UpLoadFile{
	
	public UpLoadFile(){}
	
	public UpLoadFile(File file, String uploadName, String contentType,long fileSize) {
		this.file = file;
		this.uploadName = uploadName;
		this.contentType = contentType;
		this.fileSize=fileSize;
	}
	
	private File file;
	/**
	 * 文件名
	 */
	private String uploadName;
	/**
	 * 文件类型
	 */
	private String contentType;
	/**
	 * 文件大小
	 */
	private long fileSize;
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getUploadName() {
		return uploadName;
	}
	
	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	/**
	 * 扩展名
	 */
	public String getExtension (){
		if(uploadName.indexOf(".")!=-1){
			return uploadName.substring(uploadName.lastIndexOf("."));
		}
		return "";
	}
	/**
	 * 文件名不包含扩展名
	 */
	public String getName(){
		if(uploadName.indexOf(".")!=-1){
			return uploadName.substring(0,uploadName.lastIndexOf("."));
		}
		return uploadName;
	}
	/**
	 * 写入文件
	 */
	public void write(File dest){
		this.file.renameTo(dest);
	}
	
}