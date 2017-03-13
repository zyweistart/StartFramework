package start.application.core.annotation.verify;

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
	 * 身份证
	 */
	IDCARD,
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
