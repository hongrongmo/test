package org.ei.connectionpool;

public class NoConnectionAvailableException extends Exception
{
	public NoConnectionAvailableException()
	{
		super();
	}

	public NoConnectionAvailableException(String msg)
	{
		super(msg);
	}

}
