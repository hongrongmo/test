package org.ei.session;

import java.util.StringTokenizer;

import org.ei.util.GUID;

public class SessionID
{

	private String ID;
	private int versionNumber;

	public SessionID()
		throws SessionException
	{
		try
		{
			this.ID = new GUID().toString();
			this.versionNumber = 1;
		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
	}

	public SessionID(String sessionID)
	{
		StringTokenizer tok = new StringTokenizer(sessionID, "_");
		this.versionNumber = Integer.parseInt(tok.nextToken());
		this.ID = tok.nextToken();
	}

	public SessionID(String ID,
					 int versionNumber)
	{
		this.ID = ID;
		this.versionNumber = versionNumber;
	}


	public String toString()
	{
		return Integer.toString(versionNumber)+"_"+ID;
	}

	public int incrementVersion()
	{
		this.versionNumber++;
		return this.versionNumber;
	}

	public int getVersionNumber()
	{
		return this.versionNumber;
	}

	public String getID()
	{
		return this.ID;
	}







}