package org.ei.thesaurus;

import java.util.List;
import java.util.StringTokenizer;

import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastClient;

public class ThesaurusSearchControl
{

    private int pSize;
    private ThesaurusQuery tquery;
    private ThesaurusSearchResult searchResult;
    public static String BASE_URL;


    public ThesaurusSearchResult search(ThesaurusQuery query,
                                        int pSize)
            throws ThesaurusException
    {
        this.pSize = pSize;
        this.tquery = query;

        searchResult = new ThesaurusSearchResult(this);
        return searchResult;
    }


    public ThesaurusPage pageAt(int docIndex)
                throws ThesaurusException
    {
        int offset = ((getPageIndex(pSize, docIndex)-1) * pSize);
        return search(offset, this.pSize);
    }

    private int getPageIndex(int pSize, int dIndex)
    {
        int pageIndex;

        if ( (dIndex%pSize) == 0 )
        {
            // if the remainder is zero the page index will find in the following way
            pageIndex  = (dIndex/pSize);
        }
        else
        {
                        // otherwise find the page index in the following way
            pageIndex  = (dIndex/pSize) + 1;
        }

        return pageIndex;

    }


    private ThesaurusPage search(int offset,
                                 int pSize)
                throws ThesaurusException
    {

        ThesaurusPage page = null;

        try
        {
            DatabaseConfig dConfig = DatabaseConfig.getInstance();
            String fastSearchString = tquery.getSearchQuery();
            FastClient client = new FastClient();
            client.setBaseURL(BASE_URL);
            fastSearchString = fastSearchString+" (meta.collection:thesaurus)";
            fastSearchString = fastSearchString.replaceAll("AND"," ");
            client.setQueryString(fastSearchString);
            client.setOffSet(offset);
            client.setPageSize(pSize);
            client.setDoClustering(false);
            client.setResultView("asearch");
            client.setDoNavigators(false);
            client.setDoThesaurus(true);
            client.setDoCatCount(false);
            client.setPrimarySort("so");
            client.setPrimarySortDirection("+");
            client.search();
            int docCount = client.getHitCount();
            searchResult.setHitCount(docCount);
            List ds = client.getDocIDs();

            ThesaurusRecordID[] recIDs = new ThesaurusRecordID[pSize];
            int i = -1;
            for(int j=0;j<ds.size();j++)
            {
                String[] fields = (String[])ds.get(j);
                ThesaurusRecordID recID = new ThesaurusRecordID(stripDatabase(fields[0]),
                                                                tquery.getDatabase());
                ++i;
                recIDs[i] = recID;
            }

            ThesaurusRecordBroker broker = new ThesaurusRecordBroker(tquery.getDatabase());
            page = broker.buildPage(recIDs, 0,i);

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }

        return page;
    }

    private int stripDatabase(String s)
    {
        StringTokenizer tokens = new StringTokenizer(s, "_");
        tokens.nextToken();
        return Integer.parseInt(tokens.nextToken());
    }


}