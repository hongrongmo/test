/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import org.ei.domain.ClassNodeManager;
import org.ei.util.StringUtil;

import java.util.regex.Pattern;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PatentCodeModifier extends EiModifier
{
    public PatentCodeModifier(int i, String slable, String svalue)
    {
        super(i, slable, svalue);
    }

    static public String cleanPatentModifier(String modstring)
    {
        if(modstring != null)
        {
            Pattern slash = Pattern.compile("SLASH",Pattern.CASE_INSENSITIVE);
            Pattern period = Pattern.compile("PERIOD",Pattern.CASE_INSENSITIVE);

            modstring = slash.matcher(modstring).replaceAll("\\/");
            modstring = period.matcher(modstring).replaceAll("\\.");
        }
        return modstring;
    }


    public abstract String seekCode(ClassNodeManager cm) throws Exception;

    /**
     * @return
     */
    public String getTitle()
    {
        String title = StringUtil.EMPTY_STRING ;
        try
        {
            ClassNodeManager cm = ClassNodeManager.getInstance();

            title = seekCode(cm);

        }
        catch (Exception e)
        {
            System.out.println("error is " + e.getMessage());
        }

        return (title != null) ? title : StringUtil.EMPTY_STRING ;
    }

	public String toXML()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<MODIFIER COUNT=\"").append(this.getCount()).append("\">");

		if(!StringUtil.EMPTY_STRING.equals(this.getValue()))
		{
			sb.append("<VALUE><![CDATA[").append(this.getValue()).append("]]></VALUE>");
		}

		sb.append("<LABEL><![CDATA[").append(this.getLabel()).append("]]></LABEL>")
		.append("<TITLE><![CDATA[").append(this.getTitle()).append("]]></TITLE>")
		.append("</MODIFIER>");

		return sb.toString();
	}


}
