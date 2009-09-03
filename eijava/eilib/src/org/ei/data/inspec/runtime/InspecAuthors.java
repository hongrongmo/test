/*
 * Created on Jun 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.inspec.runtime;
import java.util.*;

import org.ei.data.bd.BdAuthors;
import org.ei.data.bd.loadtime.*;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InspecAuthors
{
    private BdAuthors au ;
    private static ArrayList  auElements = new ArrayList();
    
    private static ArrayList  auElementsExpanded = new ArrayList();
    private boolean isExpanded = false;

    static
    {      
        auElements.add("indexedName");
        auElements.add("affidStr");
    }
    
    static
    {      
        auElementsExpanded.add("indexedName");
        auElementsExpanded.add("affidStr");
        auElementsExpanded.add("foreNames"); 
        auElementsExpanded.add("initials");
        auElementsExpanded.add("suffix");
        auElementsExpanded.add("eAddress"); 
        auElementsExpanded.add("id");
    }

    public InspecAuthors(String dAus)
    { 
    	au = new BdAuthors(formatAuStr(dAus));  	
    }
    
    
    public String formatAuStr(String dAus)
    {   	
    	StringBuffer formatedData = new StringBuffer();
    	
    	String[] daus = dAus.split(BdParser.AUDELIMITER);   	
    	for(int i = 0; i < daus.length; i++)
    	{
    		if(daus[i] != null && daus[i].length() > 0)
    		{
    			String []dau = daus[i].split(BdParser.IDDELIMITER,-1);
    			
    		  	if(dau.length > 1)
    	    	{
    	    		isExpanded = true;
    	    	}
    		  	
    			if(daus != null && daus.length > 0)
    			{
    				if(!isExpanded)
    				{   //0
    					formatedData.append(BdParser.IDDELIMITER);
    					//1
    					formatedData.append(BdParser.IDDELIMITER);
    					//2
    					if(dau.length >1 && dau[1] != null)
    					{
    						formatedData.append(dau[1].substring(dau[1].lastIndexOf(".")+1));
    					}    				
    					formatedData.append(BdParser.IDDELIMITER);
    					if(dau.length >0 && dau[0] != null)
    					{
    						formatedData.append(dau[0]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);
    					formatedData.append(BdParser.IDDELIMITER);   		
    				}
    				else
    				{  
    					//0
    					formatedData.append(BdParser.IDDELIMITER);
    					//1
     					formatedData.append(BdParser.IDDELIMITER);
    					//2
     					if(dau.length >1 && dau[1] != null)
    					{
     						formatedData.append(dau[1].substring(dau[1].lastIndexOf(".")+1));
    					}    				
    					formatedData.append(BdParser.IDDELIMITER);
    					//3
    					if(dau.length >0 && dau[0] != null)
    					{
    						formatedData.append(dau[0]);
    					}

    					formatedData.append(BdParser.IDDELIMITER);
    					//4
    					if(dau.length >3 && dau[3] != null)
    					{
    						formatedData.append(dau[3]); //3
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					//5

    					formatedData.append(BdParser.IDDELIMITER);
    					//6
    					formatedData.append(BdParser.IDDELIMITER);
    					//7
    					formatedData.append(BdParser.IDDELIMITER);
    					//8
    					if(dau.length >4 && dau[4] != null)
    					{
    						formatedData.append(dau[4]);
    					}
    					formatedData.append(BdParser.IDDELIMITER);
    					//9
    					formatedData.append(BdParser.IDDELIMITER);
    					//10
    					formatedData.append(BdParser.IDDELIMITER);
    					//11
    					formatedData.append(BdParser.IDDELIMITER);
    					//12
    					formatedData.append(BdParser.IDDELIMITER);
    					//13
    					formatedData.append(BdParser.IDDELIMITER);
    					if(dau.length >5 && dau[5] != null)
    					{
    						formatedData.append(dau[5]);
    					}
    				}    		
    			}
    		}   		
    		formatedData.append(BdParser.AUDELIMITER);
    	}
    	
    	return formatedData.toString();

    }
    
    
    
    
    public String[] getSearchValue()
    { 
    	return au.getSearchValue();
    }
    public List getInspecAuthors()
    { 
    	return au.getAuthors();
    }

}
