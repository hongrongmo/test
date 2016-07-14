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
		public static final String DISPLAY_NAME = "20";
		public static final String DIDPLAY_CITY = "21";
		public static final String DISPLAY_COUNTRY = "22";
		public static final String AFFILIATION_HISTORY_ID = "23";
		public static final String HISTORY_DISPLAY_NAME = "24";
		public static final String HISTORY_CITY = "25";
		public static final String HISTORY_COUNTRY = "26";
		public static final String PARENT_AFFILIATION_ID = "27";
		public static final String NAME_ID = "28";
		
		
		
		
		
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
