package start.application.plugins.mybatis;

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
import start.application.core.Constant;
import start.application.core.annotation.Repository;
import start.application.core.beans.BeanBuilder;
import start.application.core.beans.BeanDefinition;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.StringHelper;

public class MybatisManager extends BeanBuilder {
	
	private final static Logger log=LoggerFactory.getLogger(MybatisManager.class);
	
	private SqlSessionFactory sqlSessionFactory;
	private DataSource dataSource;
	private String basePackage;

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
	
	public void init(){
		if(StringHelper.isEmpty(getBasePackage())){
			log.warn("Mybatis映射扫描包路径为空!");
			return;
		}
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, getDataSource());
		Configuration configuration = new Configuration(environment);
		for (String packageName : getBasePackage() .split(Constant.COMMA)) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				if(clasz.isInterface()){
					if(clasz.isAnnotationPresent(Repository.class)){
						Repository repository=clasz.getAnnotation(Repository.class);
						registerBeanManager(repository.value(), clasz.getName());
					}else{
						registerBeanManager(clasz.getName(), clasz.getName());
					}
					configuration.addMapper(clasz);
				}
			}
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}

	@Override
	public Object getBean(BeanDefinition bean) {
		System.out.println("调用了mybatis"+bean.getPrototypeString());
		SqlSession session = sqlSessionFactory.openSession();
		return session.getMapper(bean.getPrototype());
	}

}
