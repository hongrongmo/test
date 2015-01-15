package org.ei.thesaurus;

import java.io.IOException;
import java.io.Writer;

import org.ei.domain.Database;



public class ThesaurusRecordID
	implements ThesaurusNode
{
	private int recordID = -1;
	private Database database;
	private String mainTerm;

	public ThesaurusRecordID(int recordID,
							 Database database)
	{
		this.recordID = recordID;
		this.database = database;
	}

	public ThesaurusRecordID(String mainTerm,
							 Database database)
	{
		this.mainTerm = mainTerm;
		this.database = database;
	}

	public ThesaurusRecordID(int recordID,
	 						 String mainTerm,
	 						 Database database)
	{
		this.recordID = recordID;
		this.mainTerm = mainTerm;
		this.database = database;
	}

	public void accept(ThesaurusNodeVisitor v)
		throws ThesaurusException
	{
		v.visitWith(this);
	}

	public int getRecordID()
	{
		return this.recordID;
	}

	public Database getDatabase()
	{
		return this.database;
	}

	public String getMainTerm()
	{
		return this.mainTerm;
	}

	public boolean equals(Object o)
	{
		boolean b = false;

		Class c = getClass();
		if(c.isInstance(o))
		{
			ThesaurusRecordID i = (ThesaurusRecordID)o;

			if(((i.getDatabase()).getID()).equals(getDatabase().getID()))
			{

				if(i.getRecordID() > -1 &&
				   getRecordID() > -1)
				{
					if(i.getRecordID() == getRecordID())
					{
						b = true;
					}
					else
					{
						b = false;
					}
				}
				else if(i.getMainTerm() != null &&
						getMainTerm() != null)
				{
					if(i.getMainTerm().toLowerCase().equals(getMainTerm().toLowerCase()))
					{
						b = true;
					}
					else
					{
						b = false;
					}
				}
				else
				{
					b = false;
				}
			}
			else
			{
				b = false;
			}
		}
		else
		{
			b = false;
		}

		return b;
	}

	public void toXML(Writer out)
		throws IOException
	{
		out.write("<TID>");

		out.write("</TID>");

	}

}