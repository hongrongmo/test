package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class Classification
    implements XMLSerializable
{
    private ClassificationID cid;
    private String status;
    private int level;
    private String classTitle;
    private String scopeNotes;
    private String seeAlsoRefs;
    private String seeRefs;
    private String historyScopeNotes;
	protected Key key;

    public Classification(ClassificationID cid)
    {
		this.key = Keys.CLASS_CODES;
		this.cid = cid;
	}

	public Classification(Key key,
						  ClassificationID cid)
	{
		this.key = key;
		this.cid = cid;
	}


    public ClassificationID getClassificationID()
    {
        return this.cid;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return this.status;
    }

    public int getLevel()
    {
        return this.level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getClassTitle()
    {
        return this.classTitle;
    }

    public void setClassTitle(String classTitle)
	{
//        System.out.println("setClassTitle::"+classTitle);
		this.classTitle = classTitle;
    }

    public String getScopeNotes()
    {
        return this.scopeNotes;
    }

    public void setScopeNotes(String scopeNotes)
    {
        this.scopeNotes = scopeNotes;
    }

    public String getHistoryScopeNotes()
    {
        return this.historyScopeNotes;
    }

    public void setHistoryScopeNotes(String historyScopeNotes)
    {
        this.historyScopeNotes = historyScopeNotes;
    }

    public String getSeeRefs()
    {
        return this.seeRefs;
    }

    public void setSeeRefs(String seeRefs)
    {
        this.seeRefs = seeRefs;
    }

    public String getSeeAlsoRefs()
    {
        return this.seeAlsoRefs;
    }

    public void setSeeAlsoRefs(String seeAlsoRefs)
    {
        this.seeAlsoRefs = seeAlsoRefs;
    }

    public void toXML(Writer out)
        throws IOException
    {
        out.write("<");
        out.write(this.key.getSubKey());
        out.write(">");
        cid.toXML(out);
        out.write("<CTI><![CDATA[");
        out.write(notNull(getClassTitle()));
        out.write("]]></CTI>");
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