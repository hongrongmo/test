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
//        testDocs.add(new DocID(i++,"c84_1b6496ff840222940M7dcd19817173212",dConfig.getDatabase("c84")));
testDocs.add(new DocID(i++,"pag_9780123694263_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780125249751_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994589_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607521_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750651349_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607521_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781932266573_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994510_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607521_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994510_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678131_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994565_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440408_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123694768_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994565_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750652131_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750652131_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440408_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994589_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123694768_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750652131_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440668_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440668_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123695123_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440668_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678193_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080440668_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705020_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678285_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080441016_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994855_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080441016_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678285_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607668_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994596_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678285_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705211_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678285_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994596_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994596_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678322_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994596_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750653213_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705211_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994596_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750653213_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750658003_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705327_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607682_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678520_656" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705976_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444628_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607903_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444635_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558607903_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444635_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750656924_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657150_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080446998_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678568_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705976_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657150_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657310_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123705976_756" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994701_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444635_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657150_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994701_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678612_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123724984_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_656" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781558608023_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750678612_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080446912_656" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123724984_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123724984_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657310_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781928994770_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444772_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444772_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780080444772_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123797773_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123797773_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750657310_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750665544_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582944_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836661_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582944_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120586615_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120586615_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127150512_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836661_856" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836661_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490214_656" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582951_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750665544_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582951_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490214_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490276_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780444519993_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490214_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490214_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750665544_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127425030_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582951_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120594757_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582975_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582975_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666336_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490276_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127639529_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666336_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127639529_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127639529_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666336_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490306_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127639529_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490313_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666565_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555582975_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490313_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127745756_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120781423_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666565_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780123188601_756" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120883813_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780127745756_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780444502155_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836708_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490320_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490320_356" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836708_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120883813_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490320_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666633_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836708_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490320_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780444506993_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836746_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583026_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583026_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780750666633_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_856" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781931836708_456" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780444506993_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490351_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490351_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_156" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583019_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_756" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9780120884056_556" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781597490368_56" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583026_256" ,dConfig.getDatabase("pag")));
testDocs.add(new DocID(i++,"pag_9781555583026_356" ,dConfig.getDatabase("pag")));
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