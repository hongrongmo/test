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
		//Added for both Author & Affiliation to QA using updatenumber
		public static final String UPDATE_NUMBER = "3";
		public static final String EID = "4";
		public static final String DOC_TYPE = "5";
		public static final String STATUS = "6";
		public static final String LOADDATE = "7";
		public static final String ITEMTRANSACTIONID = "8";
		public static final String INDEXEDDATE = "9";
		public static final String ESINDEXTIME = "10";
		public static final String AFID = "11";
		public static final String AFFILIATION_PREFERRED_NAME = "12";
		public static final String AFFILIATION_SORT_NAME = "13";
		public static final String AFFILIATION_VARIANT_NAME = "14";
		public static final String ADDRESS = "15";
		public static final String CITY = "16";
		public static final String STATE = "17";
		public static final String ZIP = "18";
		public static final String COUNTRY = "19";
		public static final String QUALITY = "20";
		public static final String CERTAINITY_SCORES = "21";
		
		
		// Added for Author Profile
		public static final String AUID = "22";
		public static final String ORCID = "23";
		public static final String VARIANT_FIRST = "24";
		public static final String VARIANT_INI = "25";
		public static final String VARIANT_LAST = "26";
		public static final String PREFERRED_FIRST = "27";
		public static final String PREFERRED_INI = "28";
		public static final String PREFERRED_LAST = "29";
		public static final String FREQUENCY = "30";
		public static final String CODE = "31";
		public static final String CLASSIFICATION_SUBJABBR = "32";
		public static final String SUBJECT_CLUSTER = "33";
		public static final String PUBLICATION_RANGE_FIRST = "34";
		public static final String PUBLICATION_RANGE_LAST = "35";
		public static final String SOURCE_TITLE = "36";
		public static final String ISSN = "37";
		public static final String EMAIL_ADDRESS = "38";
		
		public static final String DISPLAY_NAME = "39";
		public static final String DISPLAY_CITY = "40";
		public static final String DISPLAY_COUNTRY = "41";
		public static final String AFFILIATION_HISTORY_ID = "42";
		public static final String HISTORY_DISPLAY_NAME = "43";
		public static final String HISTORY_CITY = "44";
		public static final String HISTORY_COUNTRY = "45";
		public static final String PARENT_AFFILIATION_ID = "46";
		public static final String NAME_ID = "47";
		//Added for the current affiliation_id (dept) for EV display instead of going to db
		public static final String CURRENT_DEPT_AFFILIATION_ID = "48";
		public static final String CURRENT_DEPT_AFFILIATION_DISPLAY_NAME = "49";
		public static final String CURRENT_DEPT_AFFILIATIOIN_CITY = "50";
		public static final String CURRENT_DEPT_AFFILIATION_COUNTRY = "51";
		//added 05/24/2017 for both profiles as a place holder for future use (filled as "" now) as this holds SQS epoch not the one in profile
		public static final String UPDATEEPOCH = "52";
		//added 05/24/2017 only for Affiliation profile, but it not be indexed to ES bc it is already parent (only used fro chile which is not indexed in ES), parafid is set to "0"
		public static final String PARAFID = "53";
		public static final String PARENT_PREFERED_NAME = "54";
		// aftype will always set to "parent" because all indexed Affiliations are only parent ones
		public static final String AFTYPE = "55";
		

		
		
		
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
