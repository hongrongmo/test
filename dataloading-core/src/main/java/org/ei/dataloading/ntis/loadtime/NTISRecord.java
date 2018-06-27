package org.ei.dataloading.ntis.loadtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class NTISRecord
	extends Hashtable
{
	private String serialNumber;
	private String data;
	private String Authors;
	private String MAAcronym;
	private String MANumber;


	public static List startswiththree;

	static
	{

		startswiththree = new ArrayList();
		startswiththree.add("PA2");
		startswiththree.add("PA3");
		startswiththree.add("PA4");
		startswiththree.add("PA5");
		startswiththree.add("MAA2");
		startswiththree.add("MAN2");
	}




	public NTISRecord(String serialNumber,
			  String data)
		throws IOException
	{
		this.serialNumber = serialNumber;
		this.data = data;
		int fieldNumber = 1;
		//System.out.println(data);
		StringTokenizer tokens = new StringTokenizer(data, "[");
		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			token = token.trim();
			if(token.length() > 0)
			{
				String field = NTISBaseTableRecord.baseTableFields[fieldNumber];
			    //System.out.println(token);
				if(startswiththree.contains(field))
				{
					token = token.substring(3);
				}
				else
				{
					token = token.substring(2);
				}

				put(field,token);
				fieldNumber++;
			}
		}
	}



}
