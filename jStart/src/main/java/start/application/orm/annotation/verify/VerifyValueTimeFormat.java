package start.application.orm.annotation.verify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检测日期格式是否正确
 * @author zhenyao
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyValueTimeFormat {
	String format() default "yyyyMMddHHmmss";
	String message();
}
