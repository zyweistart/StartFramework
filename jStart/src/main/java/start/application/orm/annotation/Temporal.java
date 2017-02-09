package start.application.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日期类型注解于String、Date类型的字段上
 * @author Start
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Temporal {
	/**
	 * 字段名
	 */
	String name() default "";
	/**
	 * 日期时间格式
	 */
	String format() default "yyyyMMddHHmmss";
}
