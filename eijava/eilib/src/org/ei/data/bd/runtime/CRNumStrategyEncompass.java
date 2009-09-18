package org.ei.data.bd.runtime;

import java.util.ArrayList;

import org.ei.data.bd.loadtime.BdParser;

public class CRNumStrategyEncompass 
							extends CRNumStrategy 
{
	
	public CRNumStrategyEncompass(String crnumRaw) {
		super.crnumraw = crnumRaw;
	}

	public String[] crnAlgorithm() 
	{
		if(crnumraw != null)
		{
			ArrayList list = new ArrayList();
			String[] multiStringArray =  crnumraw.split(BdParser.IDDELIMITER,-1);
			if(crnumraw.length()>0)
			{
				for(int i = 0; i < multiStringArray.length; i++)
				{
					String[] multiStringArray2 = multiStringArray[i].split(BdParser.AUDELIMITER,-1);	
					if(multiStringArray2.length == 3)
					{
						if(multiStringArray2[1] != null &&
								!multiStringArray2[1].equals(""))
						{
							if(multiStringArray2[2].indexOf(multiStringArray2[1])== -1)
							{
								if(multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b"))
								{
									list.add(multiStringArray2[2].concat("-").concat("(BT)").concat("-").concat(multiStringArray2[1]));
								}	
								else
								{
									list.add(multiStringArray2[2].concat("-").concat(multiStringArray2[1]));
								}
							}
							else
							{
								if(multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b"))
								{
									list.add(multiStringArray2[2].concat("-").concat("(BT)"));
								}
								else
								{
									list.add(multiStringArray2[2]);
								}
							}
						}						
						else
						{
							if(multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b"))
							{
								list.add(multiStringArray2[2].concat("-").concat("(BT)"));
							}
							list.add(multiStringArray2[2]);
						}
					}
				}
			}
			return (String[]) list.toArray(new String[0]);
		}
		return null;
	}



	public String[] getCRN(int bitmask) {
		
		return null;
	}



}
