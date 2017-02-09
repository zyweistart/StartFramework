package start.application.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注当前的实体的表名一般只有对标注了@Entity才有效
 * 表名最长为25个字符
 * @author Start
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 标明表的名字
	 */
	String value();
}
