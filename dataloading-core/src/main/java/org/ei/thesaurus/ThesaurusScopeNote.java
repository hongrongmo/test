package org.ei.thesaurus;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.validator.GenericValidator;
import org.ei.domain.Classification;
import org.ei.domain.ClassificationID;
import org.ei.domain.DataDictionary;
import org.ei.domain.Database;

public class ThesaurusScopeNote {

    private String status;
    private String searchInfo;
    private String mainTerm;
    private String scopeNotes;
    private String historyScopeNotes;
    private String dateOfIntro;
    private String coordinates;
    private List<Double> coords;
    private List<Classification> classifications;
    private String type = null;
    private String latitude1 = null;
    private String latitude2 = null;
    private String longitude1 = null;
    private String longitude2 = null;
    private boolean drawmap = false;
    
    public static ThesaurusScopeNote build(ThesaurusRecord rec) throws ThesaurusException {
        ThesaurusScopeNote scopenote = new ThesaurusScopeNote();
        try {
            // Parse info from main thes record
            ThesaurusRecordID recID = rec.getRecID();
            scopenote.status = rec.getStatus();
            scopenote.searchInfo = rec.getSearchInfo();
            scopenote.mainTerm = recID.getMainTerm();
            scopenote.scopeNotes = rec.getScopeNotes();
            scopenote.historyScopeNotes = rec.getHistoryScopeNotes();
            scopenote.dateOfIntro = rec.getDateOfIntro();
            if(!GenericValidator.isBlankOrNull(rec.getType())) {
                scopenote.type = rec.getTranslatedType();
            }
            
            // Special EPT processing
            scopenote.scopeNotes = processEptScopeNote(scopenote.scopeNotes);

            // Parse coordinates if present
            scopenote.coordinates = rec.getCoordinates();
            if(!GenericValidator.isBlankOrNull(scopenote.coordinates)) {
                scopenote.coords = new ArrayList<Double>();
                StringTokenizer stk = new StringTokenizer(scopenote.coordinates,"NEWS");
                int coordCount = 1;
                while(stk.hasMoreTokens()) {
                    String tk = stk.nextToken();
                    double coord = Double.parseDouble(tk.substring(0,tk.length()-4) + "." + tk.substring(tk.length()-4,tk.length()));
                    if((scopenote.coordinates.indexOf("S0") != -1 || scopenote.coordinates.indexOf("S1") != -1) && coordCount < 3)
                    {
                        coord = coord * -1;
                    }
                    if((scopenote.coordinates.indexOf("W0") != -1 || scopenote.coordinates.indexOf("W1") != -1) && coordCount >= 3)
                    {
                        coord = coord * -1;
                    }
                    coordCount++;
                    // Add coord
                    scopenote.coords.add(coord);
                }
                if(scopenote.coordinates.indexOf("E") != -1 && scopenote.coordinates.indexOf("W") == -1) {
                    scopenote.drawmap = true;
                }
                if(scopenote.coordinates.indexOf("W") != -1 && scopenote.coordinates.indexOf("E") == -1) {
                    scopenote.drawmap = true;
                }


            }

            // Build classifications
            ClassificationID[] cids = rec.getClassificationIDs();
            if(cids != null && cids.length > 0) {
                scopenote.classifications = new ArrayList<Classification>();
                for(int i=0;i<cids.length;i++) {
                    Classification cl = new Classification(cids[i]);

                    /*
                        This is alot of crap.
                    */
                    Database database = cids[i].getDatabase();
                    DataDictionary dataDictionary = database.getDataDictionary();
                    String classTitle = dataDictionary.getClassCodeTitle(cids[i].getClassCode());
                    cl.setClassTitle(classTitle);
                    scopenote.classifications.add(cl);
                }
            }

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }

        return scopenote;
    }

    public static String processEptScopeNote(String scopenote) {
        if (GenericValidator.isBlankOrNull(scopenote)) {
            return scopenote;
        }
        
        StringBuffer snbuffer = new StringBuffer();
        int appendixpos = scopenote.indexOf("Appendix");
        if (appendixpos > -1) {
            // Special 'Appendix' value found, use it link to static Appendix. 
            // NOTE that if found, it will always be 'Appendix X' where 'X' is B through G
            snbuffer.append(scopenote.substring(0, appendixpos));
            String appendix = scopenote.substring(appendixpos, appendixpos + 10);
            if ("Appendix B".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appendb2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            } else if ("Appendix C".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appendc2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            } else if ("Appendix D".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appendd2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            } else if ("Appendix E".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appende2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            } else if ("Appendix F".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appendf2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            } else if ("Appendix G".equals(appendix)) {
                snbuffer.append("<a href=\"/static/help/encompass/Appendg2013.pdf\" target=\"_blank\">");
                snbuffer.append(appendix);
                snbuffer.append("</a>");
            }
            snbuffer.append(scopenote.substring(appendixpos + 10, scopenote.length()));
            return snbuffer.toString();
        } else {
            return scopenote;
        }
    }

    //
    // GETTERS/SETTERS
    //
    public String getStatus() {
        return this.status;
    }

    public String getSearchInfo() {
        return this.searchInfo;
    }

    public String getMainTerm() {
        return this.mainTerm;
    }

    public String getScopeNotes() {
        return this.scopeNotes;
    }

    public String getHistoryScopeNotes() {
        return this.historyScopeNotes;
    }

    public String getDateOfIntro() {
        return this.dateOfIntro;
    }

    public String getCoordinates() {
        return this.coordinates;
    }

    public List<Double> getCoords() {
        return this.coords;
    }

    public List<Classification> getClassifications() {
        return this.classifications;
    }

    public String getType() {
        return this.type;
    }

    public String getLatitude1() {
        return this.latitude1;
    }

    public String getLatitude2() {
        return this.latitude2;
    }

    public String getLongitude1() {
        return this.longitude1;
    }

    public String getLongitude2() {
        return this.longitude2;
    }

    public boolean isDrawmap() {
        return this.drawmap;
    }
}
