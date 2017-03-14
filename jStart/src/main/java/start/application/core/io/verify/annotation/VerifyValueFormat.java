package start.application.core.io.verify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检测数据的格式
 * @author zhenyao
 *
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyValueFormat {
	public enum FormatType {
		/**
		 * 电话号码
		 */
		PHONE,
		/**
		 * 手机号码
		 */
		MOBILE,
		/**
		 * 电子邮件
		 */
		MAIL,
		/**
		 * MD5格式
		 */
		MD5,
		/**
		 * IP地址
		 */
		IP,
		/**
		 * MAC地址
		 */
		MAC,
		/**
		 * 是否为中文
		 */
		CHINESE,
		/**
		 * 是否为数字
		 */
		NUMBER,
		/**
		 * 是否为金额
		 */
		MONEY
	}
	
	FormatType type();
	String message();
}
