package start.application.web.support.interceptor.fileupload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.config.ConstantConfig;
import start.application.core.utils.FileHelper;
import start.application.core.utils.StringHelper;
import start.application.web.action.ActionSupport;
import start.application.web.exceptions.ActionException;
import start.application.web.interceptor.InterceptorHandler;
import start.application.web.support.interceptor.RequestParameterInject;

public class UploadFileInterceptor extends InterceptorHandler {

	private final static Logger log=LoggerFactory.getLogger(UploadFileInterceptor.class);
	
	/**
	 * 文件上传大小限制BYTE为单位
	 */
	public final static Long MAXUPLOADSIZE=ConstantConfig.getLong("MAXUPLOADSIZE");
	
	/**
	 * 允许上传的文件类型"*"代表允许所有
	 * <pre>
	 * 可选值：*或其他文件类型
	 * </pre>
	 */
	public final static String[] ALLOWUPLOADTYPES=ConstantConfig.getString("ALLOWUPLOADTYPES").trim().split(",");
	
	/**
	 * 每次读取字节的大小
	 */
	private final static int LENGTH = 1024 * 8;

	private final static String NAME = "name=\"";
	private final static String FILENAME = "filename=\"";
	private final static String CONTENTDISPOSITIONFORMDATA = "Content-Disposition: form-data; ";
	private final static String CONTENTTYPE = "Content-Type: ";
	private final static String MULTIPARTFORMDATA="multipart/form-data";

	@Override
	public void intercept(ActionSupport support) throws Exception {
		if(support.getBean().isSingleton()){
			doInterceptor(support);
			return;
		}
		try {
			HttpServletRequest request = support.request();
			String contentType = request.getContentType();
			if (contentType != null && contentType.startsWith(MULTIPARTFORMDATA)) {

				String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
				// rfc1867数据包开头
				String startBoundary = "--" + boundary + "\r\n";
				// rfc1867数据包结尾
				String endBoundary = "--" + boundary + "--\r\n";
				// 从request对象中取得流
				ServletInputStream servletInputStream = request.getInputStream();
				// 读取的内容是否为文件
				boolean isFileFlag = false;
				// 是否正在读取数据内容
				boolean isReadFlag = false;
				// 字段
				Map<String, List<String>> fieldMaps = new HashMap<String, List<String>>();
				// 文件
				Map<String, List<UpLoadFile>> upLoadFiles = new HashMap<String, List<UpLoadFile>>();
				int readLength = -1;
				String fieldName = null;
				String fileName = null;
				String fileContentType = null;
				StringBuilder dataContent = null;
				File tmpFile = null;
				BufferedOutputStream bufferedOutputStream = null;
				// 每次读取的最大缓冲区大小
				byte[] BUFFER = new byte[LENGTH];
				while ((readLength = servletInputStream.readLine(BUFFER, 0, LENGTH)) != -1) {
					// 把读取到的值转换为String对象
					String content = new String(BUFFER, 0, readLength, ConstantConfig.ENCODING);
					// 如果是数据的开头或结尾
					if (content.equals(startBoundary) || content.equals(endBoundary)) {
						// 数据读取
						if (isReadFlag) {
							// 文件写入
							if (isFileFlag) {
								if (bufferedOutputStream != null) {
									// 文件对象保存
									List<UpLoadFile> lists = upLoadFiles.get(fieldName);
									if (lists == null) {
										lists = new ArrayList<UpLoadFile>();
									}
									long fileSize = tmpFile.length();
									if (fileSize <= MAXUPLOADSIZE) {
										lists.add(new UpLoadFile(tmpFile, fileName, fileContentType, fileSize));
										upLoadFiles.put(fieldName, lists);
									} else {
										tmpFile.delete();
										log.error("文件大小超过了：" + MAXUPLOADSIZE);
									}
									// 清理对象
									bufferedOutputStream.flush();
									bufferedOutputStream.close();
									bufferedOutputStream = null;
									tmpFile = null;
								}
							} else {// 文本内容
								List<String> lists = fieldMaps.get(fieldName);
								if (lists == null) {
									lists = new ArrayList<String>();
								}
								// -2表示删除最后的\r\n
								lists.add(dataContent.substring(0, dataContent.length() - 2));
								fieldMaps.put(fieldName, lists);
								dataContent = null;
							}
						}
						// 初始化
						isFileFlag = false;
						isReadFlag = false;
					} else {
						// 数据读取
						if (isReadFlag) {
							// 文件写入
							if (isFileFlag) {
								if (bufferedOutputStream != null) {
									bufferedOutputStream.write(BUFFER, 0, readLength);
								}
							} else {
								// 文本内容
								if (dataContent != null) {
									dataContent.append(content);
								}
							}
						} else {// 数据头信息
							isReadFlag = true;
							// 字段名
							fieldName = getFieldName(content);
							// 判断是否为文件
							isFileFlag = isFileField(content);
							// 如果为文件
							if (isFileFlag) {
								fileName = getFileName(content);
								if (fileName != null) {
									// 如果为文件则读取文件的内容类型
									readLength = servletInputStream.readLine(BUFFER, 0, LENGTH);
									content = new String(BUFFER, 0, readLength);
									fileContentType = content.substring(CONTENTTYPE.length(), content.length() - 2);
									if (isAllowUpload(fileContentType.trim())) {
										File dir = FileHelper.createDirectory(ConstantConfig.TMPPATH);
										tmpFile = new File(dir, UUID.randomUUID().toString());
										bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
									} else {
										log.error(fileName + "\t文件类型:" + fileContentType + "\t无法上传该类型文件！");
									}
								} else {
									// 读取空行
									servletInputStream.readLine(BUFFER, 0, LENGTH);
								}
							} else {
								dataContent = new StringBuilder();
							}
							// 读取空行
							servletInputStream.readLine(BUFFER, 0, LENGTH);
						}
					}
				}
				
				//字段注入
				Map<String,String> params=new HashMap<String,String>();
				for (String parameterName : fieldMaps.keySet()) {
					List<String> parameterValues = fieldMaps.get(parameterName);
					if(parameterValues.size()==1){
						params.put(parameterName, parameterValues.get(0));
					}else{
						List<String> lists=new ArrayList<String>();
						for(String o : parameterValues){
							lists.add(o);
						}
						params.put(parameterName, StringHelper.listToString(lists));
					}
				}
				RequestParameterInject.injectParameter(support.getAction(),params);
				// 文件注入
				Map<String,List<UpLoadFile>> fileParams=new HashMap<String,List<UpLoadFile>>();
				for (String fileField : upLoadFiles.keySet()) {
					fileParams.put(fileField	, upLoadFiles.get(fileField));
				}
				RequestParameterInject.injectObject(support.getAction(), fileParams);
			}
		} catch (Exception e) {
			throw new ActionException(e);
		} finally {
			// 继续执行下一个拦截器
			doInterceptor(support);
		}
	}

	/**
	 * 判断是否是文件字段
	 */
	static boolean isFileField(String content) {
		if (contentDisposition(content)) {
			int nIndex = content.indexOf(FILENAME);
			if (nIndex != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是内容描述
	 */
	static boolean contentDisposition(String content) {
		return content.startsWith(CONTENTDISPOSITIONFORMDATA);
	}

	/**
	 * 获取字段名
	 */
	static String getFieldName(String content) {
		if (contentDisposition(content)) {
			int nIndex = content.indexOf(NAME);
			if (nIndex != -1) {
				int nLastIndex = content.indexOf("\"", nIndex + NAME.length());
				return content.substring(nIndex + NAME.length(), nLastIndex);
			}
		}
		return "";
	}

	/**
	 * 获取文件名称
	 */
	static String getFileName(String content) {
		if (contentDisposition(content)) {
			int nIndex = content.indexOf(FILENAME);
			if (nIndex != -1) {
				int nLastIndex = content.indexOf("\"", nIndex + FILENAME.length());
				String fileName = content.substring(nIndex + FILENAME.length(), nLastIndex);
				if (!"".equals(fileName.trim())) {
					return fileName;
				}
			}
		}
		return null;
	}

	/**
	 * 是否允许上传
	 */
	static boolean isAllowUpload(String contentType) {
		for (String all : ALLOWUPLOADTYPES) {
			if ("*".equals(all.trim())) {
				return true;
			} else if (all.trim().equals(contentType.trim())) {
				return true;
			}
		}
		return false;
	}

}