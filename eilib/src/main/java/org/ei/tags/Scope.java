package org.ei.tags;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.gui.ListBoxOption;

public class Scope
{
	public static final int SCOPE_PUBLIC = 1;
	public static final int SCOPE_PRIVATE = 2;
	public static final int SCOPE_GROUP = 3;
	public static final int SCOPE_INSTITUTION = 4;
	public static final int SCOPE_LOGIN = 6;


	private TagGroup[] groups;
	private int scope = -1;
	private String groupid;
	private String userid;

	public Scope(String userid,
				String scope) throws Exception  
	{
		this.scope = parseScope(scope);
		this.groupid = parseGroup(scope);
		this.userid= userid;
		if (!GenericValidator.isBlankOrNull(userid)) {
			TagGroupBroker groupBroker = new TagGroupBroker();
			this.groups = groupBroker.getGroups(userid, true);
		}
	}
	
	/**
	 * Parse the group id from a scope string
	 * @param scope
	 */
	public static String parseGroup(String scope) {
		String groupscope = "";
		if (GenericValidator.isBlankOrNull(scope)) return groupscope;
		if (scope.indexOf(":") > -1) {
			String[] scopeParts = scope.split(":");
			groupscope = scopeParts[1];
		}

		return groupscope;
	}
	
	/**
	 * Parse the integer value of the scope from input
	 * @param scope
	 * @return
	 */
	public static int parseScope(String scope) {
		int iscope = SCOPE_PUBLIC;
		if (GenericValidator.isBlankOrNull(scope)) return iscope;

		// Get the scope if present.
		if (scope.indexOf(":") > -1) {
			String[] scopeParts = scope.split(":");
			iscope = Integer.parseInt(scopeParts[0]);
		} else {
			iscope = Integer.parseInt(scope);
		}

		return iscope;
	}

	/**
	 * Return Map of Scope objects: key = scope name (Public, Private, etc.), value = Scope object
	 * 
	 * @param userID
	 * @param scope
	 * @param groupscope
	 * @param customerID
	 * @param groups
	 * @return
	 */
	public List<ListBoxOption> getScopeOptions() {
		List<ListBoxOption> scopeoptions = new ArrayList<ListBoxOption>();

		// Create options!  Order below is the order they will be presented!
		scopeoptions.add(new ListBoxOption(
				(scope == SCOPE_PUBLIC || scope == -1) ? Integer.toString(SCOPE_PUBLIC) : "", 
				Integer.toString(SCOPE_PUBLIC),"Public"));
		if (!GenericValidator.isBlankOrNull(userid)) {
			scopeoptions.add(new ListBoxOption(
					(scope == SCOPE_PRIVATE) ? Integer.toString(SCOPE_PRIVATE) : "", 
					Integer.toString(SCOPE_PRIVATE), "Private"));
		}
		scopeoptions.add(new ListBoxOption(
				(scope == SCOPE_INSTITUTION) ? Integer.toString(SCOPE_INSTITUTION) : "", 
				Integer.toString(SCOPE_INSTITUTION), "My Institution"));
		if(groups != null)
		{
			for (int i = 0; i < groups.length; i++) {
				String groupvalue = Integer.toString(SCOPE_GROUP) + ":"
						+ groups[i].getGroupID();
				String selected;
				if (scope == 6 && i == 0) {
					// Select the first group in this case
					selected = groupvalue;
				} else {
					selected = scope + ":" + groupid;
				}
				scopeoptions.add(new ListBoxOption(selected,groupvalue, groups[i].getTitle()));
			}
		}
		
		return scopeoptions;
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


		if(!GenericValidator.isBlankOrNull(userid))
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
				   this.groupid != null &&
				   this.groupid.equals(groupID))
				{
					out.write(" selected='true'");
				}
				out.write("/>");
			}
		}
		out.write("</SCOPES>");
		//out.close();
	}

	public TagGroup[] getGroups() {
		return groups;
	}

	public void setGroups(TagGroup[] groups) {
		this.groups = groups;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}