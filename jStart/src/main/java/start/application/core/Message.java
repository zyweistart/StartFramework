package start.application.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;


/**
 * 消息资源
 * @author Start
 */
public final class Message {
	
	private final static Logger log=LoggerFactory.getLogger(Message.class);
	
	private final static String BUNDLE_NAME_MESSAGE="META-INF.Message";

	private final static ResourceBundle RESOURCE_BUNDLE_MESSAGE;
	
	static{
		RESOURCE_BUNDLE_MESSAGE = ResourceBundle.getBundle(BUNDLE_NAME_MESSAGE);
	}
	
	public static String getMessage(Integer key,Object...params){
		if(key == null) {
			return Constant.UNKNOWN;
		}
		try{
			return String.format(RESOURCE_BUNDLE_MESSAGE.getString(String.valueOf(key)), params);
		}catch(MissingResourceException e){
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			return Constant.UNKNOWN;
		}
	}
	
	//Context
	/**
	 * 初始化容器，请稍候....
	 */
	public final static Integer PM_1000=1000;
	/**
	 * 容器中不存在该%s对象!
	 */
	public final static Integer PM_1003=1003;
	/**
	 * 当前%s字段上未标注%s注解
	 */
	public final static Integer PM_1005=1005;
	//Config
	public final static Integer PM_2001=2001;
	//Annotation
	/**
	 * %s--已经注册，重复注册！
	 */
	public final static Integer PM_3000=3000;
	/**
	 * %s实体的主键未定义！
	 */
	public final static Integer PM_3001=3001;
	/**
	 * %s实体的主键重复定义，根据约定一个表主键只能有一个，可用唯一约束加非空来代替！
	 */
	public final static Integer PM_3002=3002;	
	/**
	 * %s实体类中%s字段,Date类型字段必须加上@Temporal注解
	 */
	public final static Integer PM_3011=3011;
	/**
	 * %s实体类中%s字段,@Column注解只能注解在数字、字符串型的字段
	 */
	public final static Integer PM_3012=3012;
	/**
	 * %s实体类中%s字段,@Temporal注解只能注解在日期、字符串型的字段
	 */
	public final static Integer PM_3013=3013;
	/**
	 * %s实体类中%s字段,@Lob注解只能注解在类型为字符串的字段
	 */
	public final static Integer PM_3014=3014;
	/**
	 * %s实体%s字段中,自动生成主键策略类型只能为数字型
	 */
	public final static Integer PM_3015=3015;
	/**
	 * %s实体%s字段中,UUID生成策略类型只能为字符串型
	 */
	public final static Integer PM_3016=3016;
	public final static Integer PM_3017=3017;
	//Controller
	/**
	 * %s控制类%s方法返回的类型不是IActionResult接口!
	 */
	public final static Integer PM_4002=4002;
	/**
	 * %s控制类中不存在%s执行方法
	 */
	public final static Integer PM_4003=4003;
	/**
	 * 当前Action类实例不是ActionSupport的子类，数据注入失败！
	 */
	public final static Integer PM_4005=4005;
	/**
	 * %s字段注入失败，最多只支持二级注入！
	 */
	public final static Integer PM_4006=4006;
	//Repository
	/**
	 * %s实体的主键值为空！
	 */
	public final static Integer PM_5001=5001;
	/**
	 * %s实体,%s方法,%s类型,注入%s值时出错！
	 */
	public final static Integer PM_5004=5004;
	/**
	 * %s实体,%s方法,无法将%s值注入到%s类型中！
	 */
	public final static Integer PM_5005=5005;
	/**
	 * %s语句执行失败，错误信息：%s
	 */
	public final static Integer PM_5009=5009;
	/**
	 * 容器析构出现异常，信息：%s
	 */
	public final static Integer PM_5015=5015;
	/**
	 * 开启事务失败！
	 */
	public final static Integer PM_5016=5016;
	/**
	 * 事务提交时发生异常，无法完成提交！
	 */
	public final static Integer PM_5017=5017;
	/**
	 * 事务调用发现异常,无法完成回滚！
	 */
	public final static Integer PM_5018=5018;
	/**
	 * %s类%s字段无法定义%s类型数据！
	 */
	public final static Integer PM_5019=5019;
	
}
