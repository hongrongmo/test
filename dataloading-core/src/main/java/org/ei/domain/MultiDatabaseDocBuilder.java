package org.ei.domain;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ei.data.bd.runtime.BdDatabase;
import org.ei.data.bd.runtime.BDDocBuilder;

public class MultiDatabaseDocBuilder implements DocumentBuilder {
    public DocumentBuilder newInstance(Database database) {
        return new MultiDatabaseDocBuilder();
    }

    public MultiDatabaseDocBuilder() {
    }

    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> finishedList = new ArrayList<EIDoc>(listOfDocIDs.size());
        BdDatabase bdDatabase = new BdDatabase();
        Hashtable<String, ArrayList<DocID>> listTable = new Hashtable<String, ArrayList<DocID>>();
        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID id = (DocID) listOfDocIDs.get(i);
            Database database = id.getDatabase();
            String databaseID = database.getID();
            if (databaseID.length() > 3) {
                databaseID = databaseID.substring(0, 3);
            }

            if (bdDatabase.isBdDatabase(databaseID)) {
                databaseID = bdDatabase.getID();
            }

            if (listTable.containsKey(databaseID)) {
                ArrayList<DocID> al = (ArrayList<DocID>) listTable.get(databaseID);
                al.add(id);
            } else {
                ArrayList<DocID> al = new ArrayList<DocID>();
                al.add(id);
                listTable.put(databaseID, al);
            }
        }

        DatabaseConfig dConfig = DatabaseConfig.getInstance();

        Enumeration<String> en = listTable.keys();
        Hashtable<String, EIDoc> builtDocsTable = new Hashtable<String, EIDoc>();

        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            ArrayList<DocID> l = (ArrayList<DocID>) listTable.get(key);
            Database database = null;
            DocumentBuilder builder = null;

            if (key.equalsIgnoreCase("bd")) {
                builder = new BDDocBuilder();
            } else {
                database = dConfig.getDatabase(key);
                builder = database.newBuilderInstance(); // ??
            }

            List<?> bList = builder.buildPage(l, dataFormat);

            for (int k = 0; k < bList.size(); ++k) {
                EIDoc doc = (EIDoc) bList.get(k);
                builtDocsTable.put((doc.getDocID()).getDocID(), doc);
            }
        }

        for (int z = 0; z < listOfDocIDs.size(); ++z) {
            DocID dID = (DocID) listOfDocIDs.get(z);
            finishedList.add(builtDocsTable.get(dID.getDocID()));
        }

        return finishedList;
    }

    @Override
    public Key[] getCitationKeys() {
        return null;
    }

    @Override
    public Key[] getAbstractKeys() {
        return null;
    }

    @Override
    public Key[] getDetailedKeys() {
        return null;
    }
}
