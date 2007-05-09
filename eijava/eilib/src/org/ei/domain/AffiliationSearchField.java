
package org.ei.domain;


public class AffiliationSearchField
	extends SearchField
{

	public AffiliationSearchField(String ID,
								  String title)
	{
		super(ID, title);
	}

	public String getTitle(Database databases[])
	{
		String t = "Affiliation";
		boolean assignee = hasAssignee(databases);
		boolean affil = hasAffil(databases);
		if(assignee && affil)
		{
			t = "Affiliation/Assignee";
		}
		else if(assignee)
		{
			t = "Assignee";
		}
		else if(affil)
		{
			t = "Affiliation";
		}

		return t;
	}

	private boolean hasAssignee(Database[] databases)
	{
		for(int i=0; i<databases.length; i++)
		{
			if(databases[i].getMask() == 16384||
			   databases[i].getMask() == 32768)
			{
				return true;
			}
		}

		return false;
	}


	private boolean hasAffil(Database[] databases)
	{
		for(int i=0; i<databases.length; i++)
		{
			if(databases[i].getMask() == 1||
			   databases[i].getMask() == 2 ||
			   databases[i].getMask() == 8192 ||
			   databases[i].getMask() == 4)
			{
				return true;
			}
		}

		return false;
	}
}