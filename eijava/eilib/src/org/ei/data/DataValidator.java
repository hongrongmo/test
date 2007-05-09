package org.ei.data;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DataValidator
{

	private XMLReader validator;



	public DataValidator()
		throws Exception
	{

		this.validator = new org.apache.xerces.parsers.SAXParser();
		validator.setFeature("http://xml.org/sax/features/validation",
		                     true);

	}


	public void validateFile(String filepath)
	{
		FileOutputStream fout = null;
		FileInputStream fin = null;
		PrintWriter log = null;

		try
		{
			fin = new FileInputStream(filepath);
			log = new PrintWriter(new FileWriter("validator.log", true));
			log.println((new Date()).toString()+":Validating "+ filepath);
			validator.parse(new InputSource(fin));
			log.println("Done");
		}
		catch(Exception e)
		{
			if(log!=null)
				e.printStackTrace(log);
			else
				e.printStackTrace();
		}
		finally
		{
			if(log != null)
			{
				try
				{
					log.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(fin != null)
			{
				try
				{
					fin.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}

			}
		}

	}


}