package org.ei.exception;


public class SecurityException extends EVBaseException
{
	public SecurityException(int code, String message, Exception e)
	{
		super(code, message, e);
	}
}