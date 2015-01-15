package org.ei.util;


public class SpinLockException
	extends Exception
{
	private Exception e;

	public SpinLockException(Exception e)
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
