package org.ei.domain;

import java.io.PrintWriter;


public class DocumentBuilderException extends Exception
{
	private Exception e;

	public DocumentBuilderException(Exception e)
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



}
