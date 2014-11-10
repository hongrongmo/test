package org.ei.service.amazon.dynamodb;


public interface AmazonDynamoDBService {
	
	void setTableName(String name);
	String getTableName();
	
	void setRunLevel(String level);
	String getRunLevel();
}
