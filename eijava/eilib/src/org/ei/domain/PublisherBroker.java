package org.ei.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.ei.connectionpool.ConnectionBroker;


public class PublisherBroker
{
	private static PublisherBroker instance;
	private Hashtable issnTable=null;
	private Hashtable ieeeTable=null;
    private Hashtable ojpsTable=null;

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

