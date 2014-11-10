package org.ei.domain;

import java.util.ArrayList;
import java.util.List;

import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;

public class FastLastUpdate {
    public int getLastUpdate(Database db) throws SearchException {
        int maxWeek = -1;
        try {
            FastClient client = new FastClient();
            client.setBaseURL(FastSearchControl.BASE_URL);
            client.setQueryString("DB:" + db.getID());
            client.setOffSet(0);
            client.setPageSize(1);
            client.setDoClustering(false);
            client.setDoNavigators(false);

            client.setResultView("ei");
            client.setPrimarySort("wk");
            client.setPrimarySortDirection("-");
            client.search();
            List<String[]> ds = client.getDocIDs();
            for (int i = 0; i < ds.size(); ) {
                String[] fields = (String[]) ds.get(i);
                DocID docID = new DocID(1, fields[0], db);
                ArrayList<DocID> list = new ArrayList<DocID>();
                list.add(docID);
                DocumentBuilder builder = db.newBuilderInstance();
                List<?> docList = builder.buildPage(list, Citation.CITATION_FORMAT);
                EIDoc eidoc = (EIDoc) docList.get(0);
                maxWeek = eidoc.getLoadNumber();
                break;
            }

        } catch (Exception e) {
            throw new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, "Unable to get last update!", e);
        }

        return maxWeek;
    }
}