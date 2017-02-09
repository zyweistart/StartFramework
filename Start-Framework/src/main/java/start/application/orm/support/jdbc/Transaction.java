package start.application.orm.support.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.core.utils.StackTraceInfo;
import start.application.orm.exceptions.RepositoryException;

public class Transaction {
	
	private final static Logger log=LoggerFactory.getLogger(Transaction.class);
	
	private Connection mConnection;
	
	public Transaction(Connection connection){
		this.mConnection=connection;
	}
	
	public Connection getConnection() {
		return mConnection;
	}

	/**
	 * 开始事务
	 */
	public void beginTrans(){
		try{
			/**
			 * 大多数主流数据库的默认事务等级，
			 * 保证了一个事务不会读到另一个并行事务己修改但未提交的数据，避免了“脏读取".
			 */
			getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			getConnection().setAutoCommit(false);
		}catch(SQLException e){
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * 数据库提交
	 */
	public void commitTrans() {
		try{
			getConnection().commit();
			getConnection().setAutoCommit(true);
		}catch(SQLException e){
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new RepositoryException(e);
		}finally{
			SessionManager.closeConnection();
		}
	}
	
	/**
	 * 数据库回滚
	 */
	public void rollbackTrans() {
		try{
			getConnection().rollback();
			getConnection().setAutoCommit(true);
		}catch(SQLException e){
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new RepositoryException(e);
		}finally{
			SessionManager.closeConnection();
		}
	}
	
}