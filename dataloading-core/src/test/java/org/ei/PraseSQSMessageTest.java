package org.ei;

import static org.junit.Assert.*;

import org.ei.dataloading.cafe.*;
import org.junit.Test;

public class PraseSQSMessageTest {


	@Test
	public void testParseSQSMessage() {
		
		ReceiveAmazonSQSMessage obj = new ReceiveAmazonSQSMessage();
		
		 String msg = "{ \"bucket\" : \"sccontent-ani-xml-prod\", \"entries\" : [{ \"key\" : \"85119428320\", \"eid\" : \"2-s2.0-85119428320\", "
		 		+ "\"prefix\" : \"2-s2.0-\", \"load-unit-id\" : \"swd_dD43701021232.dat\", \"xocs-timestamp\" : \"n/a\", \"action\" : \"d\", "
		 		+ "\"epoch\" : \"1639994856245\", \"version\" : \"n/a\", \"document-type\" : \"dummy\" } ] }\r\n";
		
		 assertEquals("message is dummy ani deletion",true, obj.ParseSQSMessage(msg));
	}

	
}
