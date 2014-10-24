package org.ei.util.security;

import org.ei.util.GUID;
import org.ei.util.MD5Digester;

public class SecureID
{
	private static final String SEED = "fkjhsd9tr87whfs3297fshjk";

	private static final String citedBySaltKey = "Kyvs.FpdJvCAXVa:9TK13xB!a01ZV(iW";

	public static String getCitedbyMD5(String sessionID,String accessNumber)
	{
		MD5Digester digester = new MD5Digester();
		String theDigest=null;
		try
		{
			if(sessionID.indexOf("_")>-1)
			{
				sessionID = sessionID.substring(sessionID.indexOf("_")+1);
			}
			theDigest = digester.asHex(digester.digest(sessionID+accessNumber+citedBySaltKey));
			//System.out.println("ACCESSNUMBER= "+accessNumber+" Session-ID="+sessionID+" MD5="+theDigest);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return theDigest;
	}

	public static String getSecureID(long ttl)
		throws Exception
	{
		MD5Digester digester = new MD5Digester();
		GUID guid = new GUID();
		String guidString = guid.toString();
		String createTime = null;
		String timeToLive = null;
		StringBuffer hashBuffer = new StringBuffer(guidString);
		hashBuffer.append(SEED);

		if(ttl > 0)
		{
			createTime = Long.toString(System.currentTimeMillis());
			timeToLive = Long.toString(ttl);
			hashBuffer.append(createTime).append(timeToLive);
		}

		String hashed = digester.asHex(digester.digest(hashBuffer.toString()));
		StringBuffer buffer = new StringBuffer(guidString);
		buffer.append("-").append(hashed);

		if(ttl > 0)
		{
			buffer.append("-").append(createTime).append("-").append(ttl);
		}
		return buffer.toString();
	}

	public static boolean validSecureID(String secureID)
		throws Exception
	{
		String[] parts = secureID.split("-");
		String guid = parts[0];
		String hash = parts[1];
		String timestamp = null;
		long ttl = -1L;
		if(parts.length == 4)
		{
			timestamp = parts[2];
			ttl = Long.parseLong(parts[3]);
		}
		StringBuffer hashBuffer = new StringBuffer(guid);
		hashBuffer.append(SEED);

		if(parts.length == 4)
		{
			long timediff = System.currentTimeMillis() - Long.parseLong(timestamp);
			if(ttl < timediff)
			{
				return false;
			}
			hashBuffer.append(timestamp).append(ttl);
		}

		MD5Digester digester = new MD5Digester();
		String hashTest = digester.asHex(digester.digest(hashBuffer.toString()));
		return hashTest.equals(hash);
	}
}