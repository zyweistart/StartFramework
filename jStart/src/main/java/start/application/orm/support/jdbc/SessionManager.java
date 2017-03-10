package start.application.orm.support.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.orm.exceptions.RepositoryException;

public class SessionManager {

	private final static Logger log=LoggerFactory.getLogger(SessionManager.class);
	private static ThreadLocal<Connection> conectionSession=new ThreadLocal<Connection>();
	
	private DataSource datasource;

	public int executeUpdate(String SQL,Object...params) throws SQLException{
		return executeUpdate1(SQL,false,params);
	}
	
	/**
	 * @param GENERATED_KEYS_FLAG 是否返回生成的主键值
	 */
	public int executeUpdate1(String SQL,boolean GENERATED_KEYS_FLAG, Object...params)throws SQLException{
		logConsole(SQL);
		Connection conn=null;
		PreparedStatement pstmt=null;
		try{
			conn=getConnection();
			pstmt=GENERATED_KEYS_FLAG?
					conn.prepareStatement(SQL,Statement.RETURN_GENERATED_KEYS):
						conn.prepareStatement(SQL);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					pstmt.setObject(i+1, params[i]);
				}
			}
			int resultValue=pstmt.executeUpdate();
			if(GENERATED_KEYS_FLAG){
				ResultSet rSet=null;
				try{
					rSet=pstmt.getGeneratedKeys();
					while(rSet.next()){
						resultValue=rSet.getInt(1);
					}
				}finally{
					SessionManager.closeResultSet(rSet);
				}
			}
			return resultValue;
		}finally{
			SessionManager.closePreparedStatement(pstmt);
			SessionManager.closeConnection();
		}
	}
	
	/**
	 * SQL批处理
	 */
	public int[] executeBatch(String... SQLBatchs) throws SQLException{
		Statement stmt=null;
		Connection conn=null;
		try{
			conn=getConnection();
			stmt=conn.createStatement();
			for(String batch:SQLBatchs){
				stmt.addBatch(batch);
				logConsole(batch);
			}
			return stmt.executeBatch();
		}finally{
			SessionManager.closeStatement(stmt);
			SessionManager.closeConnection();
		}
	}
	
	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	/**
	 * 获取连接对象
	 */
	public Connection getConnection() throws SQLException {
		Connection connection=conectionSession.get();
		if(connection==null){
			connection=getDatasource().getConnection();
			conectionSession.set(connection);
		}
		return connection;
	}
	
	/**
	 * 获取事务对象
	 */
	public TransactionManager getTransaction() throws SQLException{
		return new TransactionManager(getConnection());
	}
	
	/**
	 * 开始事务
	 */
	public void beginTrans(){
		try{
			getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			getConnection().setAutoCommit(false);
		}catch(SQLException e){
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * 数据库提交
	 */
	public void commitTrans() {
		try{
			getConnection().commit();
		}catch(SQLException e){
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
		}catch(SQLException e){
			throw new RepositoryException(e);
		}finally{
			SessionManager.closeConnection();
		}
	}

	////////////////////////////关闭对象/////////////////////
	
	public static void closeResultSet(ResultSet rset) {
		if (rset != null) {
			try {
				if(!rset.isClosed()){
					rset.close();
				}
			} catch (SQLException e) {
	     		throw new RepositoryException(e);
			}finally{
				rset = null;
			}
		}
	}
	
	public static void closeStatement(Statement stmt){
		if (stmt != null) {
			try {
				if(!stmt.isClosed()){
					stmt.close();
				}
			} catch (SQLException e) {
	     		throw new RepositoryException(e);
			}finally{
				stmt = null;
			}
		}
	}
	
	public static void closePreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				if(!pstmt.isClosed()){
					pstmt.close();
				}
			} catch (SQLException e) {
	     		throw new RepositoryException(e);
			}finally{
				pstmt = null;
			}
		}
	}
	
	public static void closeConnection() {
		Connection conn=conectionSession.get();
		if (conn != null) {
			try {
				//不是自动提交则不关闭连接
				if(conn.getAutoCommit()){
					if(!conn.isClosed()){
						conn.close();
					}
					conectionSession.remove();
				}
			} catch (SQLException e) {
	     		throw new RepositoryException(e);
			}finally{
				conn = null;
			}
		}
	}

	/**
	 * 打印SQL语句
	 */
	public static void logConsole(String SQL){
		log.info(SQL);
	}
	
}