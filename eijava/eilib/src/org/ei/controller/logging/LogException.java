package org.ei.controller.logging;

public class LogException
	extends Exception
{

	private Exception e;

	public LogException(Exception e)
	{
		this.e = e;
	}

	public String getMessage()
	{
		return e.getMessage();
	}

	public void printStackTrace()
	{
		e.printStackTrace();
	}
	

}
