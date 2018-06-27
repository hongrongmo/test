package org.ei.dataloading.xmlDataLoading;

public class Status extends BaseElement
{

	String status;
	String status_type;
	String status_state;
	String status_stage;

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus_type(String status_type)
	{
		this.status_type = status_type;
	}

	public String getStatus_type()
	{
		return this.status_type;
	}

	public void setStatus_state(String status_state)
	{
		this.status_state = status_state;
	}

	public String getStatus_state()
	{
		return this.status_state;
	}

	public void setStatus_stage(String status_stage)
	{
		this.status_stage = status_stage;
	}

	public String getStatus_stage()
	{
		return this.status_stage;
	}


}
