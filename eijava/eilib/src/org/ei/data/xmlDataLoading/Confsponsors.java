package org.ei.data.xmlDataLoading;
import java.util.*;

public class Confsponsors extends BaseElement
{

	Confsponsor confsponsor;
	String confsponsor_complete;
	List confsponsors = new ArrayList();

	public void setConfsponsors(List confsponsors)
	{
		this.confsponsors = confsponsors;
	}

	public void addConfsponsor(Confsponsor confsponsor)
	{
		confsponsors.add(confsponsor);
	}

	public List getConfsponsors()
	{
		return this.confsponsors;
	}

	public void setConfsponsors_complete(String confsponsor_complete)
	{
		this.confsponsor_complete = confsponsor_complete;
	}

	public String getConfsponsors_complete()
	{
		return this.confsponsor_complete;
	}

}