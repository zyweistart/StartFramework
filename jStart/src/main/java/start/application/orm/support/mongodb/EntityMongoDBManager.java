package start.application.orm.support.mongodb;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import start.application.commons.logger.Logger;
import start.application.commons.logger.LoggerFactory;
import start.application.context.ContextObject;
import start.application.context.ContextDataReadWrite;
import start.application.core.Message;
import start.application.core.utils.StackTraceInfo;
import start.application.core.utils.StringHelper;
import start.application.orm.AbstractEntityManager;
import start.application.orm.annotation.GeneratedValue;
import start.application.orm.annotation.Id;
import start.application.orm.entity.EntityInfo;
import start.application.orm.entity.EntityProperty;
import start.application.orm.exceptions.RepositoryException;

public class EntityMongoDBManager implements AbstractEntityManager {

	private final static Logger log=LoggerFactory.getLogger(EntityMongoDBManager.class);

	private MongoDBDatasource session;

	@Override
	public void persist(Object entity) {
		EntityInfo entityMember = ContextObject.getEntitys(entity.getClass());
		// 主键值
		Object primaryKeyValue = null;
		MongoCollection<Document> dbCollection = getSession().getDataBase().getCollection(entityMember.getTableName());
		Document doc = new Document();
		Id id = (Id) entityMember.getPrimaryKeyMember().getField().getAnnotation(Id.class);
		if (id.value() == GeneratedValue.UID) {
			// 如果为UID生成策略则自动生成一个UID值
			primaryKeyValue = StringHelper.random();
			try {
				entityMember.getPrimaryKeyMember().getSet().invoke(entity, primaryKeyValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new RepositoryException(e);
			}
		} else if (id.value() == GeneratedValue.NONE) {
			// 获取已经设置的主键值
			try {
				primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new RepositoryException(e);
			}
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			log.error(StackTraceInfo.getTraceInfo() + message);
			throw new RepositoryException(message);
		} else {
			// 主键字段
			doc.put(entityMember.getPrimaryKeyMember().getFieldName(),
					ContextDataReadWrite.convertWriteOut(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		}
		// 其它字段
		for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
			Object value=null;
			try {
				value = propertyMember.getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new RepositoryException(e);
			}
			// 为空则不插入
			if (value == null) {
				continue;
			}
			doc.put(propertyMember.getFieldName(), ContextDataReadWrite.convertWriteOut(propertyMember.getField(), value));
		}
		logDocument(doc);
		dbCollection.insertOne(doc);
	}

	@Override
	public long merge(Object entity) {
		EntityInfo entityMember = ContextObject.getEntitys(entity.getClass());
		Object primaryKeyValue=null;
		try {
			primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new RepositoryException(e);
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			log.error(StackTraceInfo.getTraceInfo() + message);
			throw new RepositoryException(message);
		}
		MongoCollection<Document> dbCollection = getSession().getDataBase()
				.getCollection(entityMember.getTableName());
		Document updateDoc = new Document();
		Document whereDoc = new Document();
		// 其它字段
		for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
			Object value=null;
			try {
				value = propertyMember.getGet().invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new RepositoryException(e);
			}
			// 为空则不插入
			if (value == null) {
				continue;
			}
			updateDoc.put(propertyMember.getFieldName(), ContextDataReadWrite.convertWriteOut(propertyMember.getField(), value));
		}
		whereDoc.put(entityMember.getPrimaryKeyMember().getFieldName(),
				ContextDataReadWrite.convertWriteOut(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		logDocument(whereDoc);
		logDocument(updateDoc);
		UpdateResult result=dbCollection.updateOne(whereDoc, new Document("$set",updateDoc));
		return result.getModifiedCount();
	}

	@Override
	public long remove(Object entity) {
		Class<?> prototype = entity.getClass();
		EntityInfo entityMember = ContextObject.getEntitys(prototype);
		Object primaryKeyValue=null;
		try {
			primaryKeyValue = entityMember.getPrimaryKeyMember().getGet().invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
			throw new RepositoryException(e);
		}
		// 主键不能为空
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			log.error(StackTraceInfo.getTraceInfo() + message);
			throw new RepositoryException(message);
		}
		MongoCollection<Document> dbCollection = getSession().getDataBase()
				.getCollection(entityMember.getTableName());
		Document doc = new Document();
		doc.put(entityMember.getPrimaryKeyMember().getFieldName(),
				ContextDataReadWrite.convertWriteOut(entityMember.getPrimaryKeyMember().getField(), primaryKeyValue));
		logDocument(doc);
		DeleteResult result=dbCollection.deleteOne(doc);
		return result.getDeletedCount();
	}

	@Override
	public <T> T load(Class<T> prototype, Serializable primaryKeyValue) {
		EntityInfo entityMember = ContextObject.getEntitys(prototype);
		if (primaryKeyValue == null) {
			String message = Message.getMessage(Message.PM_5001, entityMember.getEntityName());
			log.error(StackTraceInfo.getTraceInfo() + message);
			throw new RepositoryException(message);
		}
		Document doc = new Document();
		doc.put(entityMember.getPrimaryKeyMember().getFieldName(), primaryKeyValue);
		List<T> entitys=getListEntity(prototype, doc);
		if(!entitys.isEmpty()){
			return entitys.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getListEntity(Class<T> prototype, Document doc) {
		// 把List,Map组成装对象
		EntityInfo entityMember = ContextObject.getEntitys(prototype);
		List<Map<String, String>> entitys = getListMap(entityMember.getTableName(),doc);
		List<T> tEntitys = new ArrayList<T>();
		if (entitys.isEmpty()) {
			return tEntitys;
		}
		for (Map<String, String> en : entitys) {
			Object obj;
			try {
				obj = prototype.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
				throw new RepositoryException(e);
			}
			// 主键
			String value = en.get(entityMember.getPrimaryKeyMember().getFieldName());
			Object tarValue = ContextDataReadWrite.convertReadIn(entityMember.getPrimaryKeyMember().getField(), value);
			if (tarValue != null) {
				try {
					entityMember.getPrimaryKeyMember().getSet().invoke(obj, tarValue);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
					throw new RepositoryException(e);
				}
			}
			// 类字段成员
			for (EntityProperty propertyMember : entityMember.getPropertyMembers()) {
				value = en.get(propertyMember.getFieldName());
				tarValue = ContextDataReadWrite.convertReadIn(propertyMember.getField(), value);
				if (tarValue != null) {
					try {
						propertyMember.getSet().invoke(obj, tarValue);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						log.error(StackTraceInfo.getTraceInfo() + e.getMessage());
						throw new RepositoryException(e);
					}
				}
			}
			tEntitys.add((T) obj);
		}
		return tEntitys;
	}
	
	public List<Map<String, String>> getListMap(String tableName,Document doc) {
		MongoCollection<Document> dbCollection = getSession().getDataBase().getCollection(tableName);
		FindIterable<Document> iterable = dbCollection.find(doc);
		final List<Map<String, String>> mapDatas = new ArrayList<Map<String, String>>();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				Map<String,String> data=new HashMap<String,String>();
				for(String key:document.keySet()){
					data.put(key, String.valueOf(document.get(key)));
				}
				mapDatas.add(data);
			}
		});
		return mapDatas;
	}

	public MongoDBDatasource getSession() {
		return session;
	}

	public void setSession(MongoDBDatasource session) {
		this.session = session;
	}
	
	public void logDocument(Document doc){
		log.info(doc.toString());
	}
	
}