package start.application.orm.entity;

import java.util.HashSet;
import java.util.Set;


/**
 * 实体成员
 */
public final class EntityInfo {
	/**
	 * 实体类名称 
	 */
	private String entityName;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 主键属性
	 */
	private EntityProperty primaryKeyMember;
	/**
	 * 普通成员
	 */
	private Set<EntityProperty> propertyMembers=new HashSet<EntityProperty>();
	
	/**
	 * 表明不映射为数据表字段的列
	 */
	private Set<EntityProperty> transientPropertyMembers=new HashSet<EntityProperty>();

	public String getEntityName() {
		return entityName;
	}
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public EntityProperty getPrimaryKeyMember() {
		return primaryKeyMember;
	}
	
	public void setPrimaryKeyMember(EntityProperty pMember) {
		this.primaryKeyMember = pMember;
	}


	public Set<EntityProperty> getPropertyMembers() {
		return propertyMembers;
	}
	
	public void setPropertyMembers(Set<EntityProperty> propertyMembers) {
		this.propertyMembers = propertyMembers;
	}
	
	public Set<EntityProperty> getTransientPropertyMembers() {
		return transientPropertyMembers;
	}
	
	public void setTransientPropertyMembers(
			Set<EntityProperty> transientPropertyMembers) {
		this.transientPropertyMembers = transientPropertyMembers;
	}

}