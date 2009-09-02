
package org.ei.data.inspec.runtime;

import java.util.*;

import org.ei.data.CountryFormatter;
//import org.ei.data.bd.BdAffiliations.AffIDComp;
import org.ei.data.bd.BdAffiliation;
import org.ei.data.bd.BdAffiliations;
import org.ei.data.bd.loadtime.*;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InspecAffiliations
{
    private BdAffiliations affs ;
    private static ArrayList  affElements = new ArrayList();
    private static ArrayList  affElementsExpanded = new ArrayList();
    private boolean isExpanded = false;

    static
    {
    	affElements.add("affOrganization");
        affElements.add("affid");            
        affElements.add("affCountry");
    }
    
    static
    {
    	affElementsExpanded.add("affOrganization");
        affElementsExpanded.add("affid");              
        affElementsExpanded.add("affDepartment");
        affElementsExpanded.add("affAddressPart");
        affElementsExpanded.add("affCityGroup");
        affElementsExpanded.add("affCity");
        affElementsExpanded.add("affState");
        affElementsExpanded.add("affPostalCode");
        affElementsExpanded.add("affCountry");
        affElementsExpanded.add("affOrganisationIdentifier");
    }

    public InspecAffiliations(String insAffiliations)
    {

    	this.affs = new BdAffiliations(formatAffStr(insAffiliations));
    }
    
    
    public String formatAffStr(String dAffs)
    {
    	String [] bdaf =  dAffs.split(BdParser.IDDELIMITER);

    	if(bdaf.length > 3)
    	{
    		isExpanded = true;
    	}
    	
    	StringBuffer formatedData = new StringBuffer();
    	
    	String[] daffs = dAffs.split(BdParser.AUDELIMITER);   	
    	for(int i = 0; i < daffs.length; i++)
    	{
    		if(daffs[i] != null && daffs[i].length() > 0)
    		{
    			String []daff = daffs[i].split(BdParser.IDDELIMITER);
    			if(daff != null && daff.length > 0)
    			{
    				if(!isExpanded)
    				{
    					if(daff.length >1 && daff[1] != null)
    					{
    						formatedData.append(daff[1]);
    					}    				
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >0 && daff[0] != null)
    					{
    						formatedData.append(daff[0]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >2 && daff[2] != null)
    					{
    						formatedData.append(daff[2]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);   		
    				}
    				else
    				{
    					if(daff.length >1 && daff[1] != null)
    					{
    						formatedData.append(daff[1]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >2 && daff[2] != null)
    					{
    						formatedData.append(daff[2]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >4 && daff[4] != null)
    					{
    						formatedData.append(daff[4]); //2
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >8 && daff[8] != null)
    					{
    						formatedData.append(daff[8]); //3
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >8 && daff[8] != null)
    					{
    						formatedData.append(daff[8]); //4
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >5 && daff[5] != null)
    					{
    						formatedData.append(daff[5]); //5
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >6 && daff[6] != null)
    					{
    						formatedData.append(daff[6]); //6
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >7 && daff[7] != null)
    					{
    						formatedData.append(daff[7]); //7
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					if(daff.length >0 && daff[0] != null)
    					{
    						formatedData.append(daff[0]);
    					}	
    				}    		
    			}
    		}   		
    		formatedData.append(BdParser.AUDELIMITER);
    	}
    	
    	return formatedData.toString();

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