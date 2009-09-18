package org.ei.data.bd.runtime;

import java.util.ArrayList;

import org.ei.data.bd.loadtime.BdParser;

public class CRNumStrategyBData extends CRNumStrategy
{

	public CRNumStrategyBData(String crnumRaw) 
	{
		super.crnumraw = crnumRaw;
	}

	public String[] crnAlgorithm()
	{
		if(crnumraw!=null && crnumraw.trim().length()>0)
		{
			String nu =  crnumraw.replaceAll(BdParser.AUDELIMITER, ";");
			nu =  nu.replaceAll(BdParser.IDDELIMITER, ";");
			nu =  nu.replaceAll(BdParser.GROUPDELIMITER, ";");
			String cas[] = nu.split(";");
			ArrayList array = new ArrayList();

			for(int i =  0 ; i< cas.length; i++ )
			{
				if(cas[i] != null && !cas[i].trim().equals("") )
				{
					array.add((String)cas[i]);
				}
			}

			return (String[]) array.toArray(new String[1]);

		}
		else
		{
			return null;
		}
	}

	

}
