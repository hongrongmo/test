package org.ei.domain;

import java.util.*;

public class DedupData {
    private LinkedHashMap<String, DocID> docIDs = new LinkedHashMap<String, DocID>();
    private HashMap<String, Map<String, DocID>> databases = new HashMap<String, Map<String, DocID>>();

    public void add(DocID docID) {
        String database = getDatabase(docID.getDatabase());
        if (databases.containsKey(database)) {
            Map<String, DocID> m = (Map<String, DocID>) databases.get(database);
            m.put(docID.getDocID(), docID);
        } else {
            LinkedHashMap<String, DocID> m = new LinkedHashMap<String, DocID>();
            m.put(docID.getDocID(), docID);
            databases.put(database, m);
        }

        this.docIDs.put(docID.getDocID(), docID);
    }

    private String getDatabase(Database database) {
        if (database.isBackfile()) {
            if (database.getID().equals("c84")) {
                return "cpx";
            } else {
                return "ins";
            }

        }

        return database.getID();
    }

    public void replace(DocID oldDocID, DocID newDocID) {
        docIDs.remove(oldDocID.getDocID());
        docIDs.put(newDocID.getDocID(), newDocID);

        String oldDatabase = getDatabase(oldDocID.getDatabase());
        Map<?, ?> oldDatabaseMap = (Map<?, ?>) databases.get(oldDatabase);
        oldDatabaseMap.remove(oldDocID.getDocID());

        String newDatabaseID = getDatabase(newDocID.getDatabase());

        if (databases.containsKey(newDatabaseID)) {
            Map<String, DocID> m = (Map<String, DocID>) databases.get(newDatabaseID);
            m.put(newDocID.getDocID(), newDocID);
        } else {
            Map<String, DocID> m = new LinkedHashMap<String, DocID>();
            m.put(newDocID.getDocID(), newDocID);
            databases.put(newDatabaseID, m);
        }
    }

    public List<DocID> getDocIDs() {
        return fillList(docIDs.values());
    }

    public List<DocID> getDocIDs(String database) {
        Map<?, DocID> ids = (Map<?, DocID>) databases.get(database);
        return fillList(ids.values());
    }

    public Map<String, Map<String, DocID>> getDatabases() {
        return this.databases;
    }

    private List<DocID> fillList(Collection<DocID> col) {
        ArrayList<DocID> list = new ArrayList<DocID>();
        Iterator<DocID> it = col.iterator();
        int index = 1;
        while (it.hasNext()) {
            DocID docID = (DocID) it.next();
            docID.setHitIndex(index);
            list.add(docID);
            index++;
        }

        return list;
    }
}