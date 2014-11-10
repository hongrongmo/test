package org.ei.query.limiter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ei.books.collections.ReferexCollection;

public class ReferexLimiter implements Limiter {

	public static final String SEPARATOR = ";";

    private List<ReferexCollection> colls = new ArrayList<ReferexCollection>();

	public ReferexLimiter(){};

	public ReferexLimiter(String[] values)
	{
		setLimitValues(values);
	}

	public ReferexLimiter(String values)
	{
		if(values != null)
		{
			setLimitValues(values.split(ReferexLimiter.SEPARATOR));
		}
	}
	private void setLimitValues(String[] values)
	{
		if(values != null)
		{
			for(int i = 0; i < values.length; i++)
			{
				String[] tnfBookArray = null;
				if(values[i].indexOf(";")>0)
				{
					tnfBookArray = values[i].split(ReferexLimiter.SEPARATOR);
				}
				else
				{
					tnfBookArray = new String[1];
					tnfBookArray[0] = values[i];
				}
				for(int k=0;k<tnfBookArray.length;k++)
				{
					ReferexCollection acoll = ReferexCollection.getCollection(tnfBookArray[k]);
					if(acoll != null)
					{
						colls.add(acoll);
					}
				}
			}
		}
	}

	public String getDisplayQueryFormat() {
	    if(colls.isEmpty())
	    {
	      return ReferexCollection.ALL;
	    }
	    String retval = "";
		Iterator itrCols = colls.iterator();
		while(itrCols.hasNext())
		{
			ReferexCollection rcol = (ReferexCollection) itrCols.next();
			String displayName = rcol.getDisplayName();
			if(retval.indexOf(displayName)<0)
			{
				retval = retval.concat(displayName);
				if(itrCols.hasNext())
				{
					retval = retval.concat(", ");
				}
			}
		}

		if(retval.lastIndexOf(',')==retval.length()-2)
		{
			retval = retval.substring(0,retval.length()-2);
		}

		return retval;
	}

	public String getPhysicalQueryFormat() {
	    if(colls.isEmpty())
	    {
	      return "";
	    }
		// TODO Auto-generated method stub
		String retval = " (";
		Iterator itrCols = colls.iterator();
		while(itrCols.hasNext())
		{
			ReferexCollection rcol = (ReferexCollection) itrCols.next();
			retval = retval.concat(rcol.getShortname()).concat(" WN CL");
			if(itrCols.hasNext())
			{
				retval = retval.concat(" OR ");
			}
		}
		retval = retval.concat(") ");
		return retval;
	}

	public String getDBStoreFormat() {
		String retval = "";
		Iterator itrCols = colls.iterator();
		while(itrCols.hasNext())
		{
			ReferexCollection rcol = (ReferexCollection) itrCols.next();
			retval = retval.concat(rcol.getAbbrev());
			if(itrCols.hasNext())
			{
				retval = retval.concat(ReferexLimiter.SEPARATOR);
			}
		}
		return retval;
	}

	public String toXML() {
		StringBuffer strBufQueryXML = new StringBuffer();

		strBufQueryXML.append("<COLLECTIONS>");

		Iterator itrCols = colls.iterator();
		while(itrCols.hasNext())
		{
			ReferexCollection rcol = (ReferexCollection) itrCols.next();
    		strBufQueryXML.append("<COLL>").append(rcol.getAbbrev()).append("</COLL>");
		}

    	strBufQueryXML.append("</COLLECTIONS>");

    	return strBufQueryXML.toString();
	}
    
    public List<ReferexCollection> getSelectedCollections() {
        return colls;
    }
}
