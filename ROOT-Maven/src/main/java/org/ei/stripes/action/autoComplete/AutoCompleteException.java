package org.ei.stripes.action.autoComplete;
import org.ei.exception.EVBaseException;


public class AutoCompleteException 	extends EVBaseException
{

	private static final long serialVersionUID = -2684354402090012187L;

	public AutoCompleteException(Exception e)
	{
		super(e);
	}
	
	public AutoCompleteException(String message, Exception e)
	{
		super(message, e);
	}
}

