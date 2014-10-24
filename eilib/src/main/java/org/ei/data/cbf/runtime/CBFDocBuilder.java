package org.ei.data.cbf.runtime;

import java.util.*;
import org.ei.domain.*;
import org.ei.data.c84.runtime.*;

public class CBFDocBuilder extends C84DocBuilder {

    private Database database;

    public DocumentBuilder newInstance(Database database) {
        return new CBFDocBuilder(database);
    }

    public CBFDocBuilder() {

    }

    public CBFDocBuilder(Database database) {
        this.database = database;
    }

    protected String buildINString(List<DocID> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = CBFDatabase.C84_PREF.concat(doc.getDocID().substring(4));
            if ((k - 1) == 0) {
                sQuery.append("'").append(docID).append("'");
            } else {
                sQuery.append("'").append(docID).append("'").append(",");
            }
        }
        sQuery.append(")");
        return sQuery.toString();
    }

    protected Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);

            String docID = CBFDatabase.C84_PREF.concat(d.getDocID().substring(4));
            // d.setDocID(docID);
            h.put(docID, d);
        }

        return h;
    }

}

// End Of C84DocBuilder
