package org.ei.dataloading.cafe;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.ei.dataloading.bd.loadtime.BaseTableRecord;

/**
 * 
 * @author TELEBH
 *
 */
public class CafeRecordMetaData {
	
	String key;
    String value;
    

	private static HashMap<String,String>  cafeRecordMetadata = new HashMap<String,String>();

	public static HashMap getCafeMetadaData()
	{
	    return cafeRecordMetadata;
	}

	public static String getValue(String key)
	{
		if(cafeRecordMetadata.containsKey(key))
		{
			return cafeRecordMetadata.get(key);
		}
		
		return null;
	}
	
	
	public static void SetKeyValue(String key, String value)
	{
	    
	    cafeRecordMetadata.put(key, value);
	    
	}


	

}
