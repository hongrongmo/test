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
		
		
		// Added for Author Profile
		public static final String AUID = "20";
		public static final String ORCID = "21";
		public static final String VARIANT_FIRST = "22";
		public static final String VARIANT_INI = "23";
		public static final String VARIANT_LAST = "24";
		public static final String PREFERRED_FIRST = "25";
		public static final String PREFERRED_INI = "26";
		public static final String PREFERRED_LAST = "27";
		public static final String FREQUENCY = "28";
		public static final String CODE = "29";
		public static final String CLASSIFICATION_SUBJABBR = "30";
		public static final String SUBJECT_CLUSTER = "31";
		public static final String PUBLICATION_RANGE_FIRST = "32";
		public static final String PUBLICATION_RANGE_LAST = "33";
		public static final String SOURCE_TITLE = "34";
		public static final String ISSN = "35";
		public static final String EMAIL_ADDRESS = "36";
		
		public static final String DISPLAY_NAME = "37";
		public static final String DISPLAY_CITY = "38";
		public static final String DISPLAY_COUNTRY = "39";
		public static final String AFFILIATION_HISTORY_ID = "40";
		public static final String HISTORY_DISPLAY_NAME = "41";
		public static final String HISTORY_CITY = "42";
		public static final String HISTORY_COUNTRY = "43";
		public static final String PARENT_AFFILIATION_ID = "44";
		public static final String NAME_ID = "45";
		//Added for the current affiliation_id (dept) for EV display instead of going to db
		public static final String CURRENT_DEPT_AFFILIATION_ID = "46";
		public static final String CURRENT_DEPT_AFFILIATION_DISPLAY_NAME = "47";
		public static final String CURRENT_DEPT_AFFILIATIOIN_CITY = "48";
		public static final String CURRENT_DEPT_AFFILIATION_COUNTRY = "49";
		
		
		
		
		
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
