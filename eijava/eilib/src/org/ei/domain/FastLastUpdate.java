package org.ei.domain;

import java.util.ArrayList;
import java.util.List;

public class FastLastUpdate
{
    public int getLastUpdate(Database db)
        throws SearchException
    {
        int maxWeek = -1;
        try
        {
            FastClient client = new FastClient();
            client.setBaseURL(FastSearchControl.BASE_URL);
            client.setQueryString("DB:"+db.getID());
            client.setOffSet(0);
            client.setPageSize(1);
            client.setDoClustering(false);
                        client.setDoNavigators(false);

            client.setResultView("ei");
            client.setPrimarySort("wk");
            client.setPrimarySortDirection("-");
            client.search();
            List ds = client.getDocIDs();
            for(int i=0;i<ds.size();i++)
            {
                String[] fields = (String[])ds.get(i);
                DocID docID = new DocID(1, fields[0], db);
                ArrayList list = new ArrayList();
                list.add(docID);
                DocumentBuilder builder = db.newBuilderInstance();
                List docList = builder.buildPage(list, Citation.CITATION_FORMAT);
                EIDoc eidoc = (EIDoc)docList.get(0);
                maxWeek = eidoc.getLoadNumber();
                break;
            }

        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        return maxWeek;
    }
}