package org.ei.exception;

import java.io.PrintWriter;


public class BasicException extends Exception
{
	private Exception e;
	
	public BasicException(Exception e)
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

	public void printStackTrace(PrintWriter writer)
	{
		e.printStackTrace(writer);
	}

	public String toString()
	{
		return e.toString();
	}
}
