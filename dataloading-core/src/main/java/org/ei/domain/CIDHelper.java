package org.ei.domain;

import java.util.HashMap;
import java.util.Map;

public class CIDHelper {

	private static Map<String, String> searchResults = new HashMap<String, String>();
	static
	{
		searchResults.put("Book","quickSearchCitationFormat");
		searchResults.put("Easy","expertSearchCitationFormat");
		searchResults.put("Quick","quickSearchCitationFormat");
		searchResults.put("Expert","expertSearchCitationFormat");
		searchResults.put("Combined","combineSearchHistory");
		searchResults.put("Thesaurus","thesSearchCitationFormat");
		searchResults.put("TagSearch","tagSearch");
	}

	public static String searchResultsCid(String searchType)
	{
		if(searchType != null)
		{
			if(searchResults.containsKey(searchType))
			{
				return (String) searchResults.get(searchType);
			}
		}
		return "expertSearchCitationFormat";
	}

	private static Map<String, String> newSearches = new HashMap<String, String>();
	static
	{
		newSearches.put("Book","ebookSearch");
		newSearches.put("Easy","easySearch");
		newSearches.put("Quick","quickSearch");
		newSearches.put("Expert","expertSearch");
		newSearches.put("Combined","quickSearch");
		newSearches.put("Thesaurus","thesHome");
		newSearches.put("TagSearch","tagsLoginForm");

	}

	public static String newSearchCid(String searchType)
	{
		if(searchType != null)
		{
			if(newSearches.containsKey(searchType))
			{
				return (String) newSearches.get(searchType);
			}
		}
		return "expertSearch";
	}

	private static Map<String, String> formatBase = new HashMap<String, String>();
	static
	{
		formatBase.put("Book","quickSearch");

		formatBase.put("Easy","expertSearch");
		formatBase.put("Quick","quickSearch");
		formatBase.put("Expert","expertSearch");
		formatBase.put("Combined","expertSearch");
		formatBase.put("Thesaurus","quickSearch");
		formatBase.put("TagSearch","tagSearch");
	}

	public static String formatBase(String searchType)
	{
		return (String) formatBase.get(searchType);
	}
}
