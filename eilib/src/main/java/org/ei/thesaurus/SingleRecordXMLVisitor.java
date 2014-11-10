package org.ei.thesaurus;

import java.io.Writer;

public class SingleRecordXMLVisitor
	extends MultiRecordXMLVisitor
{

	public SingleRecordXMLVisitor(Writer out)
	{

		super(out);
	}

	public void visitWith(ThesaurusRecord rec)
		throws ThesaurusException
	{
		try
		{
			out.write("<TREC INFO=\"");
			out.write((new Boolean(rec.hasInfo())).toString());
			out.write("\">");

			this.status = rec.getStatus();
			ThesaurusRecordID recID = rec.getRecID();
			recID.accept(this);

			MultiRecordXMLVisitor mv = new MultiRecordXMLVisitor(out);

			ThesaurusPage ntpage = rec.getNarrowerTerms();
			if(ntpage.size() > 0)
			{
				out.write("<NT>");
				ntpage.accept(mv);
				out.write("</NT>");
			}


			ThesaurusPage ptpage = rec.getPriorTerms();
			if(ptpage.size() > 0)
			{
				out.write("<PT>");
				ptpage.accept(mv);
				out.write("</PT>");
			}


			ThesaurusPage ttpage = rec.getTopTerms();
			if(ttpage.size() > 0)
			{
				out.write("<TT>");
				ttpage.accept(mv);
				out.write("</TT>");
			}


			ThesaurusPage ltpage = rec.getLeadinTerms();
			if(ltpage.size() > 0)
			{
				out.write("<LT>");
				ltpage.accept(mv);
				out.write("</LT>");
			}


			ThesaurusPage rtpage = rec.getRelatedTerms();
			if(rtpage.size() > 0)
			{
				out.write("<RT>");
				rtpage.accept(mv);
				out.write("</RT>");
			}

			ThesaurusPage btpage = rec.getBroaderTerms();
			if(btpage.size() > 0)
			{
				out.write("<BT>");
				btpage.accept(mv);
				out.write("</BT>");
			}

			ThesaurusPage utpage = rec.getUseTerms();
			if(utpage.size() > 0)
			{
				out.write("<UT>");
				utpage.accept(mv);
				out.write("</UT>");
			}

			if(this.status != null)
			{
				out.write("<STATUS>" + this.status + "</STATUS>");
			}
			out.write("</TREC>");
		}
		catch(Exception e)
		{
			throw new ThesaurusException(e);
		}

	}


}