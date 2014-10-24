package org.ei.shibboleth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShibbolethIdpMap
{
	private static ShibbolethIdpMap instance = null;
	private Hashtable idpMap = new Hashtable();
	protected static Log log = LogFactory.getLog(ShibbolethIdpMap.class);

	public static ShibbolethIdpMap getInstance()
	{
		if(instance == null)
		{
			synchronized (ShibbolethIdpMap.class)
			{
				instance = new ShibbolethIdpMap();
				instance.fillIdpMap();
			}
		}

		return instance;

	}

	private void fillIdpMap()
	{
		idpMap = new Hashtable();
		BufferedReader bufferedReader = null;

		try
		{
			bufferedReader = new BufferedReader(new FileReader("shibboleth/shibbolethIDPs.txt"));

			if(bufferedReader != null)
			{
				while(bufferedReader.ready())
				{
					String line = bufferedReader.readLine();

					String[] idps = line.split("\t");
					if(idps != null)
						idpMap.put(idps[0], idps[1]);
				}
			System.out.println("Done loading shibboleth idps");

			}
			else
			{
				log.error("Reader is null");
				System.out.println("Reader is null");

			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("shibbolethIDPs.txt not found in shibboleth subdirectory");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bufferedReader.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
	}

	public String lookupIdp(String custid)
  	{
		if(idpMap.containsKey(custid))
		{
			return (String) idpMap.get(custid);
		}
		else
		{
			return null;
		}
  	}

}
