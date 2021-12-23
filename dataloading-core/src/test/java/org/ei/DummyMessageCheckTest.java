package org.ei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/***
 * @author TELEBH
 * @Date: 12/20/2021 
 * @Descrption: Test why even after modifying ReceiveAmazonSQSMessages class to download dummy ANI messages with action d
 * still not finding them in SQS archive out file. after testing found out that I did not deplpy updated eilib.jar to the right 
 * path on cafeloading where SQSArxhive messages script refer to. this test showed it worked well and can archive dummy message
 * for ANI deletion
 */

import org.ei.dataloading.cafe.ReceiveAmazonSQSMessage;

public class DummyMessageCheckTest {

	public static final char FIELDDELIM = '\t';
	
	public static void main(String[] args)
	{
		DummyMessageCheckTest obj = new DummyMessageCheckTest();
		String msg = "{ \"bucket\" : \"sccontent-ani-xml-prod\", \"entries\" : [{ \"key\" : \"85119428320\", \"eid\" : \"2-s2.0-85119428320\", \"prefix\" : \"2-s2.0-\", \"load-unit-id\" : \"swd_dD43701021232.dat\", \"xocs-timestamp\" : \"n/a\", \"action\" : \"d\", \"epoch\" : \"1639994856245\", \"version\" : \"n/a\", \"document-type\" : \"dummy\" } ] }";
		
		
		/*msg = "{ \"bucket\" : \"sccontent-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84929990685\", \"eid\" : \"2-s2.0-84929990685\", \"dbcollcodes\" : \"EMBASE|EMBIO|NURSNG|R\r\n"
				+ "EAXYS|SCOPUS|Scopusbase|MEDL\", \"pui\" : \"604108158\", \"prefix\" : \"2-s2.0\", \"sort-year\" : \"2015\", \"epoch\" : \"1639996028297\", \"version\" : \"2021-12-20T10:27:07.159\r\n"
				+ "366Z\", \"document-type\" : \"core\", \"modification\" : \"CONTENT\", \"issn\" : \"19326203\", \"xocs-timestamp\" : \"2021-12-20T10:26:33.40135Z\", \"load-unit-id\" : \"ADDON-FUN\r\n"
				+ "DINGS-2021-12-20-10-26-12.32.xml\", \"action\" : \"u\", \"doi\" : \"10.1371/journal.pone.0124581\" } ] }"; */
		
		
		obj.checkIfDummyMessage(msg);
	}
	public void checkIfDummyMessage(String messageBody) {

		ReceiveAmazonSQSMessage obj = new ReceiveAmazonSQSMessage();
		String bucketName = "";
		String document_type="";
		StringBuffer recordBuf = new StringBuffer();
		
		try(PrintWriter out = new PrintWriter(new File("sqsmessage.txt")))
		{
			if (messageBody.startsWith("{") && messageBody.endsWith("}") && messageBody.contains("bucket")) {
				
				if (obj.ParseSQSMessage(messageBody)) {
					bucketName = obj.getMessageField("bucket");
					if (!(bucketName.isEmpty()) && bucketName.contains("ani")) {
						document_type = "ani";
					} else if (!(bucketName.isEmpty()) && bucketName.contains("apr")) {
						document_type = "apr";
					} else if (!(bucketName.isEmpty()) && bucketName.contains("ipr")) {
						document_type = "ipr";
					}

					// Key
					if (obj.getMessageField("key") != null) {
						recordBuf.append(obj.getMessageField("key"));
					}
					recordBuf.append(FIELDDELIM);

					// epoch
					if (obj.getMessageField("epoch") != null) {
						recordBuf.append(obj.getMessageField("epoch"));
					}
					recordBuf.append(FIELDDELIM);

					// pui
					if (obj.getMessageField("pui") != null) {
						recordBuf.append(obj.getMessageField("pui"));
					}
					recordBuf.append(FIELDDELIM);

					// action
					if (obj.getMessageField("action") != null) {
						recordBuf.append(obj.getMessageField("action"));
					}
					recordBuf.append(FIELDDELIM);

					// Bucket Name
					if (obj.getMessageField("bucket") != null) {
						recordBuf.append(obj.getMessageField("bucket"));
					}
					recordBuf.append(FIELDDELIM);

					// Document_type

					if (!(document_type.isEmpty())) {
						recordBuf.append(document_type);
					}
					recordBuf.append(FIELDDELIM);

				

					// Body
					recordBuf.append(messageBody);
					recordBuf.append(FIELDDELIM);

					
					// write the message to out file
					out.println(recordBuf.toString().trim());
				}
			}
		}
			
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
}
