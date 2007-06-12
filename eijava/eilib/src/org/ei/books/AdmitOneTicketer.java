package org.ei.books;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;

public class AdmitOneTicketer
{

	private Perl5Util perl = new Perl5Util();
	private String secretname = "Elsevier";
	private String secret = "35738437";
	private static final String SHRDKEY = "!MM01234-5-6789MM#";
	private String id = "Authorised";
	private String baseUrl = "http://referexengineering.elsevier.com";
	private static final long expireIn =   172800000L; // 48 Hours
	private static final long createEvery = 86400000L; // 24 Hours
	private long createTime = -1L;
	private Hashtable tickets;
	private static AdmitOneTicketer instance;
	private static int count = 0;

	public static synchronized AdmitOneTicketer getInstance()
	{
		if(instance == null)
		{
		   instance = new AdmitOneTicketer();
		}

		return instance;
	}

	private AdmitOneTicketer()
	{
		tickets = new Properties();

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
		System.out.println(ticket);
		
		return ticket;
	}
	
	public String getSectionTicketedURL(String link,
										String custID,
										long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		String parsedLink = perl.substitute("s/\\s+//g", link);
		buf.append(parsedLink);
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}

	public String getBookTicketedURL(String isbn,
									 String custID,
									 long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		buf.append("e");
		buf.append(isbn);
		buf.append(".pdf");
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}



	private synchronized String getTicketedURL(String fullUrl,
											   String custID,
											   long currentTime)
		throws BookException
	{
		Ticket ticket = null;
		StringBuffer buf = new StringBuffer();
		String formattedExpires;


		try
		{

			String anchor = null;
			String root = getRoot(fullUrl);
			String path = getPath(fullUrl);

			if (fullUrl.indexOf("#") > -1)
			{
				anchor = getAnchor(fullUrl);
			}


			if((createTime == -1) ||
			   ((currentTime - createTime) > createEvery))
			{
				tickets = new Hashtable();
				createTime = currentTime;
			}
			else
			{
				ticket = (Ticket)tickets.get(path);
			}


			if(ticket == null)
			{
				ticket = new Ticket();
				formattedExpires = formatExpires(expireIn);
				ticket.t = getTicket(path + formattedExpires + secretname + secret + id);
				ticket.e = formattedExpires;
				tickets.put(path,ticket);
			}


			buf.append(root);
			buf.append(path);
			buf.append("?expires=");
			buf.append(ticket.e);
			buf.append("&secretname=");
			buf.append(URLEncoder.encode(secretname,"UTF-8"));
			buf.append("&id=");
			buf.append(URLEncoder.encode(id,"UTF-8"));
			buf.append("&ticket=");
			buf.append(ticket.t);
			buf.append("&f=d.exe");
			buf.append("&count="+Integer.toString(count++));
			buf.append("&custid="+custID);
			if(count > 1000000)
			{
				count = 0;
			}

			if (anchor != null)
			{
				buf.append("#nameddest=").append(anchor);
				buf.append("&zoom=100");
				buf.append("&pagemode=none");
			} else {
				buf.append("#page=1&zoom=100");
				buf.append("&pagemode=none");
			}
		}
		catch (Exception e)
		{
			throw new BookException(e);
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

	private String getAnchor(String fullUrl)
	{
		int hashIndex = fullUrl.indexOf("#") + 1;

		return fullUrl.substring(hashIndex);
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

	private String formatExpires(long expireIn)
	{
		long now = System.currentTimeMillis();
		long expired = now + AdmitOneTicketer.expireIn;
		Date d = new Date(expired);
        DateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        return sd.format(d);
       
	}


	class Ticket
	{
		public String t;
		public String e;
	}
}
