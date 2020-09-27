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
	Map<String,String> searchFacetFields = new HashMap<>();
	
	public SearchFields()
	{
		setupFields();
		setupFacetFields();
	}
	public String getSearchField(String key)
	{
		if(searchFields.containsKey(key))
			return searchFields.get(key);
		else
			return "";
	}
	public String getFacetField(String key)
	{
		if(searchFacetFields.containsKey(key))
			return searchFacetFields.get(key);
		else
			return "";
	}
	private void setupFields()
	{
		searchFields.put("auid", "authorId");
		searchFields.put("afid","affiliationId");
		searchFields.put("updatetime", "updateTime");
		searchFields.put("batchinfo", "batchInfo");
		searchFields.put("updatetime", "updateTime");
		searchFields.put("wk", "loadNumber");
		searchFields.put("updatenumber", "updateNumber");
		searchFields.put("ab", "abstract");
		searchFields.put("doi", "doi");
		searchFields.put("yr", "publicationYear");
		searchFields.put("ti", "title");
		searchFields.put("au", "authorGroup.phrase");
		searchFields.put("af", "authorEditorAffiliation.agg");
		searchFields.put("cv", " controlledVocabularyGroup.exact");
		searchFields.put("st", "serialTitles.agg");
		searchFields.put("pn", "publisherName.agg");
		searchFields.put("pid", "inPatentClassification.agg");
		searchFields.put("pc", "authorityCode.agg");
		searchFields.put("an", "accessionNumber");
		searchFields.put("pi", "processInfo");
		
		
	}
	
	private void setupFacetFields()
	{
		searchFacetFields.put("auid", "authorId");
		searchFacetFields.put("afid","affiliationId");
		searchFacetFields.put("batchinfo", "batchInfo");
		searchFacetFields.put("db", "database");
		
		
	}
}
