package org.ei.stripes.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.thesaurus.ThesaurusPage;
import org.ei.thesaurus.ThesaurusRecord;
import org.ei.thesaurus.ThesaurusRecordBroker;
import org.ei.thesaurus.ThesaurusRecordID;
import org.ei.thesaurus.ThesaurusScopeNote;

@UrlBinding("/thes/scopenote.url")
public class ThesaurusScopeNoteAction extends EVActionBean {
    private final static Logger log4j = Logger.getLogger(ThesaurusScopeNoteAction.class);
    
    private String term;
    private ThesaurusScopeNote scopenote;
    
    @DefaultHandler
    public Resolution display() {
        log4j.info("Starting ThesaurusScopeNotesAction...");
        
        HttpServletRequest request = context.getRequest();
        String term = request.getParameter("term");
        String dbName = request.getParameter("database");
        String databaseID = null;
        
        if ("1".equals(dbName)) {
            databaseID = "cpx";
        }
        else if ("2".equals(dbName)) {
            databaseID = "ins";
        }
        else if ("2048".equals(dbName) || "3072".equals(dbName)) {
            databaseID = "ept";
        }
        else if ("1024".equals(dbName)) {
            databaseID = "elt";
        }
        else if ("8192".equals(dbName)) {
            databaseID = "geo";
        }
        else if ("2097152".equals(dbName)) {
            databaseID = "grf";
        } else {
            throw new IllegalArgumentException("Request attribute 'database' is invalid!");
        }
        
        if (!GenericValidator.isBlankOrNull(term)) {
            term = term.replaceAll("\\*$", "");
        }
        
        ThesaurusRecord trec = null;
        try {
            
            Database database = (DatabaseConfig.getInstance()).getDatabase(databaseID);
            ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
            ThesaurusRecordID recID = new ThesaurusRecordID(term, database);
            ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
            recIDs[0] = recID;
            
            ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);
            trec = tpage.get(0);
            
            // Convert to ScopeNote object
            scopenote = ThesaurusScopeNote.build(trec);
            
        } catch (Exception cpe) {
            cpe.printStackTrace();
        }
        
        return new ForwardResolution("/WEB-INF/pages/customer/search/thesScopeNote.jsp");
    }
    
    public boolean isGmapDisplayable() {
        // Ensure valid coords object
        if (this.scopenote == null || this.scopenote.getCoords() == null || this.scopenote.getCoords().isEmpty() || this.scopenote.getCoords().size() < 4) {
            log4j.warn("No valid coords!");
            return false;
        } 
        
        // Ensure coords do not match
        List<Double> coords = this.scopenote.getCoords();
        if (coords.get(0).equals(coords.get(1)) || coords.get(2).equals(coords.get(3))) {
            log4j.warn("Coordinates match");
            return false;
        }
        return this.scopenote.isDrawmap();
    }

    /**
     * Special getter to return coords as gmap-compatible path
     * @return
     */
    public String getCoordsAsGmapPath() {
        // Ensure valid coords object
        if (!this.isGmapDisplayable()) {
            log4j.warn("Map is not displayable!!");
            return "";
        }
        
        // Check coords are displayable
        List<Double> coords = this.scopenote.getCoords();

        StringBuffer coordsstr = new StringBuffer();
        coordsstr.append(coords.get(1) + "," + coords.get(2) + "|");
        coordsstr.append(coords.get(0) + "," + coords.get(2) + "|");
        coordsstr.append(coords.get(0) + "," + coords.get(3) + "|");
        coordsstr.append(coords.get(1) + "," + coords.get(3) + "|");
        coordsstr.append(coords.get(1) + "," + coords.get(2));
        
        return coordsstr.toString();
    }
    //
    // GETTERS/SETTERS
    //
    
    public String getTerm() {
        return this.term;
    }
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    public ThesaurusScopeNote getScopenote() {
        return this.scopenote;
    }
    
}
