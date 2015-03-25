package org.ei.data.bd.runtime;

import java.util.ArrayList;
import org.ei.common.Constants;

public class CRNumStrategyBData extends CRNumStrategy
{
	//IDDELIMITER = 31
	//chm GROUPDELIMITER = 29
	//format in bd_master
	//chm   ch=31IDDELIMITER  aluminum ch=29GROUPDELIMITER 7429-90-5  ch=31IDDELIMITER zinc oxide ch=29GROUPDELIMITER 1314-13-2
	//parsing is identical to the prod site parsing (but seems to be en error - should be ch=31 field for desc CRN)

	public static String NOCRN = "";

	public CRNumStrategyBData(String crnumRaw)
	{
		super.crnumraw = crnumRaw;
	}

	public String[] crnAlgorithm()
	{

		if(crnumraw!=null && crnumraw.trim().length()>0)
		{
			String nu =  crnumraw.replaceAll(Constants.IDDELIMITER, ";");
			nu =  nu.replaceAll(Constants.GROUPDELIMITER, ";");
			String crn[] = nu.split(";");
			ArrayList array = new ArrayList();

			for(int i =  0 ; i< crn.length; i++ )
			{
				if(crn[i] != null && !crn[i].trim().equals(NOCRN) )
				{
					array.add((String)crn[i]);
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
