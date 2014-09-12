package org.ei.biz.personalization;


import org.ei.exception.EVBaseException;

public class PersonalAccountException
	extends EVBaseException
{
	public PersonalAccountException(Exception e)
	{
		super(e);
	}
	
	public PersonalAccountException(String message, Exception e)
	{
		super(message, e);
	}

}
