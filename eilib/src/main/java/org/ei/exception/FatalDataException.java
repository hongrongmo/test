package org.ei.exception;



public class FatalDataException extends Exception
{

	private static final long serialVersionUID = -3425701976139492630L;
	private Exception e;
	
	public  FatalDataException(Exception e)
	{
		super(e);
	}

	public  FatalDataException(String message, Exception e)
	{
		super(message, e);
	}
	
	public  FatalDataException(String message)
	{
		super(message);
	}
	
}
