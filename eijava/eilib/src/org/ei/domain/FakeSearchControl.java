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
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f8014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f7014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f6014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f5914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f1514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f4014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f3014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2f14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2e14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2d14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2c14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2b14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2a14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7f2314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef414536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef314536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef214536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef114536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ef014536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eef14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eee14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eed14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eec14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eeb14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7eea14536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ee914536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ee814536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ee714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ee614536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7ee514536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7fe714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7fe714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7fe714536192163" ,dConfig.getDatabase("grf")));
          testDocs.add(new DocID(i++,"gref_M67d40cfc117a79cabb6M7fe714536192163" ,dConfig.getDatabase("grf")));
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