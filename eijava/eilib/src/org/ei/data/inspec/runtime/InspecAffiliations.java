
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
  //(0 aff?,1  orgn?, 2 dept?, 3 addline*,4  city?, 5 state?,6  pcode?, 7 cntry?,8  orgid?)  -- raw data
  //in db table - aff, rid , country, orgn, depart, addLine, city, state, pccode, orig

public class InspecAffiliations
{
    private BdAffiliations affs ;
    private static ArrayList  affElements = new ArrayList();
    private static ArrayList  affElementsExpanded = new ArrayList();
    
    public InspecAffiliations(String insAffiliations)
    {
    	this.affs = new BdAffiliations(formatAffStr(insAffiliations));
    }
       
    public String formatAffStrNewXMLformatOptFirst(String[] daff)
    {
    	StringBuffer formatedData = new StringBuffer();

		if(daff.length >1 && daff[1] != null)
		{
			daff[1] = daff[1].substring(daff[1].lastIndexOf(".")+1) ;
			formatedData.append(daff[1]);
		}    
		formatedData.append(BdParser.IDDELIMITER);
    	if(daff.length > 0 && daff[0] != null)
    	{
    		formatedData.append(daff[0]);    		
    		
    	}
    	formatedData.append(BdParser.IDDELIMITER);
    	//city group
    	formatedData.append(BdParser.IDDELIMITER);
    	//country
    	if(daff.length > 2 && daff[2] != null)
    	{
    		formatedData.append(daff[2]);
    	}

    	System.out.println("aff is in data "+formatedData.toString());
    	return formatedData.toString();
    }
    
    public String formatAffStrNewXMLformatOptSecond(String[] daff)
    {

    	StringBuffer formatedData = new StringBuffer();
    	
		if(daff.length >1 && daff[1] != null)
		{
			daff[1] = daff[1].substring(daff[1].lastIndexOf(".")+1) ;
			formatedData.append(daff[1]);
		}    
		formatedData.append(BdParser.IDDELIMITER);
		//organization - aff isn't there - use department field
		if(daff.length > 4 && daff[4] != null)
		{
			formatedData.append(daff[4]);
		}
		formatedData.append(BdParser.IDDELIMITER);
		//city group - origin
		if(daff.length > 3 && daff[3] != null)
		{
			formatedData.append(daff[3]);
		}		
		formatedData.append(BdParser.IDDELIMITER);
		//country - country field
		
		if(daff.length > 2 && daff[2] != null)
		{
			formatedData.append(daff[2]);
		}
		
		formatedData.append(BdParser.IDDELIMITER);
		// address line - address part
		if(daff.length > 5 && daff[5] != null)
		{
			formatedData.append(daff[5]);
		}
		
		formatedData.append(BdParser.IDDELIMITER);
		// city
		if(daff.length >6  && daff[6] != null)
		{ 
			formatedData.append(daff[6]);
			
		}
		formatedData.append(BdParser.IDDELIMITER);
		//state
		if(daff.length >7  && daff[7] != null)
		{ 
			formatedData.append(daff[7]);
			
		}
		formatedData.append(BdParser.IDDELIMITER);
		//postal code
		if(daff.length >8  && daff[8] != null)
		{ 
			formatedData.append(daff[8]);				
		}
		
		formatedData.append(BdParser.IDDELIMITER);
		//text
		formatedData.append(BdParser.IDDELIMITER);
		

		System.out.println("no aff in data"+formatedData.toString());
    	return formatedData.toString();
    }
    
  // method to use when only three fields aval
    
   public String formatAffStrThreeFields(String[] daff)
   {
	   System.out.println(" daff ");
	   	StringBuffer formatedData = new StringBuffer();
    	
		if(daff.length >1 && daff[1] != null)
		{
			daff[1] = daff[1].substring(daff[1].lastIndexOf(".")+1) ;
			formatedData.append(daff[1]);
		}    				
		formatedData.append(BdParser.IDDELIMITER);
		//1
		if(daff.length >0 && daff[0] != null)
		{
			formatedData.append(daff[0]);
		}
		formatedData.append(BdParser.IDDELIMITER);
		//3
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
		formatedData.append(BdParser.IDDELIMITER);
		
		
		System.out.println(" curr data "+formatedData.toString());
				
		return formatedData.toString();
		
    }
   

    public String formatAffStr(String dAffs)
    {
    	boolean isExpanded = false;
    	StringBuffer formatedData = new StringBuffer();
    	
    	String[] daffs = dAffs.split(BdParser.AUDELIMITER, -1);   	

    	for(int i = 0; i < daffs.length; i++)
    	{
    		if(daffs[i] != null && daffs[i].length() > 0)
    		{ 			    	    	
    			String []daff = daffs[i].split(BdParser.IDDELIMITER, -1);
    			
    	    	if(daff.length > 3)
    	    	{
    	    		isExpanded = true;
    	    	}

    			if(daff != null && daff.length > 0)
    			{
    				if(!isExpanded)
    				{    					
    					formatedData.append(formatAffStrThreeFields(daff));   				  				
    				}
    				else
    				{
    					// if aff is field is present - use this method
    					if(daff[0] != null && !daff[0].trim().equals(""))
    					{
    						formatedData.append(formatAffStrNewXMLformatOptFirst(daff));
    					}
    					//else if(daff[2] != null && !daff[2].trim().equals(""))
    					else
    					{
    						formatedData.append(formatAffStrNewXMLformatOptSecond(daff));   						
    					}
    				}
    					    		 
    				formatedData.append(BdParser.AUDELIMITER);
    			}
    		}   		   		
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
				   System.out.println("COUNTRY"+aff.getAffCountry());
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