package start.application.core.config;

import start.application.core.context.ContextCacheObject;
import start.application.core.utils.StringHelper;

/**
 * 框架配置的常量
 * @author Start
 */
public class ConstantConfig {
	
	public static final String DEFAULT_PLACEHOLDER_PREFIX="${";
	public static final String DEFAULT_PLACEHOLDER_SUFFIX="}";
	
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
	
	public static Integer getInt(String key){
		return StringHelper.nullToInt(ContextCacheObject.getConstant(key));
	}

	public static Long getLong(String key){
		return StringHelper.nullToLong(ContextCacheObject.getConstant(key));
	}
	
	public static String getString(String key){
		return StringHelper.nullToStrTrim(ContextCacheObject.getConstant(key));
	}
	
	public static Boolean getBoolean(String key){
		return StringHelper.nullToBoolean(ContextCacheObject.getConstant(key));
	}
	
	public static String get(String value) {
		int p1 = value.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
		int p2 = value.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);

		if (p1 > -1 && p2 > -1) {
			String innerKey = value.substring(p1 + DEFAULT_PLACEHOLDER_PREFIX.length(), p2);
			value = ContextCacheObject.getConstant(innerKey);
		}
		return value;
	}
	
}