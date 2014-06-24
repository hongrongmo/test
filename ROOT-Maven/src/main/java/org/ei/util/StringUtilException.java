package org.ei.util;


public class StringUtilException extends Exception
{

	private static final long serialVersionUID = 6499115312195815982L;
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
