package org.ei.data.ibs.runtime;

import java.util.Hashtable;
import java.util.List;

import org.ei.data.insback.runtime.InsBackDocBuilder;
import org.ei.domain.Database;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;

public class IbsDocBuilder extends InsBackDocBuilder {

    public DocumentBuilder newInstance(Database database) {
        return new IbsDocBuilder(database);
    }

    public IbsDocBuilder() {
    }

    public IbsDocBuilder(Database database) {
        super(database);
    }

    protected String buildINString(List<DocID> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = "ibf_".concat((doc.getDocID().substring(4)));
            if ((k - 1) == 0) {
                sQuery.append("'" + docID + "'");
            } else {
                sQuery.append("'" + docID + "'").append(",");
            }
        }
        sQuery.append(")");
        return sQuery.toString();
    }

    protected Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            String docID = "ibf_".concat((d.getDocID().substring(4)));
            h.put(docID, d);
        }

        return h;
    }
}
