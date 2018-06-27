package org.ei.dataloading.xmlDataLoading;

public class Item extends BaseElement
{
	Process_info  process_info;
	Bibrecord bibrecord;


	public void setProcess_info(Process_info process_info)
	{
		this.process_info = process_info;
	}

	public Process_info getProcess_info()
	{
		return this.process_info;
	}

	public void setBibrecord(Bibrecord bibrecord)
	{
		this.bibrecord = bibrecord;
	}

	public Bibrecord getBibrecord()
	{
		return this.bibrecord;
	}


}
