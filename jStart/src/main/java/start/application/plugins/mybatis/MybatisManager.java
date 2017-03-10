package start.application.plugins.mybatis;

import java.lang.reflect.Proxy;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.annotation.Repository;
import start.application.core.beans.ContextBeanAdvice;
import start.application.core.beans.factory.DisposableBean;
import start.application.core.beans.factory.InitializingBean;
import start.application.core.constant.Constant;
import start.application.core.beans.BeanDefinition;
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.StringHelper;

public class MybatisManager extends ContextBeanAdvice implements InitializingBean,DisposableBean {
	
	private final static Logger log=LoggerFactory.getLogger(MybatisManager.class);
	
	private String basePackage;
	private DataSource dataSource;
	private SqlSessionFactory sqlSessionFactory;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(StringHelper.isEmpty(getBasePackage())){
			throw new ApplicationException("Mybatis映射扫描包路径为空!");
		}
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, getDataSource());
		Configuration configuration = new Configuration(environment);
		for (String packageName : getBasePackage() .split(Constant.COMMA)) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				if(clasz.isInterface()){
					if(clasz.isAnnotationPresent(Repository.class)){
						Repository repository=clasz.getAnnotation(Repository.class);
						registerBeanCenter(repository.value(), clasz.getName());
						configuration.addMapper(clasz);
						log.info("已配置"+clasz+" 加入到MybatisManager中管理~~~~");
					}
				}
			}
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	@Override
	public Object newBean(BeanDefinition bean) {
		SqlSession session=sqlSessionFactory.openSession();
		Object mapper=session.getMapper(bean.getPrototype());
		MybatisSessionProxy proxy=new MybatisSessionProxy(session,mapper);
		return Proxy.newProxyInstance(mapper.getClass().getClassLoader(), mapper.getClass().getInterfaces(),proxy);
	}

	@Override
	public void destroy() throws Exception {
	}


}
