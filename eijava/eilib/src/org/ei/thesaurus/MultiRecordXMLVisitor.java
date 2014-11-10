package org.ei.thesaurus;

import java.io.Writer;


public class MultiRecordXMLVisitor
	implements ThesaurusNodeVisitor
{
	protected Writer out;
	protected String status;



	public MultiRecordXMLVisitor(Writer out)
	{
		this.out = out;
	}

	public void visitWith(ThesaurusPage p)
		throws ThesaurusException
	{
		try
		{
			out.write("<PAGE>");
			for(int i=0; i<p.size(); i++)
			{
				ThesaurusRecord rec = p.get(i);
				if(rec != null)
				{
					rec.accept(this);
				}
			}

			out.write("</PAGE>");
		}
		catch(Exception e)
		{
			throw new ThesaurusException(e);
		}
	}

	public void visitWith(ThesaurusRecord rec)
		throws ThesaurusException
	{
		try
		{
			out.write("<TREC>");
			ThesaurusRecordID recID = rec.getRecID();
			this.status = rec.getStatus();
			recID.accept(this);

			out.write("<NTC>");
			out.write(Integer.toString(rec.countNarrowerTerms()));
			out.write("</NTC>");
			out.write("</TREC>");
		}
		catch(Exception e)
		{
			throw new ThesaurusException(e);
		}
	}

	public void visitWith(ThesaurusRecordID recID)
		throws ThesaurusException
	{
		try
		{
			out.write("<ID>");
			out.write("<RNUM>");
			out.write(Integer.toString(recID.getRecordID()));
			out.write("</RNUM>");
			out.write("<MT>");
			if(this.status.equals("C"))
			{
				out.write("<CU><![CDATA[");
				out.write(recID.getMainTerm());
				out.write("]]></CU>");
			}
			else if(this.status.equals("L"))
			{
				out.write("<LE><![CDATA[");
				out.write(recID.getMainTerm());
				out.write("]]></LE>");
			}
			else
			{
				out.write("<PR><![CDATA[");
				out.write(recID.getMainTerm());
				out.write("]]></PR>");
			}

			out.write("</MT>");
			out.write("</ID>");
		}
		catch(Exception e)
		{
			throw new ThesaurusException(e);
		}
	}

}