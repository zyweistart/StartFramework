package start.application.orm.support.redis;

import java.io.Serializable;

import start.application.orm.AbstractEntityManager;

public class EntityRedisManager implements AbstractEntityManager {

	@Override
	public void persist(Object entity) {
		
	}

	@Override
	public long merge(Object entity) {
		return 0;
	}

	@Override
	public long remove(Object entity) {
		return 0;
	}

	@Override
	public <T> T load(Class<T> prototype, Serializable primaryKeyValue) {
		return null;
	}

}
