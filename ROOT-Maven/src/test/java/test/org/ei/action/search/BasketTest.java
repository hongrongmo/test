package test.org.ei.action.search;

import junit.framework.Assert;

import org.ei.domain.BasketEntry;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBasket;
import org.ei.domain.DriverConfig;
import org.ei.domain.InvalidArgumentException;
import org.ei.domain.Query;
import org.ei.exception.InfrastructureException;
import org.junit.Test;

import test.org.ei.action.BaseActionTest;

public class BasketTest extends BaseActionTest {
	// 	 *     &lt;basketentry sno=num sessionid=id searchid=id docid=id handle=string database=string query=string count=num/&gt;
	//http://www.engineeringvillage.com/search/doc/abstract.url?&pageType=quickSearch&searchtype=Quick&SEARCHID=M37896083140e2e71f917d17prod4con2&DOCINDEX=1&database=1&format=quickSearchAbstractFormat&tagscope=&displayPagination=yes
	private static final String basketXML = "<basket><entry sno='1' handle='1' sessionid='12345678' searchid='M37896083140e2e71f917d17prod4con2' docid='cpx_6e3d60135a650a68fM7b512061377553' database='cpx' count='1' query='((stress) WN All fields), 1884-2014'/></basket>";
	@Test
	public void TestBasket() throws InfrastructureException, DatabaseConfigException, InvalidArgumentException {
        //
        // Init the DatabaseConfig instance. Just calling the
        // instance() method will initialize for future calls
        //
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());

        // Test basket entries in memcached
		DocumentBasket basket = new DocumentBasket("12345678");

		DocID docID = new DocID("cpx_6e3d60135a650a68fM7b512061377553", databaseConfig.getDatabase("cpx"));
		docID.setHitIndex(1);

		Query query = new Query();
		query.setDisplayQuery("((stress) WN All fields), 1884-2014");
		query.setID("M37896083140e2e71f917d17prod4con2");
		query.setRecordCount("187000");

		BasketEntry basEntry = new BasketEntry();
		basEntry.setDocID(docID);
		basEntry.setQuery(query);
		basket.add(basEntry);

		Assert.assertEquals(basket.getBasketSize(), 1);

		basket.removeAll();
	}

}
