package org.ei.session;

public class EntryToken
{
	public static final int STATUS_ON = 1;
	public static final int STATUS_OFF = 0;

	private String tokenID;
	private long createTime;
	private long sessionLength;
	private long startTime = -1;
	private long expireTime;
	private String credentials;
	private int campaign;
	private String accountID;
	private int status;

	public long getExpireTime()
	{
		return this.expireTime;
	}

	public void setExpireTime(long expireTime)
	{
		this.expireTime = expireTime;
	}

	public String getTokenID()
	{
		return this.tokenID;
	}

	public void setTokenID(String tokenID)
	{
		this.tokenID = tokenID;
	}

	public String getAccountID()
	{
		return this.accountID;
	}

	public void setAccountID(String accountID)
	{
		this.accountID = accountID;
	}

	public long getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(long createTime)
	{
		this.createTime = createTime;
	}

	public long getSessionLength()
	{
		return this.sessionLength;
	}

	public void setSessionLength(long sessionLength)
	{
		this.sessionLength = sessionLength;
	}

	public long getStartTime()
	{
		return this.startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public String getCredentials()
	{
		return this.credentials;
	}

	public void setCredentials(String credentials)
	{
		this.credentials = credentials;
	}

	public int getCampaign()
	{
		return this.campaign;
	}

	public void setCampaign(int campaign)
	{
		this.campaign = campaign;
	}

	public int getStatus()
	{
		return this.status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public boolean allowEntry()
	{
		long currentTime = System.currentTimeMillis();
		if(this.status == STATUS_OFF ||
		   currentTime > expireTime  ||
		   (startTime > -1 && ((currentTime - startTime) > sessionLength)))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}