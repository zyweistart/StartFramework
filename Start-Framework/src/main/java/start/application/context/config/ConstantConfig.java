package start.application.context.config;

import start.application.context.ContextObject;
import start.application.core.utils.StringHelper;

/**
 * 框架配置的常量
 * @author Start
 */
public class ConstantConfig {
	
	public static final String DEFAULT_PLACEHOLDER_PREFIX="${";
	public static final String DEFAULT_PLACEHOLDER_SUFFIX="}";
	
	/**
	 * 开发模式
	 * <pre>
	 * 可选值：
	 * 	true: 会打印一些帮助信息
	 * 	false:关闭打印信息
	 * </pre>
	 */
//	public final static Boolean DEVMODE=ConstantConfig.getBoolean("DEVMODE");
	/**
	 * 默认编码格式 
	 *	<pre>
	 *	GBK、UTF-8
	 *	</pre>
	 */
	public final static String ENCODING=ConstantConfig.getString("ENCODING");
	/**
	 * 默认日期时间格式
	 */
	public final static String DATAFORMAT=ConstantConfig.getString("DATAFORMAT");
	/**
	 * 需要自动打扫的类路径
	 * <pre>
	 * 	类的路径示例：start.application则打扫该包下的所有类包概所有子包
	 * </pre>
	 */
	public final static String CLASSSCANPATH=ConstantConfig.getString("CLASSSCANPATH");
	/**
	 * 数据保存主路径
	 */
	public final static String ROOTPATH=ConstantConfig.getString("ROOTPATH");
	/**
	 * 临时文件目录
	 */
	public final static String TMPPATH=ConstantConfig.getString("TMPPATH");
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
	 * 日志方向
	 */
	public final static String LOGGER=ConstantConfig.getString("LOGGER");
	/**
	 * 日志文件的扩展名
	 */
	public final static String LOGSUFFIX=ConstantConfig.getString("LOGSUFFIX");
	/**
	 * 日志文件最大的文件大小
	 */
	public final static Long LOGMAXFILESIZE=ConstantConfig.getLong("LOGMAXFILESIZE");
	
	public static Integer getInt(String key){
		return StringHelper.nullToInt(ContextObject.getConstants().get(key));
	}

	public static Long getLong(String key){
		return StringHelper.nullToLong(ContextObject.getConstants().get(key));
	}
	
	public static String getString(String key){
		return StringHelper.nullToStrTrim(ContextObject.getConstants().get(key));
	}
	
	public static Boolean getBoolean(String key){
		return StringHelper.nullToBoolean(ContextObject.getConstants().get(key));
	}
	
	public static String get(String value) {
		int p1 = value.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
		int p2 = value.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);

		if (p1 > -1 && p2 > -1) {
			String innerKey = value.substring(p1 + DEFAULT_PLACEHOLDER_PREFIX.length(), p2);
			value = ContextObject.getConstants().get(innerKey);
		}
		return value;
	}
	
}