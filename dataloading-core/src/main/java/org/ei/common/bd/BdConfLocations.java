
package org.ei.common.bd;

import java.util.*;

import org.ei.common.CountryFormatter;
import org.ei.common.bd.BdAffiliations.AffIDComp;
//import org.ei.data.bd.loadtime.*;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BdConfLocations
{
    private BdAffiliations affs ;
    private static ArrayList  caffElements = new ArrayList();

    static
    {
        caffElements.add("affid");
        caffElements.add("affVenus");
        caffElements.add("affText");
        caffElements.add("affOrganization");
        caffElements.add("affAddressPart");
        caffElements.add("affCityGroup");
        caffElements.add("affCountry");
    }

    public BdConfLocations(String bdCAffiliations)
    {
        this.affs = new BdAffiliations(bdCAffiliations,caffElements);
    }


	public List getAffiliations()
	{
		return this.affs.getAffiliations();
	}


    public String getDisplayValue()
	{
		List bdConfAff = this.affs.getAffiliations();
		StringBuffer affBuf = new StringBuffer();
		if (bdConfAff != null)
		{
			for (int i=0;i<bdConfAff.size();i++)
			{
				BdAffiliation aff = (BdAffiliation)bdConfAff.get(i);

				if(aff.getAffVenue()!=null && aff.getAffVenue().trim().length()>0)
				{
					affBuf.append(aff.getAffVenue());
				}

				if(aff.getAffText()!=null)
				{
					if(affBuf.length()>0)
					{
						affBuf.append(", ");
					}
					affBuf.append(aff.getAffText());
				}

			   if(aff.getAffOrganization()!=null)
			   {
				   if(affBuf.length()>0)
				   {
						affBuf.append(", ");
				   }
				   affBuf.append(aff.getAffOrganization());
			   }

			   if(aff.getAffAddressPart()!=null)
			   {
				   if(affBuf.length()>0)
				   {
						affBuf.append(", ");
				   }
				   affBuf.append(aff.getAffAddressPart());

			   }

			   if(aff.getAffCityGroup()!=null)
			   {
				   if(affBuf.length()>0)
				   {
						affBuf.append(", ");
				   }
				   affBuf.append(aff.getAffCityGroup());
			   }

			   if(aff.getAffCountry()!=null)
			   {
				   if(affBuf.length()>0)
				   {
						affBuf.append(", ");
				   }
				   affBuf.append(CountryFormatter.formatCountry(aff.getAffCountry()));
			   }
			}
		}
		if(affBuf.length() > 0)
		{
			return affBuf.toString();
		}
		else
		{
			return null;
		}
	}

    public String getSearchValue()
	{
    	LinkedHashMap bdAffiliationsMap = this.affs.getAffMap();
    	//List affList = new ArrayList();
    	StringBuffer affBuf = new StringBuffer();
     	if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
    	{
    	    Iterator affenum = bdAffiliationsMap.keySet().iterator();
    	    while (affenum.hasNext())
    	    {
    	        BdAffiliation aff = (BdAffiliation)affenum.next();

    	        if(aff.getAffVenue()!=null && aff.getAffVenue().trim().length()>0)
    	        {
    	            affBuf.append(aff.getAffVenue());
    	            affBuf.append(" ");
    	        }

    	        if(aff.getAffText()!=null)
    	        {
    	            affBuf.append(aff.getAffText());
    	            affBuf.append(" ");
    	        }

    	       if(aff.getAffOrganization()!=null)
    	       {
    	           affBuf.append(aff.getAffOrganization());
    	           affBuf.append(" ");
    	       }

    	       if(aff.getAffAddressPart()!=null)
    	       {

    	           affBuf.append(aff.getAffAddressPart());
    	           affBuf.append(" ");
    	       }

    	       if(aff.getAffCityGroup()!=null)
    	       {
    	           affBuf.append(aff.getAffCityGroup());
    	           affBuf.append(" ");
    	       }

    	       if(aff.getAffCountry()!=null)
    	       {
    	           affBuf.append(CountryFormatter.formatCountry(aff.getAffCountry()));
    	           affBuf.append(" ");
    	       }
    		}
    	}
    	if(affBuf.length() > 0)
    	{
    	    return affBuf.toString();
    	    //return (String[]) affList.toArray(new String[1]);
    	}
    	else
    	{
    	    return null;
    	}
	}
}
