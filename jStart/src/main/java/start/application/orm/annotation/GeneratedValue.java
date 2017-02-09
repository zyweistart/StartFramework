package start.application.orm.annotation;

/**
 * 主键的生成策略
 * UID
 * 统一的主键生成策略
 * NONE
 * 需要自己添加主键值
 */
public enum GeneratedValue {
	UID,
	NONE
}
