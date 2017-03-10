package start.application.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解当前类需要执行动态代理模式
 * @author zhenyao
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AOP {
	/**
	 * 需要拦截的AOPBean名称
	 * @return
	 */
	String[] value();
}