package org.ei.tags;


import java.io.*;
import java.util.*;
import org.ei.domain.DocID;

public class TagBubble
{
	private String backURL;
	private String nextURL;
	private String addTagURL;
	private String editTagURL;
	private String puserID;
	private String customerID;
	private DocID docID;
	private Tag[] tags;
	private TagGroupBroker groupBroker = null;
	Map tagGroups;

	public TagBubble(String addTagURL,
					 String editTagURL,
				     String nextURL,
					 String puserID,
					 String customerID,
					 DocID docID,
					 Comparator comp)
		throws Exception
	{
		this.addTagURL = addTagURL;
		this.nextURL = nextURL;
		this.editTagURL = editTagURL;
		this.puserID = puserID;
		this.customerID = customerID;
		this.docID = docID;


		if((puserID != null) &&
		   (puserID.trim().length() != 0))
		{
			groupBroker = new TagGroupBroker();
		}

		TagBroker tagBroker = new TagBroker(groupBroker);

		tags = tagBroker.getTags(docID.getDocID(),
							     puserID,
							     customerID,
							     comp);
	}


	public Tag[] getTags()
	{
	    return this.tags;
	}

	public void toXML(Writer out)
		throws Exception
	{
		out.write("<TAG-BUBBLE>");
		if(puserID != null)
		{
			out.write("<LOGGED-IN>true</LOGGED-IN>");
			out.write("<PUSER>");
			out.write(this.puserID);
			out.write("</PUSER>");
			TagGroup[] groups = groupBroker.getGroups(this.puserID, false);
			out.write("<TGROUPS>");
			for(int i=0; i<groups.length; i++)
			{
				//System.out.println("Group:"+groups[i].getTitle());
				groups[i].toXML(out);
			}
			out.write("</TGROUPS>");
		}
		out.write("<CUSTID>");
		out.write(this.customerID);
		out.write("</CUSTID>");
		out.write("<TAG-DOCID>");
		out.write(docID.getDocID());
		out.write(":");
		out.write(Integer.toString(docID.getDatabase().getMask()));
		String collection = docID.getCollection();
		if(collection != null)
		{
			out.write(":");
			out.write(collection);
		}
		out.write("</TAG-DOCID>");
		out.write("<URLS>");
		out.write("<NEXT-URL>");
		out.write(this.nextURL);
		out.write("</NEXT-URL>");
		out.write("<ADD-TAG-URL>");
		out.write(this.addTagURL);
		out.write("</ADD-TAG-URL>");
		out.write("<EDIT-TAG-URL>");
				//System.out.println("###############Edit URL:"+this.editTagURL);

		out.write(this.editTagURL);
		out.write("</EDIT-TAG-URL>");
		out.write("</URLS>");
		if(tags != null)
		{
			out.write("<TAGS>");
			for(int i=0; i<tags.length; i++)
			{
				tags[i].toXML(out);
			}
			out.write("</TAGS>");
		}


		out.write("</TAG-BUBBLE>");
	}
}

