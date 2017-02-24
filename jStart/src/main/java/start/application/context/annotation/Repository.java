package start.application.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义于数据访问层,使用Resource(字段)或Qualifier(构造注入参数)
 * @author zhenyao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
	String value();
	String init() default "";
	String destory() default "";
}
