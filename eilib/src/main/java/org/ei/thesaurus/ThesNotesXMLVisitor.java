package org.ei.thesaurus;

import java.io.Writer;
import java.util.StringTokenizer;
import org.ei.domain.*;
import java.net.URLEncoder;

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

            String searchInfo = rec.getSearchInfo();
            String mainTerm = recID.getMainTerm();
            String scopeNotes = rec.getScopeNotes();
            String historyScopeNotes = rec.getHistoryScopeNotes();
            String dateOfIntro = rec.getDateOfIntro();
            String coordinates = rec.getCoordinates();
            String type = null;
            String latitude1 = null;
            String latitude2 = null;
            String longitude1 = null;
            String longitude2 = null;

            if(rec.getType() != null)
            {
              type = rec.getTranslatedType();
		    }
            ClassificationID[] cids = rec.getClassificationIDs();

            if(mainTerm != null)
            {
                out.write("<MT>");
                out.write(URLEncoder.encode(mainTerm));             
                out.write("</MT>");
            }

            if(scopeNotes != null)
            {
                out.write("<SN><![CDATA[");
                out.write(URLEncoder.encode(scopeNotes));
                out.write("]]></SN>");
            }
            
            if(searchInfo != null)
            {
                out.write("<SI><![CDATA[");
                out.write(URLEncoder.encode(searchInfo));
                out.write("]]></SI>");
            }

            if(historyScopeNotes != null)
            {
                out.write("<HSN>");
                out.write(URLEncoder.encode(historyScopeNotes));
                out.write("</HSN>");
            }

            if(dateOfIntro != null)
            {
                out.write("<DATE>");
                out.write(dateOfIntro);
                out.write("</DATE>");
            }

            if(coordinates != null)
            {
                out.write("<COORDINATES>");
                out.write(coordinates);
                out.write("</COORDINATES>");

				StringTokenizer stk = new StringTokenizer(coordinates,"NEWS");
				int coordCount = 1;
				while(stk.hasMoreTokens())
				{
					String tk = stk.nextToken();
					double coord = Double.parseDouble(tk.substring(0,tk.length()-4) + "." + tk.substring(tk.length()-4,tk.length()));
					if((coordinates.indexOf("S0") != -1 || coordinates.indexOf("S1") != -1) && coordCount < 3)
					{
						coord = coord * -1;
					}
					if((coordinates.indexOf("W0") != -1 || coordinates.indexOf("W1") != -1) && coordCount >= 3)
					{
						coord = coord * -1;
					}
					out.write("<COORD" + coordCount + ">" + coord  + "</COORD" + coordCount + ">");
					coordCount++;
				}
                if(coordinates.indexOf("E") != -1 && coordinates.indexOf("W") == -1)
                {
					out.write("<DRAWMAP>TRUE</DRAWMAP>");
				}
                if(coordinates.indexOf("W") != -1 && coordinates.indexOf("E") == -1)
                {
					out.write("<DRAWMAP>TRUE</DRAWMAP>");
				}


            }

            if(type != null)
            {
                out.write("<TYPE>");
                out.write(type);
                out.write("</TYPE>");
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
