package org.ei.domain;

import java.util.HashMap;
import java.util.Map;

public class XSLCIDHelper {

    private static Map<String, String> searchResults = new HashMap<String, String>();
    static {
        searchResults.put("Book", "quickSearchCitationFormat");
        searchResults.put("Easy", "expertSearchCitationFormat");
        searchResults.put("Quick", "quickSearchCitationFormat");
        searchResults.put("Expert", "expertSearchCitationFormat");
        searchResults.put("Combined", "combineSearchHistory");
        searchResults.put("Thesaurus", "thesSearchCitationFormat");
        searchResults.put("TagSearch", "tagSearch");
    }

    public static String searchResultsCid(String searchType) {
        // <xsl:variable name="SEARCHRESULTSCID">
        if (searchType != null) {
            if (searchResults.containsKey(searchType)) {
                return (String) searchResults.get(searchType);
            }
        }
        // default (otherwise)
        return "expertSearchCitationFormat";
    }

    private static Map<String, String> newSearches = new HashMap<String, String>();
    static {
        newSearches.put("Book", "ebookSearch");
        newSearches.put("Easy", "easySearch");
        newSearches.put("Quick", "quickSearch");
        newSearches.put("Expert", "expertSearch");
        newSearches.put("Combined", "quickSearch");
        newSearches.put("Thesaurus", "thesHome");
        newSearches.put("TagSearch", "tagsLoginForm");

    }

    public static String newSearchCid(String searchType) {
        // <xsl:variable name="NEWSEARCHCID">
        if (searchType != null) {
            if (newSearches.containsKey(searchType)) {
                return (String) newSearches.get(searchType);
            }
        }
        // default (otherwise)
        return "expertSearch";
    }

    private static Map<String, String> formatBase = new HashMap<String, String>();
    static {
        // jam - eBookSearch is only used for initial eBook Form (new searches)
        // quickSearch is used to get Citation, Abstract and Detailed results
        formatBase.put("Book", "quickSearch");

        formatBase.put("Easy", "expertSearch");
        formatBase.put("Quick", "quickSearch");
        formatBase.put("Expert", "expertSearch");
        formatBase.put("Combined", "expertSearch");
        formatBase.put("Thesaurus", "quickSearch");
        formatBase.put("TagSearch", "tagSearch");
    }

    public static String formatBase(String searchType) {
        return (String) formatBase.get(searchType);
    }
}
