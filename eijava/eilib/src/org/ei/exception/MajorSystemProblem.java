package org.ei.exception;


public class MajorSystemProblem extends Exception
{
	private Exception e;

	public MajorSystemProblem(Exception e)
	{
		this.e = e;
	}

	public String getMessage()
	{
		return e.getMessage();
	}

	public void printStackTrace()
	{
		this.e.printStackTrace();
	}


}
