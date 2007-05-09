package org.ei.thesaurus;

import java.io.Writer;

import org.ei.domain.*;


public class ThesNotesXMLVisitor
    extends MultiRecordXMLVisitor
{

    public ThesNotesXMLVisitor(Writer out)
    {

        super(out);
    }

    public void visitWith(ThesaurusRecord rec)
        throws ThesaurusException
    {
        try
        {
            this.status = rec.getStatus();
            ThesaurusRecordID recID = rec.getRecID();
            recID.accept(this);

            String mainTerm = recID.getMainTerm();
            String scopeNotes = rec.getScopeNotes();
            String historyScopeNotes = rec.getHistoryScopeNotes();
            String dateOfIntro = rec.getDateOfIntro();
            ClassificationID[] cids = rec.getClassificationIDs();

            if(mainTerm != null)
            {
                out.write("<MT>");
                out.write(mainTerm);
                out.write("</MT>");
            }

            if(scopeNotes != null)
            {
                out.write("<SN>");
                out.write(scopeNotes);
                out.write("</SN>");
            }

            if(historyScopeNotes != null)
            {
                out.write("<HSN>");
                out.write(historyScopeNotes);
                out.write("</HSN>");
            }

            if(dateOfIntro != null)
            {
                out.write("<DATE>");
                out.write(dateOfIntro);
                out.write("</DATE>");
            }

            if(cids != null && cids.length > 0)
            {
                out.write("<CPAGE>");
                for(int i=0;i<cids.length;i++)
                {
                    Classification cl = new Classification(cids[i]);

                    /*
                    	This is alot of crap.
                    */
                    Database database = cids[i].getDatabase();
                    DataDictionary dataDictionary = database.getDataDictionary();
                    String classTitle = dataDictionary.getClassCodeTitle(cids[i].getClassCode());
					cl.setClassTitle(classTitle);


                    cl.toXML(out);
                }
                out.write("</CPAGE>");
            }

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }

    }


}
