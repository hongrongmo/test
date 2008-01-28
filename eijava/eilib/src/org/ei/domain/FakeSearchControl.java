package org.ei.domain;

import java.util.ArrayList;

public class FakeSearchControl
	extends FastSearchControl
{
	public FakeSearchControl() {

	}

	public SearchControl newInstance(Database d)
	{
		return new FakeSearchControl();
	}


	protected ArrayList search(int offset,
							 int pSize)
			throws SearchException
	{
		ArrayList testDocs = new ArrayList();

    System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
    System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
    System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");
    System.out.println(" FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE SEARCH FAKE");


		try
		{

			DatabaseConfig dConfig = DatabaseConfig.getInstance();
			String fastSearchString = query.getSearchQuery();
			int intSortOption = 0;
			if(query.getSortOption() != null)
			{
				System.out.println("Setting sort option to 1");
				intSortOption = 1;
			}

			int i = 1;
			{
          testDocs.add(new DocID(i++,"gref_1d4755d7117c220db66M745014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M7e9e911c117c21f8a93M7fdd14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M7e9e911c117c21f8a93M7fdd14536192163" ,dConfig.getDatabase("grf")));

			}
			System.out.println(" result Count " + testDocs.size());

/*			else if(fastSearchString.indexOf("page") >-1 )
			{
        testDocs.add(new DocID(i++,"pag_0080430090_11",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_12",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_13",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_14",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_15",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_16",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_21",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_31",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_41",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_51",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_61",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_71",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_81",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_91",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_92",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_93",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_94",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_95",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_96",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_97",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_98",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_99",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_101",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_102",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_110",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_56",dConfig.getDatabase("pag")));
      }
*/
			searchResult.setHitCount(testDocs.size());
			searchResult.setResponseTime(1000);

		}
		catch(Exception e)
		{
			throw new SearchException(e);
		}

		return testDocs;

	}

}











































































































































































































































