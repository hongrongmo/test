package org.ei.service.amazon.dynamodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.AmazonServiceHelper;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

/**
 * The Class AmazonDynamoDBServiceImpl.
 */
public class RuntimePropsDynamoDBServiceImpl implements AmazonDynamoDBService {

	private String tableName = DynamoDBTables.TABLE_RUNTIME_PROPERTIES;
	private String runlevel = "";

	public Map<String, String> getAllItems() throws ServiceException {

		Map<String,String> responseMap = new HashMap<String, String>();

		AmazonDynamoDBClient dynamoDB = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
		ScanRequest scan = new ScanRequest().withTableName(tableName);

		List<String> attributesToGet = new ArrayList<String>();
		attributesToGet.add(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_KEY);
		attributesToGet.add(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_DEFAULT);
		String runLevelColumn = null;
		if(runlevel != null && !runlevel.trim().equalsIgnoreCase("")){
			if(runlevel.equalsIgnoreCase(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_CERT)){
				runLevelColumn = DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_CERT;
			}else if(runlevel.equalsIgnoreCase(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_DEV)){
				runLevelColumn = DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_DEV;
			}else if(runlevel.equalsIgnoreCase(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_LOCAL)){
				runLevelColumn = DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_LOCAL;
            }else if(runlevel.equalsIgnoreCase(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_RELEASE)){
                runLevelColumn = DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_RELEASE;
            }else if(runlevel.equalsIgnoreCase(DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_PROD)){
                runLevelColumn = DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_PROD;
			}
			if(runLevelColumn != null){
				attributesToGet.add(runLevelColumn);
			}
		}
		scan.setAttributesToGet(attributesToGet);
		ScanResult results = dynamoDB.scan(scan);
		List<Map<String, AttributeValue>> items = results.getItems();


		for (Map<String, AttributeValue> item : items) {
			Iterator<String> it = item.keySet().iterator();
			String responseKey = "";
			String responseDefaultValue = null;
			String responseRunLevelValue = null;
			while (it.hasNext()) {
				String key = it.next();
				AttributeValue av = item.get(key);
				if (DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_KEY.equals(key))responseKey = av.getS();
				if (DynamoDBTables.COLUMN_RUNTIME_PROPERTIES_DEFAULT.equals(key))responseDefaultValue = av.getS();
				if (runLevelColumn != null && runLevelColumn.equals(key))responseRunLevelValue = av.getS();
			}
			String finalValue = null;
			if(responseDefaultValue != null){
				finalValue = responseDefaultValue.trim();
			}
			if(responseRunLevelValue != null){
				finalValue = responseRunLevelValue.trim();
			}
			responseMap.put(responseKey, finalValue);
		}

		return responseMap;
	}

	@Override
	public void setTableName(String tableName) {
		if(StringUtils.isNotBlank(tableName)){
			this.tableName = tableName;
		}
	}

	@Override
	public String getTableName() {
		return tableName;
	}
	@Override
	public String getRunLevel() {
		return runlevel;
	}
	@Override
	public void setRunLevel(String runlevel) {
		this.runlevel = runlevel;
	}


}
