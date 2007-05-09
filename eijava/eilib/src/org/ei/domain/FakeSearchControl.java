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

			System.out.println(" fastSearchString" + fastSearchString);

			{
        testDocs.add(new DocID(i++,"c84_1b6496ff840222940M7dcd19817173212",dConfig.getDatabase("c84")));
        testDocs.add(new DocID(i++,"pag_0080426824_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080426832_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080426956_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080426964_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080426999a_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080426999b_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080427022_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080429998_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430090_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080430104_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080437125_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080438644_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080439217_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080439381_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080439500a_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080439594_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080440290_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080440649_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080441041_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080441068_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080441297_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080441637_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0080442730_56",dConfig.getDatabase("pag")));
        testDocs.add(new DocID(i++,"pag_0120451433_56",dConfig.getDatabase("pag")));
			}
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