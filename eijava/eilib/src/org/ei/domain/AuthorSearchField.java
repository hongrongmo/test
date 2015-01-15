
package org.ei.domain;


public class AuthorSearchField
	extends SearchField
{

	public AuthorSearchField(String ID,
							 String title)
	{
		super(ID, title);
	}

	public String getTitle(Database databases[])
	{
		String t = "Author";
		boolean inventor = hasInventor(databases);
		boolean author = hasAuthor(databases);

		if(inventor && author)
		{
			t = "Author/Inventor";
		}
		else if(inventor)
		{
			t = "Inventor";
		}
		else if(author)
		{
			t = "Author";
		}

		return t;
	}

	private boolean hasInventor(Database[] databases)
	{
		for(int i=0; i<databases.length; i++)
		{
			if(databases[i].getMask() == 16384||
			   databases[i].getMask() == 32768 ||
			   databases[i].getMask() == 2048)
			{
				return true;
			}
		}

		return false;
	}


	private boolean hasAuthor(Database[] databases)
	{
		for(int i=0; i<databases.length; i++)
		{
			if(databases[i].getMask() == 1   ||
			   databases[i].getMask() == 2   ||
			   databases[i].getMask() == 4   ||
			   databases[i].getMask() == 64  ||
			   databases[i].getMask() == 128 ||
			   databases[i].getMask() == 256 ||
			   databases[i].getMask() == 1024 ||
			   databases[i].getMask() == 8192)
			{
				return true;
			}
		}

		return false;
	}
}