package org.ei.dataloading.xmlDataLoading;

public class Correspondence extends BaseElement
{
	Person person;
	Affiliation affiliation;
	String e_address;
	String correspondence;

	public void setCorrespondence(String correspondence)
	{
		this.correspondence = correspondence;
	}

	public String getCorrespondence()
	{
		return this.correspondence;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public Person getPerson()
	{
		return this.person;
	}

	public void setAffiliation(Affiliation affiliation)
	{
		this.affiliation = affiliation;
	}

	public Affiliation getAffiliation()
	{
		return this.affiliation;
	}

	public void setE_address(String e_address)
	{
		this.e_address = e_address;
	}

	public String getE_address()
	{
		return this.e_address;
	}
}
