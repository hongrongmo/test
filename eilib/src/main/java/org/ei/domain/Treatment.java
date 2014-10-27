package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class Treatment
    implements XMLSerializable
{
    private String treatmentCode;
    private String treatmentTitle;
	protected Key key;

    public Treatment(String treatmentCode,
    				 String treatmentTitle)
    {
		this.key = Keys.CLASS_CODES;
        this.treatmentCode = treatmentCode;
        this.treatmentTitle = treatmentTitle;
    }


	public Treatment(Key key,
					 String treatmentCode,
					 String treatmentTitle)
	{
		this.key = key;
		this.treatmentCode = treatmentCode;
		this.treatmentTitle = treatmentTitle;
	}


    public void toXML(Writer out)
        throws IOException
    {
        out.write("<");
        out.write(this.key.getSubKey());
        out.write(">");
        out.write("<TCO><![CDATA[");
        out.write(this.treatmentCode);
        out.write("]]></TCO>");
        out.write("<TTI><![CDATA[");
        out.write(notNull(this.treatmentTitle));
        out.write("]]></TTI>");
        out.write("</");
		out.write(this.key.getSubKey());
        out.write(">");
    }

    private String notNull(String s)
    {
        if(s == null)
        {
            return " ";
        }

        return s;
    }
}