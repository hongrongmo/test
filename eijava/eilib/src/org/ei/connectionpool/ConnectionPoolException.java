package org.ei.connectionpool;

public class ConnectionPoolException extends Exception
{
	private Exception e;

	public ConnectionPoolException(Exception e)
	{
		this.e = e;
	}

	public void printStackTrace()
	{
		e.printStackTrace();
	}

	public String getMessage()
	{
		return e.getMessage();
	}

}
