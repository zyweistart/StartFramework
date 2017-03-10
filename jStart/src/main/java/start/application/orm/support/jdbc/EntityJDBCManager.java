package start.application.orm.support.jdbc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.context.ApplicationIO;
import start.application.core.constant.Message;
import start.application.core.utils.StringHelper;
import start.application.orm.AbstractEntityManager;
import start.application.orm.annotation.GeneratedValue;
import start.application.orm.annotation.Id;
import start.application.orm.context.ContextCacheEntity;
import start.application.orm.entity.EntityInfo;
import start.application.orm.entity.EntityProperty;
import start.application.orm.exceptions.RepositoryException;

/**
 * 实体管理器
 * 
 * @author Start
 */
public class EntityJDBCManager implements AbstractEntityManager {

	private static final String INSERT="INSERT INTO %s(%s) VALUES(%s)";
	private static final String UPDATE="UPDATE %s SET %s WHERE %s";
	private static final String DELETE="DELETE FROM %s WHERE %s";
	private static final String SELECT="SELECT * FROM %s WHERE %s";
	
	private SessionManager session;

	@Override
	public void persist(Object entity) {
		EntityInfo entityMember = ContextCacheEntity.getEntity(entity.getClass());
		// 主键值
		Object primaryKeyValue = null;
		List<String> fieldNames = new ArrayList<String>();
		List<String> positions = new ArrayList<String>();
		List<Object> parameters = new ArrayList<Object>();
		Id id = (Id) entityMember.getPrimaryKeyMember().getField().getAnnotation(Id.class);
		if (id.value() == GeneratedValue.UID) {
			// 如果为UID生成策略则自动生成一个UID值
			primaryKeyValue = StringHelper.random();
			try {
				entityMember.getPrimaryKeyMember().getSet().invoke(entity, primaryKeyValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RepositoryException(e);
			}
		} else if (id.value() == GeneratedValue.NONE) {
			// 获取已经设置的主键值
			try {
				primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RepositoryException(e);
			}
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			throw new RepositoryException(message);
		} else {
			// 主键字段
			fieldNames.add(entityMember.getPrimaryKeyMember().getFieldName());
			positions.add("?");
			parameters.add(
					ApplicationIO.write(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		}
		// 其它字段
		for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
			Object value=null;
			try {
				value = propertyMember.getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RepositoryException(e);
			}
			// 为空则不插入
			if (value == null) {
				continue;
			}
			fieldNames.add(propertyMember.getFieldName());
			positions.add("?");
			parameters.add(ApplicationIO.write(propertyMember.getField(), value));
		}
		// 生成语句
		String SQL = String.format(INSERT, entityMember.getTableName(),
				StringHelper.listToString(fieldNames), StringHelper.listToString(positions));
		try {
			getSession().executeUpdate(SQL, parameters.toArray());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public long merge(Object entity) {
		EntityInfo entityMember = ContextCacheEntity.getEntity(entity.getClass());
		Object primaryKeyValue=null;
		try {
			primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RepositoryException(e);
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			throw new RepositoryException(message);
		}
		List<String> fieldNames = new ArrayList<String>();
		List<String> whereFieldNames = new ArrayList<String>();
		List<Object> parameters = new ArrayList<Object>();
		// 其它字段
		for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
			Object value=null;
			try {
				value = propertyMember.getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RepositoryException(e);
			}
			// 为空则不插入
			if (value == null) {
				continue;
			}
			fieldNames.add(propertyMember.getFieldName() + "=?");
			parameters.add(ApplicationIO.write(propertyMember.getField(), value));
		}
		// 主键字段
		whereFieldNames.add(entityMember.getPrimaryKeyMember().getFieldName() + "=?");
		parameters.add(ApplicationIO.write(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		// 生成语句
		String SQL = String.format(UPDATE, entityMember.getTableName(),
				StringHelper.listToString(fieldNames), StringHelper.listToString(whereFieldNames));
		try {
			return getSession().executeUpdate(SQL, parameters.toArray());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public long remove(Object entity) {
		Class<?> prototype = entity.getClass();
		EntityInfo entityMember = ContextCacheEntity.getEntity(prototype);
		Object primaryKeyValue=null;
		try {
			primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RepositoryException(e);
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			throw new RepositoryException(message);
		}
		// 主键字段
		List<String> whereFieldNames = new ArrayList<String>();
		whereFieldNames.add(entityMember.getPrimaryKeyMember().getFieldName() + "=?");
		// 生成语句
		String SQL = String.format(DELETE, entityMember.getTableName(),
				StringHelper.listToString(whereFieldNames));
		primaryKeyValue = ApplicationIO.write(entityMember.getPrimaryKeyMember().getField(),
				primaryKeyValue);
		try {
			return getSession().executeUpdate(SQL, primaryKeyValue);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	/////////////////////////////////////// Select///////////////////////////////////////////////////
	@Override
	public <T> T load(Class<T> prototype, Serializable primaryKeyValue) {
		EntityInfo entityMember = ContextCacheEntity.getEntity(prototype);
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			throw new RepositoryException(message);
		}
		// 主键字段
		List<String> whereFieldNames = new ArrayList<String>();
		whereFieldNames.add(entityMember.getPrimaryKeyMember().getFieldName() + "=?");
		// 生成语句
		String SQL = String.format(SELECT, entityMember.getTableName(),
				StringHelper.listToString(whereFieldNames));
		List<T> entitys = getListEntityBySQL(prototype, SQL,
				ApplicationIO.write(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		if (entitys.size() > 0) {
			return entitys.get(0);
		} else {
			return null;
		}
	}

	public List<Map<String, String>> getListMapBySQL(String sql, Object... params) {
		SessionManager.logConsole(sql);
		ResultSet rSet = null;
		PreparedStatement pStatement = null;
		Connection conn=null;
		try {
			conn=getSession().getConnection();
			pStatement=conn.prepareStatement(sql);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					pStatement.setObject(i+1, params[i]);
				}
			}
			rSet = pStatement.executeQuery();
			List<Map<String, String>> entitys = new ArrayList<Map<String, String>>();
			while (rSet.next()) {
				Map<String, String> en = new HashMap<String, String>();
				ResultSetMetaData resultSetMetaData = rSet.getMetaData();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					String fieldName = resultSetMetaData.getColumnName(i);
					en.put(fieldName.toUpperCase(), StringHelper.nullToStr(rSet.getString(fieldName)));
				}
				entitys.add(en);
			}
			return entitys;
		} catch (SQLException e) {
			String message=Message.getMessage(Message.PM_5009, sql, e.getMessage()); 
			throw new RepositoryException(message);
		} finally {
			SessionManager.closeResultSet(rSet);
			SessionManager.closePreparedStatement(pStatement);
			SessionManager.closeConnection();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getListEntityBySQL(Class<T> prototype, String sql, Object... params) {
		List<Map<String, String>> entitys = getListMapBySQL(sql, params);
		List<T> tEntitys = new ArrayList<T>();
		if (entitys.isEmpty()) {
			return tEntitys;
		}
		// 把List,Map组成装对象
		EntityInfo entityMember = ContextCacheEntity.getEntity(prototype);
		for (Map<String, String> en : entitys) {
			Object obj;
			try {
				obj = prototype.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				String message=Message.getMessage(Message.PM_5009, sql, e.getMessage()); 
				throw new RepositoryException(message);
			}
			// 主键
			String value = en.get(entityMember.getPrimaryKeyMember().getFieldName());
			Object tarValue = ApplicationIO.read(entityMember.getPrimaryKeyMember().getField(), value);
			if (tarValue != null) {
				try {
					entityMember.getPrimaryKeyMember().getSet().invoke(obj, tarValue);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					String message=Message.getMessage(Message.PM_5009, sql, e.getMessage()); 
					throw new RepositoryException(message);
				}
			}
			// 类字段成员
			for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
				value = en.get(propertyMember.getFieldName());
				tarValue = ApplicationIO.read(propertyMember.getField(), value);
				if (tarValue != null) {
					try {
						propertyMember.getSet().invoke(obj, tarValue);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						String message=Message.getMessage(Message.PM_5009, sql, e.getMessage()); 
						throw new RepositoryException(message);
					}
				}
			}
			tEntitys.add((T) obj);
		}
		return tEntitys;
	}

	//////////////////////////////////////////////////////////////// end
	//////////////////////////////////////////////////////////////// select/////////////////////////////////////////////////////////////////////
	/**
	 * 数据操作管理类
	 */
	public SessionManager getSession() {
		return session;
	}
	
	public void setSession(SessionManager session) {
		this.session = session;
	}

}