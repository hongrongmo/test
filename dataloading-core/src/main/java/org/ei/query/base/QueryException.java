
package org.ei.query.base;


public class QueryException
	extends Exception
{
	private Exception e;

	public QueryException(Exception e)
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
