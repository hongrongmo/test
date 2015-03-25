package org.ei.data.upt.runtime;

import java.util.ArrayList;
import java.util.List;

import org.ei.data.inspec.runtime.InspecWrapper;
import org.ei.domain.Citation;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.PageEntry;
import org.ei.domain.PageEntryBuilder;

public class RefPager {
    private List<?> docIDs;
    private int pageSize;
    private int docSize;
    private PatRefQuery query;
    private String sessionID;

    public RefPager(List<?> docIDs, int pageSize, PatRefQuery query, String sessionID) {
        this.pageSize = pageSize;
        this.docIDs = docIDs;
        this.query = query;
        this.sessionID = sessionID;
        this.docSize = docIDs.size();
    }

    public RefPager(int pageSize, int setSize, PatRefQuery query) {
        this.pageSize = pageSize;
        this.docSize = setSize;
        this.query = query;
    }

    public int getSetSize() {
        return docSize;
    }

    public String getQuery() {
        return query.getQuery();
    }

    public RefPage getPage(int offset, List<?> builtDocs) throws Exception {

        int endIndex = 0;
        if (builtDocs.size() > offset + pageSize) {
            endIndex = offset + pageSize;
        } else {
            endIndex = builtDocs.size();
        }
        List<?> range = builtDocs.subList(offset, endIndex);
        System.out.println("start= " + offset + " end= " + endIndex);

        // Build the pageEntries
        PageEntryBuilder peBuilder = new PageEntryBuilder(this.sessionID);
        List<?> page = peBuilder.buildPageEntryList(range);
        RefPage rp = new RefPage(page, offset);
        return rp;
    }

    public RefPage getPage(int offset) throws Exception {
        ArrayList<DocID> range = new ArrayList<DocID>();
        int count = 0;
        for (int i = offset; i < docIDs.size(); i++) {
            PatWrapper w = (PatWrapper) docIDs.get(i);
            DocID docID = w.did;
            range.add(docID);
            count++;
            if (count == pageSize) {
                break;
            }
        }

        // Build the docs
        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
        List<?> builtDocs = builder.buildPage(range, Citation.CITATION_FORMAT);

        // Build the pageEntries
        PageEntryBuilder peBuilder = new PageEntryBuilder(this.sessionID);
        List<PageEntry> page = peBuilder.buildPageEntryList(builtDocs);
        RefPage rp = new RefPage(page, offset);
        return rp;
    }

    public List<DocID> getPageIDs(int startRange, int endRange, int resultscount) throws Exception {

        ArrayList<DocID> range = new ArrayList<DocID>();
        int count = 0;
        if (startRange != 0) {
            startRange--;
        }
        for (int i = startRange; i < endRange; i++) {
            DocID docID = null;
            if (docIDs.get(i) instanceof InspecWrapper) {
                EIDoc eid = ((InspecWrapper) docIDs.get(i)).eiDoc;
                docID = eid.getDocID();
                // System.out.println("DOCID-INS= "+docID.getDocID());
            } else {
                PatWrapper w = (PatWrapper) docIDs.get(i);
                docID = w.did;
                // System.out.println("DOCID-PAT= "+docID.getDocID());
            }

            range.add(docID);
            count++;
            if (count == resultscount) {
                break;
            }

        }

        return range;

    }

}
