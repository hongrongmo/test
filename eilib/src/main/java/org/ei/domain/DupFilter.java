package org.ei.domain;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.ei.exception.EVBaseException;

//////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This class filters docID lists against a bitset and database name to remove dups
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////////////

public class DupFilter {

    private SearchControl sc;
    private Query query;

    public DupFilter(SearchControl inSc, Query inQuery) {
        super();
        this.sc = inSc;
        this.query = inQuery;
    }

    public List<DocID> getDocIDRange(int startIndex, int endIndex) throws EVBaseException {
        List<DocID> filteredList = new ArrayList<DocID>();
        List<DocID> rawList = sc.getDocIDRange(startIndex, endIndex);
        if ((query.isDeDup()) && (query.getDupSet() != null)) {
            String dbToSkip = query.getDeDupDB();
            Iterator<DocID> itr = rawList.listIterator();
            int i = 0;
            while (itr.hasNext()) {
                int out_i = i + startIndex;
                i++;
                DocID curId = (DocID) itr.next();
                int docDB = ((Database) curId.getDatabase()).getMask();
                if ((!((BitSet) query.getDupSet()).get(out_i)) || (!dbToSkip.equals(Integer.toString(docDB)))) {
                    filteredList.add(curId);
                }
            }
            return filteredList;
        }
        return rawList;
    }
}