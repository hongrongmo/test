package org.ei.fulldoc;

import org.ei.util.MD5Digester;
import org.ei.util.StringUtil;


public class ScienceDirectGateway
{

	private StringUtil sUtil = new StringUtil();


	public String getScienceDirectLink(String scienceDirectUrl,
					   String origin,
					   String URLVersion,
					   String saltVersion,
					   String method,
					   String object,
					   String salt,
					   String PII)
		throws Exception
	{
		String fPII = fixPII(PII);
		String httpVariables = getHttpVariables(origin,
							URLVersion,
							saltVersion,
							method,
							object,
							fPII);

		MD5Digester digester = new MD5Digester();
		String theDigest = digester.asHex(digester.digest(httpVariables+salt));
		httpVariables = httpVariables+"&md5="+theDigest;
		return scienceDirectUrl+"?"+httpVariables;
	}

	public String getHttpVariables(String origin,
				       String URLVersion,
				       String saltVersion,
				       String method,
				       String object,
				       String fPII)
	{

		StringBuffer buffer = new StringBuffer();
		buffer.append("_ob=");
		buffer.append(object);
		buffer.append("&_origin=");
		buffer.append(origin);
		buffer.append("&_urlversion=");
		buffer.append(URLVersion);
		buffer.append("&_method=");
		buffer.append(method);
		buffer.append("&_piikey=");
		buffer.append(fPII);
		buffer.append("&_version=");
		buffer.append(saltVersion);
		return buffer.toString();
	}


	public String fixPII(String PII)
	{
		PII = sUtil.replace(PII,
				    ")", 
				    "",
				    StringUtil.REPLACE_GLOBAL,
				    StringUtil.MATCH_CASE_INSENSITIVE);

		PII = sUtil.replace(PII,
				    "(", 
				    "",
				    StringUtil.REPLACE_GLOBAL,
				    StringUtil.MATCH_CASE_INSENSITIVE);

		PII = sUtil.replace(PII,
				    "-", 
				    "",
				    StringUtil.REPLACE_GLOBAL,
			            StringUtil.MATCH_CASE_INSENSITIVE);

		return PII.toLowerCase();
	}

}
