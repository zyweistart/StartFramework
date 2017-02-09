package start.application.orm;

import java.io.Serializable;

public interface AbstractEntityManager {
	
	public void persist(Object entity);
	public long merge(Object entity);
	public long remove(Object entity);
	public <T> T load(Class<T> prototype, Serializable primaryKeyValue);
	
}
