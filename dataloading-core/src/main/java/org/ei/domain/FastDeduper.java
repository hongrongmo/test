package org.ei.domain;

import java.io.*;
import java.util.*;

public class FastDeduper {

    private DedupData wanted;
    private DedupData unwanted;
    private DatabaseConfig dconfig = DatabaseConfig.getInstance();

    public DedupData getWanted() {
        return this.wanted;
    }

    public DedupData getUnwanted() {
        return this.unwanted;
    }

    public FastDeduper(BufferedReader in, String fieldPref, String dbPref) throws Exception {
        Hashtable<String, DocID> doiTable = new Hashtable<String, DocID>();
        Hashtable<String, DocID> dupkeyTable = new Hashtable<String, DocID>();

        wanted = new DedupData();
        unwanted = new DedupData();

        DocID docID = null;
        while ((docID = readDocID(in)) != null) {
            String dupkey = docID.getDedupKey();
            String doi = docID.getDoi();

            if (doi != null && doiTable.containsKey(doi)) {
                DocID oldID = (DocID) doiTable.get(doi);
                int logicTest = dupLogic(docID, oldID, fieldPref, dbPref);
                if (logicTest == 1) {
                    if (dupkey != null) {
                        dupkeyTable.put(dupkey, docID);
                    }

                    doiTable.put(doi, docID);
                    addDups(docID, oldID, logicTest);
                    wanted.replace(oldID, docID);
                    unwanted.add(oldID);
                } else if (logicTest == -1) {
                    addDups(docID, oldID, logicTest);
                    unwanted.add(docID);
                } else if (logicTest == 0) {
                    wanted.add(docID);
                }
            } else if (dupkey != null && dupkeyTable.containsKey(dupkey)) {
                DocID oldID = (DocID) dupkeyTable.get(dupkey);
                int logicTest = dupLogic(docID, oldID, fieldPref, dbPref);

                if (logicTest == 1) {
                    if (doi != null) {
                        doiTable.put(doi, docID);
                    }

                    addDups(docID, oldID, logicTest);
                    dupkeyTable.put(dupkey, docID);
                    wanted.replace(oldID, docID);
                    unwanted.add(oldID);
                } else if (logicTest == -1) {
                    addDups(docID, oldID, logicTest);
                    unwanted.add(docID);
                } else if (logicTest == 0) {
                    wanted.add(docID);
                }
            } else {
                wanted.add(docID);
                if (doi != null) {
                    doiTable.put(doi, docID);
                }

                if (dupkey != null) {
                    dupkeyTable.put(dupkey, docID);
                }
            }
        }
    }

    private int dupLogic(DocID docID1, DocID docID2, String fieldPref, String dedupDB) {

        int intDmask1 = docID1.getDmask();
        int intDmask2 = docID2.getDmask();
        String db1 = docID1.getDatabase().getID();
        String db2 = docID2.getDatabase().getID();

        if (db1.equalsIgnoreCase(db2)) {
            return 0;
        } else {
            int intDedupOption = 0;

            if (fieldPref != null) {
                intDedupOption = Integer.parseInt(fieldPref);
            }

            if (intDedupOption == 0
                || intDmask1 == intDmask2
                || (intDmask1 == 0 && ((intDmask2 & intDedupOption) != intDedupOption))
                || (intDmask2 == 0 && ((intDmask1 & intDedupOption) != intDedupOption))
                || ((intDmask1 != 0 && ((intDmask1 & intDedupOption) != intDedupOption)) && (intDmask2 != 0 && ((intDmask2 & intDedupOption) != intDedupOption)))) {
                // check database preferences
                if (db1.equalsIgnoreCase(dedupDB)) {
                    return 1;
                } else if (db2.equalsIgnoreCase(dedupDB)) {
                    return -1;
                } else {
                    return -1; // If neither match the DB condition return the first doc.
                }
            } else if ((intDmask1 != 0 && ((intDmask1 & intDedupOption) == intDedupOption))
                && (intDmask2 != 0 && ((intDmask2 & intDedupOption) == intDedupOption))) {
                if (db1.equalsIgnoreCase(dedupDB)) {
                    return 1;
                } else if (db2.equalsIgnoreCase(dedupDB)) {
                    return -1;
                } else {
                    return -1;
                }
            } else if (intDmask1 != 0 && ((intDmask1 & intDedupOption) == intDedupOption)) {
                return 1;
            } else if (intDmask2 != 0 && ((intDmask2 & intDedupOption) == intDedupOption)) {
                return -1;
            }
        }

        return 1;
    }

    public DocID readDocID(BufferedReader in) throws IOException {
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.indexOf("#eidocid") == 0) {
                String dedupKey = in.readLine();
                String database = in.readLine();
                in.readLine(); // Skip
                String dmask = in.readLine();
                String doi = in.readLine();
                return parseDocID(line, database, dedupKey, doi, dmask);
            }
        }

        return null;
    }

    private void addDups(DocID newID, DocID oldID, int logicTest) {
        newID.setIsDup(true);
        oldID.setIsDup(true);
        String dupIds = oldID.getDupIds();

        // set dupId for newID, oldID
        if (dupIds != null && dupIds.length() > 0) {
            newID.setDupIds(dupIds + ":" + oldID.getDocID());
            oldID.setDupIds(dupIds + ":" + newID.getDocID());
        } else {
            newID.setDupIds(oldID.getDocID());
            oldID.setDupIds(newID.getDocID());
        }

        // set dupId for oldID's dup objs
        List<DocID> oldDupObjs = oldID.getDupObjs();
        Iterator<DocID> it = oldDupObjs.iterator();
        while (it.hasNext()) {
            DocID oldDup = (DocID) it.next();
            String oldDupIds = oldDup.getDupIds();
            if (oldDupIds != null && oldDupIds.length() > 0) {
                oldDup.setDupIds(oldDupIds + ":" + newID.getDocID());
            } else {
                oldDup.setDupIds(newID.getDocID());
            }
        }

        if (logicTest == 1) {
            newID.addDupObj(oldID);
        } else if (logicTest == -1) {
            oldID.addDupObj(newID);
        }
    }

    private DocID parseDocID(String docIdLine, String databaseLine, String dedupKeyLine, String doiLine, String dmaskLine) {
        StringTokenizer tokens1 = new StringTokenizer(docIdLine);
        tokens1.nextToken();
        String docID = tokens1.nextToken().trim();

        StringTokenizer tokens2 = new StringTokenizer(dedupKeyLine);
        tokens2.nextToken();
        String dupkey = tokens2.nextToken().trim();

        StringTokenizer tokens3 = new StringTokenizer(databaseLine);
        tokens3.nextToken();
        String database = tokens3.nextToken().trim();

        StringTokenizer tokens4 = new StringTokenizer(doiLine);
        tokens4.nextToken();
        String doi = null;
        if (tokens4.hasMoreTokens()) {
            tokens4.nextToken();
            doi = tokens4.nextToken().trim();
        }

        StringTokenizer tokens5 = new StringTokenizer(dmaskLine);
        tokens5.nextToken();
        int dmaskInt = -1;
        if (tokens5.hasMoreTokens()) {
            String dmask = tokens5.nextToken().trim();
            try {
                dmaskInt = Integer.parseInt(dmask);
            } catch (Exception e) {
                dmaskInt = 0;
            }
        }

        return new DocID(0, docID, dconfig.getDatabase(database.substring(0, 3)), dupkey, doi, dmaskInt);
    }
}
