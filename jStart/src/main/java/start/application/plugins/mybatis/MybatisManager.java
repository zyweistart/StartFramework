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
import start.application.core.beans.factory.ClosedBean;
import start.application.core.beans.factory.DisposableBean;
import start.application.core.beans.factory.InitializingBean;
import start.application.core.utils.ClassHelper;
import start.application.core.utils.StringHelper;

public class MybatisManager extends BeanBuilder implements InitializingBean,ClosedBean,DisposableBean {
	
	private final static Logger log=LoggerFactory.getLogger(MybatisManager.class);
	
	private SqlSessionFactory sqlSessionFactory;
	private DataSource dataSource;
	private String basePackage;
	private ThreadLocal<SqlSession> holder=new ThreadLocal<SqlSession>();

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
		holder.set(sqlSessionFactory.openSession());
		return holder.get().getMapper(bean.getPrototype());
	}

	@Override
	public void close() throws Exception {
		//如果存在连接资源则关闭连接对象
		SqlSession session=holder.get();
		if(session!=null){
			session.commit();
			session.close();
			//关闭对象后释放连接资源
			holder.remove();
		}
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

}
