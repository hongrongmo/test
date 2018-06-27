package org.ei.data.bd.runtime;

import java.util.ArrayList;
import org.ei.common.Constants;

//elt  y ch=30 ch=30 105-58-8 ch=31 y ch30 ch30 1333-86-4 ch=31 y24937-79-9y9000-11-7y96-49-1
//y 124-38-9 b 12795-06-1 (BT)//b8002-05-9 (BT)
//y105-58-8y1333-86-4y24937-79-9y9000-11-7y96-49-1// 2009
//y y ch=30 ch=30 10102-43-9b11104-93-1 (BT)y124-38-9b12624-32-7 (BT)
//AUDELIMITER = 30 IDDELIMITER = 31
// new xml format b AP 12795-06-1

public class CRNumStrategyEncompass
							extends CRNumStrategy
{

	public class CRNgroup
	{
		String prf = "";
		String psf = "";
		String crn = "";
		boolean broad = false;
		boolean broadembedded = false;
		boolean isCrn = false;

		public CRNgroup(String[] list)
		{
			String []crns = new String[3];
			if(list != null && list.length>0)
			{
				crns = list;
				prf =(String) crns[0].trim();

				if(crns.length > 2)
				{
					crn = (String) crns[2].trim();
					isCrn = true;
					if(!crn.equals("") && crn.indexOf("BT") > 0)
					{
						broadembedded = true;
						broad = true;
					}
					else if(prf.equals("b")) // new xml fromat
					{
						broad=true;
						broadembedded = false;

					}
				}

				if(list.length > 1)
				{
					psf = (String)crns[1].trim();
					if(!psf.equals("") && prf.equalsIgnoreCase("BT"))
					{
						broad = true;
					}
				}
			}
		}

		public String crnformatted()
		{
			StringBuffer buf = new StringBuffer();
			if(!crn.equals(""))
			{
				buf.append(crn);
			}
			else
			{
				isCrn = false;
				return null;
			}
			if(broad && !broadembedded)
			{
				buf.append("-").append("(BT)");
			}
			if(!psf.equals(""))
			{
				buf.append("-").append(psf);
			}
			return buf.toString();
		}
	}

	public CRNumStrategyEncompass(String crnumRaw)
	{
		super.crnumraw = crnumRaw;
	}

	public String[] crnAlgorithm()
	{
		if(crnumraw != null)
		{
			//removing asterics
			if(crnumraw.indexOf("*") > 0)
			{
				crnumraw = crnumraw.replaceAll("[*]","");
			}


			ArrayList list = new ArrayList();
			String[] multiStringArray =  crnumraw.split(Constants.IDDELIMITER,-1);

			if(crnumraw.length()>0)
			{
				for(int i = 0; i < multiStringArray.length; i++)
				{
					String[] multiStringArray2 = multiStringArray[i].split(Constants.AUDELIMITER,-1);

					CRNgroup crn = new CRNgroup(multiStringArray2);
					if(crn.isCrn)
					{
						list.add(crn.crnformatted());
					}
				}
			}
			return (String[]) list.toArray(new String[0]);
		}
		return null;
	}

	public String[] getCRN(int bitmask)
	{
		return null;
	}
}
