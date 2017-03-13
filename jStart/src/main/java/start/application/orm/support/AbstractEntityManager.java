package start.application.orm.support;

import java.io.Serializable;

public abstract class AbstractEntityManager {
	
	public abstract void persist(Object entity);
	public abstract long merge(Object entity);
	public abstract long remove(Object entity);
	public abstract <T> T load(Class<T> prototype, Serializable primaryKeyValue);
	
}
