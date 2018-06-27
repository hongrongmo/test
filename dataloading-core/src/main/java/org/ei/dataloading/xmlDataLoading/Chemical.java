package org.ei.dataloading.xmlDataLoading;

public class Chemical extends BaseElement
{
	String chemical_name;
	String cas_registry_number;

	public void setChemical_name(String chemical_name)
	{
		this.chemical_name = chemical_name;
	}

	public String getChemical_name()
	{
		return this.chemical_name;
	}

	public void setCas_registry_number(String cas_registry_number)
	{
		this.cas_registry_number = cas_registry_number;
	}

	public String getCas_registry_number()
	{
		return this.cas_registry_number;
	}
}

