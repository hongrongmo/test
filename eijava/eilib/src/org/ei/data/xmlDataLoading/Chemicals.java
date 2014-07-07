package org.ei.data.xmlDataLoading;
import java.util.*;


public class Chemicals extends BaseElement
{
	List chemicals = new ArrayList();
	String chemical_source;

	public void setChemicals(List chemicals)
	{
		this.chemicals = chemicals;
	}

	public void addChemical(Chemical chemical)
	{
		chemicals.add(chemical);
	}

	public List getChemicals()
	{
		return this.chemicals;
	}

	public void setChemical_source(String chemical_source)
	{
		this.chemical_source = chemical_source;
	}

	public String getChemical_source()
	{
		return this.chemical_source;
	}

}

