package org.ei.query.base;


import java.util.Vector;

public class FieldException
	extends Exception
{

	private Vector fields;

	public void setFields(Vector fields)
	{
		this.fields = fields;
	}

	public Vector getFields()
	{
		return this.fields;
	}

	public String getMessage()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<DISPLAY>Query Error, The following field(s) do not exist: ");
		for(int i=0; i<fields.size(); ++i)
		{
			if(i>0)
			{
				buf.append(", ");
			}

			buf.append((String)fields.get(i));
		}


		buf.append("</DISPLAY>");
		return buf.toString();
	}
}
