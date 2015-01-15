package org.ei.fulldoc;

import org.ei.util.MD5Digester;

public class OHUBRequest
{

	private String salt;
	private String saltVersion;
	private OHUBID[] docs;
	private String identificationID;
	
	
	private String digest;
	private String requestString;
	
	
	
	
	public OHUBRequest(String salt,
			   String saltVersion,
			   String identificationID,
			   OHUBID[] docs)
		throws Exception
	{
		this.salt = salt;
		this.saltVersion = saltVersion;
		this.docs = docs;
		this.identificationID = identificationID;
		
		StringBuffer dataBuffer = new StringBuffer();
		for(int x=0; x< docs.length; ++x)
		{
			OHUBID id = docs[x];
			dataBuffer.append(Integer.toString(x+1)+id.getSaltString());
		}
		
		dataBuffer.append(this.salt);
		
		MD5Digester digester = new MD5Digester();
		this.digest = digester.asHex(digester.digest(dataBuffer.toString()));

		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<!DOCTYPE ohub-request PUBLIC \"ohubRequest10\" \"http://localhost/ohubRequest10.dtd\">");
		xml.append("<ohub-request>");
		xml.append("<header><transaction id=\""+ Integer.toString(1) + "\"/>");
		xml.append("<identification id=\""+ identificationID + "\" sv=\"" + saltVersion + "\" digest=\"" + digest + "\"/>");
		xml.append("</header>");
		xml.append("<requests>");
		for(int x=0; x<docs.length; ++x)
		{
			xml.append("<request id=\"" + Integer.toString(x+1) + "\">");
			xml.append(docs[x].getXMLString());
			xml.append("</request>");
		}
		
		xml.append("</requests>");
		xml.append("</ohub-request>");
		this.requestString = xml.toString();	
	}
	
	public String getRequestString()
		throws Exception
	{
		
		return this.requestString;
	}
	
	public String getDigest()
	{
		return this.digest;	
	}
	
	
}
