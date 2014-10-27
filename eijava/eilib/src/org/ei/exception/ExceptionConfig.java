
package org.ei.exception;

import java.util.Hashtable;

import org.ei.config.ConfigService;
import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ExceptionConfig
{
	private static ExceptionConfig instance;
	private static final String EXCEPTION_CONFIG = "ExceptionConfig.xml";
	private static final String DEFAULT_EXCEPTIONID = "default";
	private Hashtable messageTable = new Hashtable();



	public static synchronized ExceptionConfig getInstance(String path)
		throws ExceptionConfigException
	{
		//System.out.println("Getting started");
		if(instance == null)
		{
			instance = new ExceptionConfig(path);		
		}

		return instance;
	}


	public static synchronized ExceptionConfig getInstance()
		throws ExceptionConfigException
	{
		if(instance == null)
		{
			instance = new ExceptionConfig(ConfigService.getConfigPath(EXCEPTION_CONFIG));
		}

		return instance;
	}

	public ExceptionConfig(String configFilePath)
		throws ExceptionConfigException
	{
		try
		{
			DOMParser parser = new DOMParser();
			Document tdoc = parser.parse(configFilePath);
			NodeList topNode = tdoc.getChildNodes();
			for(int x=0; x<topNode.getLength(); ++x)
			{
				
				Node econfNode = topNode.item(x);
				
				NodeList eList = econfNode.getChildNodes();
				for(int y=0; y<eList.getLength(); ++y)
				{
					
					Node eNode = eList.item(y);
					//System.out.println("Node:");
					if(eNode.hasChildNodes())
					{
						NodeList plist = eNode.getChildNodes();
						Node idNode = plist.item(1);
						//System.out.println(idNode.getNodeName());
						Node messageNode = plist.item(3);
						String ID = idNode.getFirstChild().getNodeValue();
						//System.out.println(ID);
						String message = messageNode.getFirstChild().getNodeValue();
						messageTable.put(ID.trim(), message.trim());
					}
				}
				
			}
		}
		catch(Exception e)
		{
			throw new ExceptionConfigException(e);
		}
	}

	public String getDefaultMessage()
	{
		return (String)messageTable.get(DEFAULT_EXCEPTIONID);
	}

	public String getMessage(String exceptionID)
	{
		return (String)messageTable.get(exceptionID);
	}

	public boolean containsKey(String exceptionID)
	{
		return messageTable.containsKey(exceptionID);
	}

	public static void main(String args[])
		throws Exception
	{
		ExceptionConfig config = ExceptionConfig.getInstance(args[0]);
		System.out.println(config.getMessage("CurrentSessionException"));
	}

}
