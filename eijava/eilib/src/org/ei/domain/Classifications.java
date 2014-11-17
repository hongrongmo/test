package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class Classifications
			implements ElementData
{
	protected Classification[] classifications;
	protected Key key;
	protected boolean labels = false;

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public Classifications(ClassificationID[] ids)
	{
		this.key = Keys.CLASS_CODES;
		classifications = loadClassifications(this.key,ids);
	}

	public Classifications (Key key,
							ClassificationID[] ids)
	{
		this.key = key;
		classifications = loadClassifications(key,ids);

	}

	public Classifications (Key key,
						   	Classification[] codes)
	{
		this.key = key;
		this.classifications = codes;

	}

	public Classifications (Key key,
		    String[] codes,
		    Database database)
	{
	    this.key = key;
	    ClassificationID[] ids = new ClassificationID[codes.length];

	    for(int i=0;i<codes.length;i++)
	    {
	        ids[i]= new ClassificationID(codes[i], database);
	    }
	    classifications = loadClassifications(key,ids);
	}


	protected Classification[] loadClassifications(Key key,
												   ClassificationID[] ids)
	{
		Classification[] cls = new Classification[ids.length];
		for (int i = 0; i< ids.length; i++)
		{
			Database database = ids[i].getDatabase();
			DataDictionary dataDictionary = database.getDataDictionary();

			String classTitle = dataDictionary.getClassCodeTitle(ids[i].getClassCode());
			Classification cl = new Classification(key, ids[i]);
			if(database.getMask() != 1024)
			{
				cl.setClassTitle(classTitle);
			}
			cls[i] = cl;
		}

		return cls;
	}


	protected Classification[] loadClassifications(
	        									Key key,
	        									ClassificationID[] ids,
	        									String [] titles)
	{
	    Classification[] cls = new Classification[ids.length];
	    for (int i = 0; i< ids.length; i++)
	    {
	        Database database = ids[i].getDatabase();
	        DataDictionary dataDictionary = database.getDataDictionary();

	        String classTitle = titles[i];
	        Classification cl = new Classification(key, ids[i]);
	        cl.setClassTitle(classTitle);
	        cls[i] = cl;
	    }

	    return cls;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}


	public String[] getElementData()
	{
		String[] sarray = new String[classifications.length];
		for(int i=0; i<classifications.length; i++)
		{
			String ctitle = classifications[i].getClassTitle();
			if(ctitle != null)
			{
				sarray[i] = ctitle;
			}
			else
			{
				sarray[i] = "";
			}
		}

		return sarray;
	}

	public void setElementData(String[] sarray)
	{
		for(int i=0; i<sarray.length; i++)
		{
			classifications[i].setClassTitle(sarray[i]);
		}
	}


	public void toXML(Writer out)
		throws IOException
	{

       	out.write("<");
       	out.write(this.key.getKey());
		if(this.labels && (this.key.getLabel() != null))
		{
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
		}
       	out.write(">");
		for (int i = 0; i< classifications.length; i++)
		{
			classifications[i].toXML(out);
		}
		out.write("</");
		out.write(this.key.getKey());
    	out.write(">");
	}
}
