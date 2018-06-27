package org.ei.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;

public class FakeSearchControl extends FastSearchControl {
    public FakeSearchControl() {

    }

    public SearchControl newInstance(Database d) {
        return new FakeSearchControl();
    }

    protected ArrayList<DocID> search(int offset, int pSize) throws SearchException  {
        ArrayList<DocID> testDocs = new ArrayList<DocID>();

        System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
        System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
        System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
        System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");

        try {
            DatabaseConfig dConfig = DatabaseConfig.getInstance();
            if (query.getSortOption() != null) {
                System.out.println("Setting sort option to 1");
            }

            int i = 1;

            File mids = new File("c:\\mids.txt");
            if (mids.exists()) {
                BufferedReader rdr = new BufferedReader(new FileReader(mids)); // new InputStreamReader(getResourceAsStream("georefids.txt")));
                try {
                    if (rdr != null) {
                        while (rdr.ready()) {
                            String aline = rdr.readLine();
                            String dbprefix = aline.substring(0, aline.indexOf("_"));
                            testDocs.add(new DocID(i++, aline, dConfig.getDatabase(dbprefix)));
                        }
                    }
                } catch (IOException e) {
                } finally {
                    try {
                        rdr.close();
                    } catch (IOException e) {
                    }
                }
                mids = null;
            }

            /*
             * else if(fastSearchString.indexOf("page") >-1 ) { testDocs.add(new DocID(i++,"pag_0080430090_11",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_12",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_13",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_14",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_15",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_16",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_21",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_31",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_41",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_51",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_61",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_71",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_81",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_91",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_92",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_93",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_94",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_95",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_96",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_97",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_98",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_99",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_101",dConfig.getDatabase("pag"))); testDocs.add(new DocID(i++,"pag_0080430090_102",dConfig.getDatabase("pag")));
             * testDocs.add(new DocID(i++,"pag_0080430090_110",dConfig.getDatabase("pag"))); testDocs.add(new
             * DocID(i++,"pag_0080430090_56",dConfig.getDatabase("pag"))); }
             */
            searchResult.setHitCount(testDocs.size());
            searchResult.setResponseTime(1000);

        } catch (Exception e) {
            throw new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, e);
        }

        return testDocs;

    }

}
