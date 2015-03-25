package org.ei.domain;

import java.util.Hashtable;

public class PublisherBroker
{
	private static PublisherBroker instance;
	private Hashtable<String, String> issnTable=null;
	private Hashtable<String, String> ieeeTable=null;
    private Hashtable<String, String> ojpsTable=null;

	public static synchronized PublisherBroker getInstance()
	{
		if(instance == null)
		{
			instance = new PublisherBroker();
		}

		return instance;
	}

	private PublisherBroker()
	{
		issnTable = (new IssnTable()).getIssnTable();
		ieeeTable = (new IeeeTable()).getIeeeTable();
		ojpsTable = (new OjpsTable()).getOjpsTable();
	}

	public String fetchPubID(String issn)
			throws PublisherException
	{
		return (String)issnTable.get(issn);
	}

	public String fetchIEEEPunumber(String issn)
		throws PublisherException
	{
		return (String)ieeeTable.get(issn);
	}

	public String fetchOJPSConumber(String issn)
	                throws PublisherException
    {
		 return (String)ojpsTable.get(issn);
	}
}

