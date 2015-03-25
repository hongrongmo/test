package org.ei.domain;

import java.util.Hashtable;


public class DocumentTypeConverter
{
	static Hashtable geoDtMappings = new Hashtable();
	static
	{
		geoDtMappings.put("B","MR");
		geoDtMappings.put("D","JA");
		geoDtMappings.put("J","JA");
		geoDtMappings.put("P","CA");
    }

	public static String getGeoDocumentType(String sourceType)
	{
		String dtType = (String)geoDtMappings.get(sourceType.toUpperCase());

		if(dtType==null)
		{
			dtType = sourceType;
		}
		return dtType;
	}
}
