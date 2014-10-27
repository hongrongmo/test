package org.ei.util;


public class StringUtilException extends Exception
{

	private Exception e;

	public StringUtilException(Exception e)
	{
		this.e = e;
	}

	public void printStackTrace()
	{
		e.printStackTrace();
	}

	public String getMessage()
	{
		return this.e.getMessage();
	}




}
