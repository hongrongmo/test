package org.ei.dataloading.cafe;

import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * 
 * @author TELEBH
 * @date: 04/25/2019
 * @description: Affiliation Profile Record for DeanDashboard/Engineering School Profile
 */

public class InstitutionRec 
{

	// Institution Profile Fields
			public static final String INSTITUTION_ID = "1";
			public static final String INSTITUTION_NAME = "2";
			public static final String INSTITUTION_DOC_COUNT = "3";
			public static final String INSTITUTION_COUNTRY = "4";
			public static final String INSTITUTION_CONTINENT = "5";
			public static final String AFFILIATIONS = "6";
			
			LinkedHashMap <String,String> h = new LinkedHashMap<String,String>();
			
			public void put(String key, String value)
			{
				h.put(key, value);
			}
			
			public String getString(String key)
			{
				String str = h.get(key);
				return str;
			}
			public int getSize()
			{
				return h.size();
			}
			
}
