package org.ei.dataloading.cafe;

import java.util.Hashtable;

/**
 * 
 * @author TELEBH
 * @date: 06/21/2016
 * @description: combined ElasticSearch Record for Cafe Author and Affiliation profiles 
 */
public class AuAfCombinedRec {
	
	// Affiliation Profile Fields
		public static final String DOCID = "1";
		public static final String LOAD_NUMBER = "2";
		public static final String EID = "3";
		public static final String DOC_TYPE = "4";
		public static final String STATUS = "5";
		public static final String LOADDATE = "6";
		public static final String ITEMTRANSACTIONID = "7";
		public static final String INDEXEDDATE = "8";
		public static final String ESINDEXTIME = "9";
		public static final String AFID = "10";
		public static final String AFFILIATION_PREFERRED_NAME = "11";
		public static final String AFFILIATION_SORT_NAME = "12";
		public static final String AFFILIATION_VARIANT_NAME = "13";
		public static final String ADDRESS = "14";
		public static final String CITY = "15";
		public static final String STATE = "16";
		public static final String ZIP = "17";
		public static final String COUNTRY = "18";
		public static final String QUALITY = "19";
		public static final String CERTAINITY_SCORES = "20";
		
		
		// Added for Author Profile
		public static final String AUID = "21";
		public static final String ORCID = "22";
		public static final String VARIANT_FIRST = "23";
		public static final String VARIANT_INI = "24";
		public static final String VARIANT_LAST = "25";
		public static final String PREFERRED_FIRST = "26";
		public static final String PREFERRED_INI = "27";
		public static final String PREFERRED_LAST = "28";
		public static final String FREQUENCY = "29";
		public static final String CODE = "30";
		public static final String CLASSIFICATION_SUBJABBR = "31";
		public static final String SUBJECT_CLUSTER = "32";
		public static final String PUBLICATION_RANGE_FIRST = "33";
		public static final String PUBLICATION_RANGE_LAST = "34";
		public static final String SOURCE_TITLE = "35";
		public static final String ISSN = "36";
		public static final String EMAIL_ADDRESS = "37";
		
		public static final String DISPLAY_NAME = "38";
		public static final String DISPLAY_CITY = "39";
		public static final String DISPLAY_COUNTRY = "40";
		public static final String AFFILIATION_HISTORY_ID = "41";
		public static final String HISTORY_DISPLAY_NAME = "42";
		public static final String HISTORY_CITY = "43";
		public static final String HISTORY_COUNTRY = "44";
		public static final String PARENT_AFFILIATION_ID = "45";
		public static final String NAME_ID = "46";
		//Added for the current affiliation_id (dept) for EV display instead of going to db
		public static final String CURRENT_DEPT_AFFILIATION_ID = "47";
		public static final String CURRENT_DEPT_AFFILIATION_DISPLAY_NAME = "48";
		public static final String CURRENT_DEPT_AFFILIATIOIN_CITY = "49";
		public static final String CURRENT_DEPT_AFFILIATION_COUNTRY = "50";
		
		
		
		
		
		Hashtable <String,String> h = new Hashtable<String,String>();
		
		public void put(String key, String value)
		{
			h.put(key, value);
		}
		
		public String getString(String key)
		{
			String str = h.get(key);
			return str;
		}

}
