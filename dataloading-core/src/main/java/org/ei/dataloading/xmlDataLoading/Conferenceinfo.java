package org.ei.dataloading.xmlDataLoading;

public class Conferenceinfo extends BaseElement
{
	Confevent	confevent;
	Confpublication	confpublication;

	public void setConfevent(Confevent confevent)
	{
		this.confevent = confevent;
	}

	public Confevent getConfevent()
	{
		return this.confevent;
	}

	public void setConfpublication(Confpublication confpublication)
	{
		this.confpublication = confpublication;
	}

	public Confpublication getConfpublication()
	{
		return this.confpublication;
	}
}
