package start.application.orm.support.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public interface MongoDBDatasource {
	
	public MongoClient getClient();
	
	public MongoDatabase getDataBase();

}
