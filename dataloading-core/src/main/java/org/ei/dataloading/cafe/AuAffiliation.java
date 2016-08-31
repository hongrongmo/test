package org.ei.dataloading.cafe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ei.common.Constants;

/**
 * 
 * @author TELEBH
 * @Date: 07/14/2016
 * @Description: Affiliation part which is Embedded in Cafe Author Profile source file. the class is part of ElasticSearch Extraction for Author Profile 
 */
public class AuAffiliation {
	
private String affiliation_id="";
private String affiliation_preferred_name="";
private String affiliation_display_name="";
private StringBuffer affiliation_display_city = new StringBuffer();
private String affiliation_display_country="";
private String affiliation_sortname="";
private StringBuffer affiliation_history_id = new StringBuffer();
private StringBuffer affiliation_history_display_name = new StringBuffer();
private StringBuffer affiliation_history_city = new StringBuffer();
private LinkedHashSet<String> affiliation_history_city_list = new LinkedHashSet<String>();
private StringBuffer affiliation_history_country = new StringBuffer();
private LinkedHashSet<String> affiliation_history_country_list = new LinkedHashSet<String>();
private StringBuffer parent_affiliation_id = new StringBuffer();
private StringBuffer parent_affiliation_prferred_name = new StringBuffer();
private StringBuffer parent_affiliation_name_variant = new StringBuffer();
private StringBuffer affiliation_city = new StringBuffer();
private StringBuffer affiliation_country = new StringBuffer();
private StringBuffer affiliation_name_id = new StringBuffer();
private LinkedHashSet<String> affiliation_name_id_list = new LinkedHashSet<String>();

// for display only so Dayton team can get Dept affiliation displayname without going to db
private String current_dept_affiliation_id="";
private String current_dept_affiliation_display_name="";
private String current_dept_affiliation_city="";
private String current_dept_affiliation_country="";


public AuAffiliation(){}

public void setAffiliationId(String affid)
{
	this.affiliation_id = affid;
	parent_affiliation_id.append(affid);
}

public String getAffiliationId()
{
	return this.affiliation_id;
}


//-------------------
public void setAffiliationPreferredName(String preferredName)
{
	affiliation_preferred_name = preferredName;
	parent_affiliation_prferred_name.append(preferredName);	
}
public String getAffiliationPreferredName()
{
	return this.affiliation_preferred_name;
}

//-----------------

public void setAffiliationCity()
{
	if(affiliation_city.length() >0)
	{
		affiliation_city.append(Constants.IDDELIMITER);
	}	
	affiliation_city.append(this.affiliation_history_city);
}
public String getAffiliationCity()
{
	return affiliation_city.toString();
}
//--------------

public void setAffiliationCountry()
{
	if(affiliation_country.length() >0)
	{
		affiliation_country.append(Constants.IDDELIMITER);
	}
	affiliation_country.append(this.affiliation_history_country);
}
public String getAffiliationCountry()
{
	return affiliation_country.toString();
}

//-------------------
public void setAffiliationDisplayName(String display_name)
{
	this.affiliation_display_name = display_name;
}
public String getAffiliationDisplayName()
{
	return this.affiliation_display_name;
}
//---------------------


public void setAffiliationDisplayCity(String city)
{
	affiliation_display_city.append(city);
	affiliation_city.append(city);
}
public String getAffiliationDisplayCity()
{
	return this.affiliation_display_city.toString();
}

//---------------------

public void setAffiliationDisplayCountry(String country)
{
	affiliation_display_country = country;
	affiliation_country.append(country);
}
public String getAffiliationDisplayCountry()
{
	return this.affiliation_display_country;
}

//---------------
public void setAffiliationSortName(String sortName)
{
	affiliation_sortname = sortName;
}
public String getAffiliationSortName()
{
	return this.affiliation_sortname;
}
//-----------------


public void setAffiliationNameId(String nameId)
{
	if(!(affiliation_name_id_list.contains(nameId.toLowerCase())))
	{
		if(affiliation_name_id.length() >0)
		{
			affiliation_name_id.append(Constants.IDDELIMITER);
		}
		affiliation_name_id_list.add(nameId.toLowerCase());
		affiliation_name_id.append(nameId);
	}

}
public String getAffiliationNameId()
{
	return this.affiliation_name_id.toString();
}

//-------------
public void setHistoryAffid(String histaffid)
{
	if(affiliation_history_id.length()>0)
	{
		affiliation_history_id.append(Constants.IDDELIMITER);
	}
	affiliation_history_id.append(histaffid);
}
public String getHistoryAffid()
{
	return this.affiliation_history_id.toString();
}

//--------------
public void setHistoryDisplayName(String history_displayName)
{
	if(affiliation_history_display_name.length() >0)
	{
		affiliation_history_display_name.append(Constants.IDDELIMITER);
	}
	affiliation_history_display_name.append(history_displayName);
}
public String getHistoryDisplayName()
{
	return this.affiliation_history_display_name.toString();
}
//------------------

public void setHistoryCity(String history_city)
{
	if (!(affiliation_history_city_list.contains(history_city)))
	{
		if(affiliation_history_city.length() >0)
		{
			affiliation_history_city.append(Constants.IDDELIMITER);
		}
		affiliation_history_city_list.add(history_city);
		affiliation_history_city.append(history_city);
	}
	
}
public String getHistoryCity()
{
	return this.affiliation_history_city.toString();
}

//--------------------
public void setHistoryCountry(String history_country)
{
	if(!(affiliation_history_country_list.contains(history_country)))
	{
		if(affiliation_history_country.length() >0)
		{
			affiliation_history_country.append(Constants.IDDELIMITER);
		}
		affiliation_history_country_list.add(history_country);
		affiliation_history_country.append(history_country);
	}
	
}
public String getHistoryCountry()
{
	return this.affiliation_history_country.toString();
}

//-------------
public void setParentAffiliationsId()
{
	if(affiliation_history_id.length() >0)
	{
		parent_affiliation_id.append(Constants.IDDELIMITER);
	}
	parent_affiliation_id.append(affiliation_history_id);
}
public String getParentAffiliationsId()
{
	return this.parent_affiliation_id.toString();
}

//-----------------

public void setParentAffiliationsPreferredName(String parent_preferredName)
{
	if(parent_affiliation_prferred_name.length() >0)
	{
		parent_affiliation_prferred_name.append(Constants.IDDELIMITER);
	}
	parent_affiliation_prferred_name.append(parent_preferredName);
}
public String getParentAffiliationsPreferredName()
{
	return this.parent_affiliation_prferred_name.toString();
}

//-----------
public void setParentAffiliationsNameVariant(String parent_nameVariant)
{
	if(parent_affiliation_name_variant.length()>0)
	{
		parent_affiliation_name_variant.append(Constants.IDDELIMITER);
	}
	parent_affiliation_name_variant.append(parent_nameVariant);
}

public String getParentAffiliationsNameVariant()
{
	return this.parent_affiliation_name_variant.toString();
}

//-------------

// Current Dept Affiliation Info

public void setCurrentDeptAffiliation_Id(String currDeptAff_Id)
{
	current_dept_affiliation_id = currDeptAff_Id;
}
public String getCurrentDeptAffiliation_Id()
{
	return this.current_dept_affiliation_id;
}


public void setCurrentDeptAffiliation_DisplayName(String currDeptAffDisplay_Name)
{
	current_dept_affiliation_display_name = currDeptAffDisplay_Name;
}
public String getCurrentDeptAffiliation_DisplayName()
{
	return this.current_dept_affiliation_display_name;
}

//---------------
public void setCurrentDeptAffiliation_City(String currDeptAff_city)
{
	current_dept_affiliation_city = currDeptAff_city;
}
public String getCurrentDeptAffiliation_City()
{
	return this.current_dept_affiliation_city;
}
//-----------
public void setCurrentDeptAffiliation_Country(String currDeptAff_country)
{
	current_dept_affiliation_country = currDeptAff_country;
}
public String getCurrentDeptAffiliation_Country()
{
	return this.current_dept_affiliation_country;
}

}
