package org.ei.data.encompasslit.runtime;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.data.compendex.runtime.CPXDataDictionary;
import org.ei.domain.*;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import java.util.*;
import java.util.Hashtable;
import org.ei.fulldoc.LinkingStrategy;

public class EltDatabase extends Database
{

	private LinkingStrategy EltLinkingStrategy = new CPXLinkingStrategy();
	private DataDictionary dataDictionary = new EltDataDictionary();

	private static Map searchfield = new HashMap();

	  static {
		  searchfield.put("ALL", "Y");
		  searchfield.put("KY", "Y");
		  searchfield.put("AB", "Y");
		  searchfield.put("AU", "Y");
		  searchfield.put("AF", "Y");
		  searchfield.put("TI", "Y");
		  searchfield.put("ST", "Y");
		  searchfield.put("PN", "Y");
		  searchfield.put("CV", "Y");
		  searchfield.put("CR", "Y");
		  searchfield.put("CF", "Y");
	  }

	public int getStartYear(boolean hasBackFile) {
	   return 1900;
   }

   protected String getBaseTableHook() {
	   return "elt_master";
   }

   public DocumentBuilder newBuilderInstance() {
	   return new EltDocBuilder(this);
   }

   public SearchControl newSearchControlInstance() {
	   return new FastSearchControl();
   }

   public String getID() {
	   return "elt";
   }

   public String getName() {
	   return "EnCompassLIT";
   }

   public String getIndexName() {
	   return "elt";
   }

   public String getShortName() {
	   return "EnCompassLIT";
   }

   public int getMask() {
	   return 1024;
   }

   public boolean hasChildren() {
	   return false;
   }

   public boolean linkLocalHoldings(String linklabel) {
	   return false;
   }

   public int getSortValue() {
	   return getMask();
   }

   public LinkingStrategy getLinkingStrategy() {
	   return EltLinkingStrategy;
   }


	public boolean hasField(SearchField searchField,
			int mask)
	{
	    if(searchfield.containsKey(searchField.getID()))
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}

    public boolean hasField(SearchField searchField)
    {
	    if(searchfield.containsKey(searchField.getID()))
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
    }

    public DataDictionary getDataDictionary()
    {
        return dataDictionary;
    }

    public String getSingleCharName()
    {
        return "L";
    }

    public String getDisplayAbrevName()
    {
        return "L";
    }

}