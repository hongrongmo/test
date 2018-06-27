package org.ei.dataloading.xmlDataLoading;

public class Patent extends BaseElement
{

	Registrations registrations;
	Localapplications localapplications;
	Prioapplications prioapplications;
	Assignees assignees;
	Designatedcountries designatedcountries;
	Ipc_codes ipc_codes;
	Inventors inventors;

	public void setRegistrations(Registrations registrations)
	{
		this.registrations = registrations;
	}

	public Registrations getRegistrations()
	{
		return this.registrations;
	}

	public void setLocalapplications(Localapplications localapplications)
	{
		this.localapplications = localapplications;
	}

	public Localapplications getLocalapplications()
	{
		return this.localapplications;
	}

	public void setPrioapplications(Prioapplications prioapplications)
	{
		this.prioapplications = prioapplications;
	}

	public Prioapplications getPrioapplications()
	{
		return this.prioapplications;
	}

	public void setAssignees(Assignees assignees)
	{
		this.assignees = assignees;
	}

	public Assignees getAssignees()
	{
		return this.assignees;
	}

	public void setDesignatedcountries(Designatedcountries designatedcountries)
	{
		this.designatedcountries = designatedcountries;
	}

	public Designatedcountries getDesignatedcountries()
	{
		return this.designatedcountries;
	}

	public void setInventors(Inventors inventors)
	{
		this.inventors = inventors;
	}

	public Inventors getInventors()
	{
		return this.inventors;
	}

	public void setIpc_codes(Ipc_codes ipc_codes)
	{
		this.ipc_codes = ipc_codes;
	}

	public Ipc_codes getIpc_codes()
	{
		return this.ipc_codes;
	}

}
