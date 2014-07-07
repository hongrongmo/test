package org.ei.data.xmlDataLoading;
import java.util.*;


public class Chemicalgroup extends BaseElement
{
	List chemicals = new ArrayList();

	public void setChemicalss(List chemicals)
	{
		this.chemicals = chemicals;
	}

	public void addChemicals(Chemicals chemical)
	{
		chemicals.add(chemical);
	}

	public List getChemicalss()
	{
		return this.chemicals;
	}


}