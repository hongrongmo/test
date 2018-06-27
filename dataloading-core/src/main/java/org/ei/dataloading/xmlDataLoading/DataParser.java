package org.ei.dataloading.xmlDataLoading;

//import org.ei.xml.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Stack;

import org.ei.dataloading.DataValidator;
import org.ei.common.LoadNumber;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DataParser  extends DefaultHandler
{
	public static String preferedDatabase = "GEO";
	String 		name;
	String 		sName;
	String 		subTagContent;
	Hashtable 	attributes;
	BaseElement element;
	private Stack nodes;
	OutputDataToFile out = null;
	static File fileName = null;
	static String loadNumber = null;
	//static final char delimited02 = ';';
	static final char delimited02 = 02;
	static final char delimited29 = 29;
	//static final char delimited29 = ':';
	static final char delimited30 = 30;
	//static final char delimited31 = 31;
	static final char delimited31 = '|';

	public static void main(String[] args) throws Exception
	{
		String name = null;
		if(args.length>0)
		{
			name = args[0];
		}
		else
		{
			System.out.println("please enter a filename");
			System.exit(1);
		}

		if(args.length>1)
		{
			loadNumber = args[1];
		}
		else
		{
			System.out.println("Please enter a week number");
			System.exit(1);
		}

		String loadN=Integer.toString(LoadNumber.getCurrentWeekNumber());

		if(!loadNumber.equals(loadN))
		{
			System.out.println("Load Number does not match the current week number:");
			System.out.print(loadN +", Are you sure (Y/N)? ");
			int ch = System.in.read();
			if((Character.toLowerCase((char)ch) != 'y') ||(Character.toUpperCase((char) ch) !='Y'))
			{
				System.exit(1);
			}
		}


		DataValidator d = new DataValidator();
		/*
		String[] fileNameArray = null;

		if(name.indexOf(";")>-1)
		{
			fileNameArray = name.split(";");
		}
		else
		{
			fileNameArray = new String[1];
			fileNameArray[0] = name;
		}

		for(int i=0;i<fileNameArray.length;i++)
		{
			*/
		 File fileName = new File(name);
		System.out.println("Main Input file:"+fileName.getPath());
		d.validateFile(fileName.getPath());
		System.out.println("Validation Done!");
		DataParser handler = new DataParser();
		File out = new File(fileName.getName()+".out");
		handler.out= new OutputDataToFile(out);
		XMLReader reader = org.ei.xml.SAXParserFactory.getDefaultSAXParser();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.parse(fileName.getPath());
		//}
		System.exit(1);
	}



	public void startElement(String uri, String localName,String qName,Attributes atts)
	{

		attributes = new Hashtable();
		try
		{

			if(qName.equalsIgnoreCase("inf"))
			{
				((BaseElement)nodes.peek()).setText("<inf>");
				return;
			}

			if(qName.equalsIgnoreCase("sup"))
			{
				((BaseElement)nodes.peek()).setText("<sup>");
				return;
			}


			String data;
			if(!localName.equals(""))
			{
				data = localName;
			}
			else
			{
				data = qName;
			}

			this.name = qName;

			data = data.replace('-','_');
			if(data.indexOf(":")>0)
			{
				data = data.substring(data.indexOf(":")+1);
			}


			data = data.substring(0,1).toUpperCase()+data.substring(1);

			if(data.equalsIgnoreCase("item"))
			{
				nodes = new Stack();
				attributes = new Hashtable();

			}

			BaseElement element = null;

			if(!data.equalsIgnoreCase("bibdataset"))
			{
				try
				{
					String className = "org.ei.data.xmlDataLoading."+data;
					element = (BaseElement) Class.forName(className).newInstance();
				}
				catch(Exception e)
				{
					//System.out.println("No class for element "+data+" error "+e);
				}

				if( element == null)
				{
					element = new BaseElement();
				}


				for(int i=0; i<atts.getLength(); i++)
				{
					String attributeName=atts.getQName(i);
					String attributeValue=atts.getValue(i);

					if(attributeName.indexOf(":")>0)
					{
						attributeName = attributeName.substring(attributeName.indexOf(":")+1);
					}

					attributeName = data+"_"+attributeName;
					setAttributes(element,attributeName,attributeValue);
				}

				if(!(qName.equals("inf")||qName.equals("sup")))
				{
					nodes.push(element);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error from startElement= "+e);
		}
	}

	public void endElement(String namespace,String localname,String qName) throws SAXException
	{

		try
		{
			if(!qName.equalsIgnoreCase("bibdataset"))
			{
				if(!qName.equalsIgnoreCase("item"))
				{
					if(qName.equalsIgnoreCase("inf"))
					{
						((BaseElement)nodes.peek()).setText("</inf>");
					}
					else if(qName.equalsIgnoreCase("sup"))
					{
						((BaseElement)nodes.peek()).setText("</sup>");
					}
					else
					{
						element = (BaseElement)nodes.pop();
						if(!nodes.empty())
						{
							setProperty(qName,nodes.peek(),element);
						}
					}
				}
				else
				{
					processRecord(nodes);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error from endElement: "+e);
		}


	}


	public void characters(char[] text, int start, int length)
	{
		String content = null;

		char[] dest = new char[length*8];
		int newlen =0;

		if((!name.equalsIgnoreCase("bibdataset"))&&(!name.equalsIgnoreCase("item")))
		{
			try
			{
				if(length>0)
				{
					newlen = escape(text, start, length, dest);
					content = new String(dest,0,newlen);
					//content = new String(text,start,length);
					((BaseElement)(nodes.peek())).setText(content);
				}

			}
			catch(Exception e)
			{
				System.out.println("Exception "+" name1 "+name+" start= "+start+" length= "+length+" dest= "+dest.toString()+" newlen= "+newlen+" "+e);
			}

		}

	}


	void setProperty(String name, Object target, Object value)
	{

		Method method = null;
		name = name.replace('-','_');
		if(name.indexOf(":")>0)
		{
			name = name.substring(name.indexOf(":")+1);
		}

		name = name.substring(0,1).toUpperCase()+name.substring(1);

		try
		{
			method = target.getClass().getMethod("add"+name,new Class[]{value.getClass()});
		}
		catch(NoSuchMethodException e)
		{

		}

		if(method == null)
		{
			try
			{
				method = target.getClass().getMethod("set"+name,new Class[]{value.getClass()});
			}
			catch(NoSuchMethodException e)
			{

			}
		}

		if(method == null)
		{
			try
			{
				value 	= ((BaseElement)value).getText();
				method 	= target.getClass().getMethod("set"+name,new Class[]{String.class});
			}
			catch(NoSuchMethodException e)
			{

			}
		}

		if(method != null)
		{
			try
			{
				method.invoke(target,new Object[]{value});
			}
			catch(Exception e)
			{

			}
		}


		try
		{
			method = value.getClass().getMethod("set"+name,new Class[]{String.class});
			if(method!=null)
			{
				String content = ((BaseElement)value).getText();
				method.invoke(value,new Object[]{content});
			}
		}
		catch(Exception e)
		{

		}



	}

	void setAttributes(Object target,String methodName,String value)
	{
		try
		{
			Method method = target.getClass().getMethod("set"+methodName,new Class[]{String.class});
			method.invoke(target,new Object[]{value});
		}
		catch(Exception e)
		{

		}
	}


	void processRecord(Stack nodes)
	{

		Hashtable dataTable = new Hashtable();
		Object topNodes = nodes.peek();
		Item itemObject = (Item)topNodes;
		RetrievedData rData = new RetrievedData();
		rData.setItem(itemObject,dataTable);
		if(out == null)
		{
			//out= new OutputDataToFile();
		}
		out.outputData(dataTable);
	}

	private int escape(char ch[], int start, int length, char[] out)
	{
	   int o = 0;
	   for (int i = start; i < start+length; i++)
	   {
	     if (ch[i]=='<')
	     {
	       ("<").getChars(0, 1, out, o); o+=1;
	     }
	     else if (ch[i]=='>')
	     {
	       (">").getChars(0, 1, out, o); o+=1;
	     }
	     else if (ch[i]=='&')
	     {
	       ("&").getChars(0, 1, out, o); o+=1;
	     }
	     else if (ch[i]=='\"')
	     {
	       ("\"").getChars(0, 1, out, o); o+=1;
	     }
	     else if (ch[i]=='\'')
	     {
	       ("'").getChars(0, 1, out, o); o+=1;
	     }
	     else if (ch[i]<127 && ch[i]!=124)
	     {
	       out[o++]=ch[i];
	     }
	     else
	     {
	       // output character reference
	       out[o++]='&';
	       out[o++]='#';
	       out[o++]='x';
	       String code = Integer.toHexString(ch[i]);
	       int len = code.length();
	       code.getChars(0, len, out, o);
	       o+=len;
	       out[o++]=';';
	     }
	   }

	   return o;
	 }

}
