package org.ei.data.pag.runtime;

import org.ei.domain.DataDictionary;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class PAGDataDictionary  implements DataDictionary
{
    Hashtable classCodes = null;

    public PAGDataDictionary()
    {
        classCodes = new Hashtable();

        classCodes.put("ELE","Electronics & Electrical");
        classCodes.put("MAT","Materials & Mechanical");
        classCodes.put("CHE","Chemical, Petrochemical & Process");
        classCodes.put("CIV","Civil & Environmental");
        classCodes.put("COM","Computing");
        classCodes.put("SEC","Security & Networking");
    }

	  public String getClassCodeTitle(String arg0)
	  {
		  return (String) classCodes.get(arg0);
	  }

  	public Hashtable getClassCodes()
  	{
  		return classCodes;
  	}

  	public Hashtable getTreatments()
  	{
  		return null;
  	}

    public Hashtable getAuthorityCodes()
    {
    	return null;
    }
}