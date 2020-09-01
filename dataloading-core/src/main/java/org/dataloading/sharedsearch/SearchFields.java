package org.dataloading.sharedsearch;

/**
 * 
 * @author TELEBH
 * @Date: 06/17/2020
 * @Description: SearchFields mapping to have somesort 
 * of flexibility
 */

import java.util.Map;
import java.util.HashMap;

public class SearchFields {

	Map<String,String> searchFields = new HashMap<>();
	
	public SearchFields()
	{
		setupFields();
	}
	public String getSearchField(String key)
	{
		if(searchFields.containsKey(key))
			return searchFields.get(key);
		else
			return "";
	}
	private void setupFields()
	{
		searchFields.put("auid", "authorId");
		searchFields.put("afid","affiliationId");
		searchFields.put("batchInfo", "batchInfo");
		searchFields.put("updateTime", "updateTime");
	}
}
