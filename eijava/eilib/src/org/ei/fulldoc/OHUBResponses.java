
package org.ei.fulldoc;

import java.io.InputStream;
import java.util.Vector;

import org.ei.util.GUID;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class OHUBResponses
{

	private String[] urls;
	private Vector responses = new Vector();
	private String OID;


	public String getOID()
	{
		return OID;
	}

	public OHUBResponses(InputStream in)
		throws Exception
	{
		OID = new GUID().toString();
		XMLReader reader = org.ei.xml.SAXParserFactory.getDefaultSAXParser();
		DefaultHandler handler = new ResponseBuilder();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.parse(new InputSource(in));
	}

	public void addResponse(OHUBResponse response)
	{
		responses.addElement(response);
	}

	public OHUBResponse responseAt(int index)
	{
		return (OHUBResponse)responses.elementAt(index);
	}

	public int countResponses()
	{
		return responses.size();
	}

	class ResponseBuilder extends DefaultHandler
	{

		private String currentID;
		private OHUBResponse currentResponse;
		private OHUBResponseItem currentItem;
		private String currentString = "";

		private int state = -1;;

		private int LOCATION = 1;


		public void startElement(String uri,
			       		 String local,
					 String raw,
                             		 Attributes attrs)
		{

  			if(raw.equals("response"))
			{
				currentResponse = new OHUBResponse();
			}
			else if(raw.equals("response-item"))
			{
				currentItem = new OHUBResponseItem();
			}
			else if(raw.equals("location"))
			{
				state = LOCATION;
			}
    		}


		 public void characters(char ch[], int start, int length)
		 {
			 if(state == LOCATION)
			 {
        			currentString = currentString + new String(ch, start, length);
			 	//System.out.println("Current String:"+ currentString);
			 }
		}


		public void endElement(String uri, String local, String raw)
		{
			if(raw.equals("location"))
			{
				currentItem.setURL(currentString);
				currentString = "";
				state = -1;
			}
			else if(raw.equals("response-item"))
			{
				if(currentResponse.itemCount() < 3)
				{
					currentResponse.addItem(currentItem);
				}
			}
			else if(raw.equals("response"))
			{
				responses.addElement(currentResponse);
			}
    		} //
	}
}
