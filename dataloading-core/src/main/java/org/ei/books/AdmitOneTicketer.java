package org.ei.books;

import java.security.MessageDigest;


public class AdmitOneTicketer
{
	
	private String secret = "35738437";
	private static final String SHRDKEY = "!MM01234-5-6789MM#";	
	private static AdmitOneTicketer instance;
	
	public static synchronized AdmitOneTicketer getInstance()
	{
		if(instance == null) {
      instance = new AdmitOneTicketer();
		}

		return instance;
	}

	private AdmitOneTicketer()
	{

	}

	public String getPageTicket(String isbn,
								String custid,
								long currentTime)
		throws BookException
  {
		// getTicket();
		StringBuffer ticketVal = new StringBuffer();
		ticketVal.append(isbn).append(custid).append(currentTime);
		ticketVal.append(AdmitOneTicketer.SHRDKEY);
		String ticket = "";

		try {
			ticket = getTicket(ticketVal.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ticket = ticket.concat(":").concat(custid).concat(":").concat(String.valueOf(currentTime));

		return ticket;
	}

  // PII is the chapter unique identifier
	public String getChapterTicketedURL(String baseUrl, String isbn, String pii,
										String custID,
										long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		buf.append(isbn);
		buf.append("/");
		buf.append(pii);
		buf.append(".pdf");
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}

	public String getBookTicketedURL(String baseUrl, String isbn,
									 String custID,
									 long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		buf.append(isbn);
		buf.append("/");
		buf.append(isbn);
		buf.append(".pdf");
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}



	private String getTicketedURL(String fullUrl,
								  String custID,
								  long currentTime)
	{
		StringBuffer buf = new StringBuffer();
		try
		{			
			String root = getRoot(fullUrl);
			String path = getPath(fullUrl);					
			StringBuffer ticketBuffer = new StringBuffer(path);
			ticketBuffer.append(currentTime).append(secret);
			String hash = getTicket(ticketBuffer.toString());
			buf.append(root);
			buf.append(path);
			buf.append("?expires=");
			buf.append(currentTime);						
			buf.append("&ticket=");
			buf.append(hash);
			buf.append("&custid="+custID);
		}
		catch (Exception e)
		{
      		e.printStackTrace();
		}

		return buf.toString();
	}

	private String getTicket(String s) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] di = md5.digest(s.getBytes());
		return asHex(di);
	}

	private String getPath(String fullUrl)
	{
		String rest = fullUrl.substring(7, fullUrl.length());
		int slashIndex = rest.indexOf("/");
		int hashIndex = rest.indexOf("#");
		if (hashIndex > -1)
		{
			return rest.substring(slashIndex, hashIndex);
		}
		else
		{
			return rest.substring(slashIndex);
		}
	}

	private String getRoot(String fullUrl)
	{	
		String part1 = fullUrl.substring(0, 7);
		String rest = fullUrl.substring(7, fullUrl.length());
		int slashIndex = rest.indexOf("/");
		return part1 + rest.substring(0, slashIndex);
	}
	
	private String asHex(byte[] hash)
	{
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++)
		{
			if (((int) hash[i] & 0xff) < 0x10)
			{
				buf.append("0");
			}

			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}

		return buf.toString();
	}

	public static void main(String[] args) {
  }
}
