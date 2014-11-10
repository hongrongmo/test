/*
 * Created on 17.10.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.tags;

import java.io.IOException;
import java.io.Writer;

import org.ei.tags.Color.IColor;

public class TagGroup
{


    private String groupID;
    private String title;
    private String description;
    private long datefounded;
    private String color = "Red";
    private String ownerid;
    private Member[] members;
    private Tag[] tags;
    private int position = 0;


    public void setTags(Tag[] tags)
    {
        this.tags = tags;
    }

	public Tag[] getTags()
	{
		return this.tags;
	}

    public void setPosition(int p)
    {
        position = p;
    }

    public String getRemainder()
    {
        int remainder = (position % 2);
        return String.valueOf(remainder);

    }

    public String getPosition()
    {
        return String.valueOf(position);

    }

    public void setMembers(Member[] members)
    {
		this.members = members;
    }

    public Member[] getMembers()
    {
        return members;
    }

    public Member getMember(String userID)
    {
		if(members != null)
		{
			for(int i=0; i<members.length; i++)
			{
				if(members[i].getMemberID().equals(userID))
				{
					return members[i];
				}
			}
		}

		return null;
	}

    public IColor getColorByID()
    {
        return Color.getInstance().getColorByID(color);
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public void setDatefounded()
    {
        this.datefounded = System.currentTimeMillis();
    }

    public void setDatefounded(long l)
    {
        this.datefounded = l;
    }

    public long getDatefounded()
    {
        return this.datefounded;
    }

    public String getDate()
    {
        java.util.Date time = new java.util.Date(datefounded);
        return time.toLocaleString();
    }

    public TagGroup shallowCopy()
    {
		TagGroup copy = new TagGroup();
		copy.setColor(getColor());
		copy.setDescription(getDescription());
		copy.setGroupID(getGroupID());
		copy.setTitle(getTitle());
		copy.setDatefounded(getDatefounded());
		copy.setOwnerid(getOwnerid());
		return copy;
	}


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getGroupID()
    {
        return groupID;
    }


    public void setGroupID(String groupID)
    {
        this.groupID = groupID;
    }


    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOwnerid()
    {
        return ownerid;
    }

    public void setOwnerid(String ownerid)
    {
        this.ownerid = ownerid;
    }

    public void toXML(Writer out)
    	throws IOException
    {
		Color colorHelper = Color.getInstance();
        out.write("<TGROUP>");
        out.write("<GID>");
		out.write(groupID);
        out.write("</GID>");
        out.write("<OWNERID>");
		out.write(ownerid);
        out.write("</OWNERID>");
        out.write("<GTITLE><![CDATA[");
        out.write(title);
        out.write("]]>");
        out.write("</GTITLE>");

        if (description != null)
        {
            out.write("<DESCRIPTION>");
            out.write("<![CDATA[");
            out.write(description);
            out.write("]]>");
            out.write("</DESCRIPTION>");
        }

        out.write("<DATEFOUND>");
        out.write(this.getDate());
        out.write("</DATEFOUND>");
        colorHelper.toXML(color, out);

        if(members != null &&
           members.length > 0)
        {
			out.write("<MEMBERS>");
			for(int i=0; i<members.length; i++)
			{
				members[i].toXML(out);
			}
			out.write("</MEMBERS>");
		}

		if(tags != null &&
		   tags.length > 0)
		{
			out.write("<TAGS>");
			for(int i=0; i<tags.length; i++)
			{
				tags[i].toXML(out);
			}
			out.write("</TAGS>");
		}
        out.write("</TGROUP>");
    }
}
