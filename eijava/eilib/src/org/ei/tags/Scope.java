package org.ei.tags;

import java.io.*;

public class Scope
{
	public static final int SCOPE_PUBLIC = 1;
	public static final int SCOPE_PRIVATE = 2;
	public static final int SCOPE_GROUP = 3;
	public static final int SCOPE_INSTITUTION = 4;


	private TagGroup[] groups;
	private int scope = -1;
	private String groupScope;
	private String customerID;
	private String userID;

	public Scope(String userID,
				 int scope,
				 String groupScope,
				 String customerID,
				 TagGroup[] groups)
	{
		this.scope = scope;
		this.groupScope = groupScope;
		this.groups = groups;
		this.customerID = customerID;
		this.userID = userID;
	}

	public void toXML(Writer out)
		throws Exception
	{
		//FileWriter out = new FileWriter("test.xml");
		out.write("<SCOPES>");

		out.write("<SCOPE name='Public' value='");
		out.write(Integer.toString(SCOPE_PUBLIC));
		out.write("'");
		if(this.scope == SCOPE_PUBLIC ||
		   this.scope == -1)
		{
			out.write(" selected='true'");
		}
		out.write("/>");


		if(userID != null)
		{
			out.write("<SCOPE name='Private' value='");
			out.write(Integer.toString(SCOPE_PRIVATE));
			out.write("'");
			if(this.scope == SCOPE_PRIVATE)
			{
				out.write(" selected='true'");
			}
			out.write("/>");
		}

		out.write("<SCOPE name='My Institution' value='");
		out.write(Integer.toString(SCOPE_INSTITUTION));
		out.write("'");
		if(this.scope == SCOPE_INSTITUTION)
		{
			out.write(" selected='true'");
		}
		out.write("/>");





		if(this.groups != null)
		{
			for(int i=0; i<groups.length; i++)
			{
				String title = groups[i].getTitle();
				String groupID = groups[i].getGroupID();
				out.write("<SCOPE name='");
				out.write(title);
				out.write("' value='");
				out.write(Integer.toString(SCOPE_GROUP));
				out.write(":");
				out.write(groupID);
				out.write("'");
//				System.out.println("Group passed in:"+groupScope);
//				System.out.println("Group from database:"+groupID);

				if(this.scope == SCOPE_GROUP &&
				   this.groupScope != null &&
				   this.groupScope.equals(groupID))
				{
					out.write(" selected='true'");
				}
				out.write("/>");
			}
		}
		out.write("</SCOPES>");
		//out.close();
	}
}