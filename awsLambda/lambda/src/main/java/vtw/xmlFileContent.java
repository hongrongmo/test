package vtw;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import com.amazonaws.util.IOUtils;

import vtw.DigestAuthentication.PatentHttpResponseHandler;


public class xmlFileContent {


	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;


	public static void main(String[] args) throws Exception {

		//URL url = new URL("https://acc-ucs-content-store-eu-west.s3-eu-west-1.amazonaws.com/content/pat%3AEP2818471A1/MAIN/application/xml/c86165c74cde6360867c5686d628a7be/EP2818471A1.xml?AWSAccessKeyId=AKIAJHXPHCI7MP3PAWLA&Expires=1476109395&Signature=htM5sZO9TFwCnesUpuJdBu6mXoc%3D");

		//String url = "https://acc.vtw.elsevier.com/asset/pat/EP2819320A1?fmt=application%2Fxml&type=MAIN&ver=cf86d1f8893066741448fdb57ed98450";

		//String url = "https://acc.vtw.elsevier.com/asset/pat/EP1412624A2?fmt=application%2Fxml&type=MAIN&ver=68084274ea640e71e28923303531544d";
		//String url = "https://acc.vtw.elsevier.com/asset/pat/US2016009149A?fmt=application%2Fxml&type=MAIN&ver=78f2b50aab56b294499c2f6a297fad7e";  // with AB having special letters 
		//String url = "https://s3.amazonaws.com/datafabrication-reports/vtw/US20050168491A1.xml";
		String url = "https://acc.vtw.elsevier.com/asset/pat/EP1852232A4?fmt=application%2Fxml&type=MAIN&ver=b3ae4d558bb566e3fa81262c6dddb817";
		
			
		URLConnection con;
		try {


			client = (CloseableHttpClient) DigestAuthentication.getInstance();
			HttpGet request = generateRequest(url);		
			sendRequest(request);

		}
		/*catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


	// Set up the request
	private static HttpGet generateRequest(String url) {

		HttpGet request = null;

		try {

			request = new HttpGet(url);

		} catch (Exception e) {

			System.out.println("Patent URL Request exception!");
			System.out.println(e.getMessage());
			System.out.println(e.hashCode());

			e.printStackTrace();
		}  

		return request;
	}	

	/// Send the request to the server
	private static void sendRequest(HttpGet request) {

		try {

			// using ResponseHandler class below
			responseHandler =  new MyHttpResponseHandler<String>();

			String response = client.execute(request,responseHandler);
			System.out.println("Response is:  " +  response);


		} catch (ClientProtocolException e) {
			System.out.println("clientProtocolException happened!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Cause: " + e.getCause());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException happened!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Cause: " + e.getCause());
			e.printStackTrace();
			e.printStackTrace();
		}


	}

	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {


		//@Override
		//public CloseableHttpResponse handleResponse(HttpResponse response)
		public String handleResponse(HttpResponse response)
		{

			InputStream responseStream = null;
			String responseString = "";
			OutputStream out = null;

			try
			{
				File downloadDir = new File(System.getProperty("user.dir") + "/zips");
				if(!downloadDir.exists())
				{
					downloadDir.mkdir();
				}

				downloadDir = new File(downloadDir.getAbsolutePath() + "/" + Integer.toString(20164301));
				if(!(downloadDir.exists()))
				{
					downloadDir.mkdir();
				}

				//response.addHeader("Content-Encoding", "UTF-8");
				Header[] headers = response.getAllHeaders();
				for(int i=0;i<headers.length;i++)
				{
					System.out.println("Header: " + headers[i] + " " + headers[i].getName() + " " + headers[i].getValue()); 
				}
				responseStream = response.getEntity().getContent();

				//String str = EntityUtils.toString(response.getEntity(), "UTF-8");

				//responseStream = response.getEntity().getContent();
				//responseStream = new ByteArrayInputStream(str.getBytes());
				//responseStream = new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()));

				//responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

				out = new FileOutputStream(new File(downloadDir.getAbsolutePath() + "/" + "EP1852232A4.xml"));
				/*IOUtils.copy(responseStream, out);*/

				//System.out.println(responseString);
				System.out.println("Respons Code: " + response.getStatusLine().getStatusCode());

				/**
				 * This class is used to wrap a stream that includes an encoded ByteOrderMark as its first bytes. 
				 * This class detects these bytes and, if required, can automatically skip them and return the subsequent byte as the first byte in the stream. 
				 */
				//BOMInputStream  bomStream = new BOMInputStream(responseStream,false);
				BOMInputStream  bomStream = new BOMInputStream(responseStream,false,
						ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
						ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
				//int firstNonBOMByte  = bomStream.read();
				ByteOrderMark bom = bomStream.getBOM();
				String charSet = bom ==null ? "UTF-8" : bom.getCharsetName();

				if(bomStream.hasBOM())
				{
					System.out.println("XML has a UTF-8 BOM");
					System.out.println("BOM value is : " + bom.getCharsetName());
					System.out.println("detailes about BOM: " +  bom.getBytes().toString() + bom);
					System.out.println("detailes about BOM: " +  Integer.toString(bom.getBytes().length));
				}
				else if (bomStream.hasBOM(ByteOrderMark.UTF_16BE)) {
					// 
					System.out.println("XML has a UTF-16BE BOM");
					System.out.println("XML has a UTF-16LE BOM");
					System.out.println("BOM value is : " + bom.getCharsetName());
					System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
				}
				else if (bomStream.hasBOM(ByteOrderMark.UTF_16LE)) {
					System.out.println("XML has a UTF-16LE BOM");
					System.out.println("BOM value is : " + bom.getCharsetName());
					System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
				} 
				else if (bomStream.hasBOM(ByteOrderMark.UTF_32BE)) {
					System.out.println("XML has a UTF_32BE BOM");
					System.out.println("BOM value is : " + bom.getCharsetName());
					System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
				} 
				else if (bomStream.hasBOM(ByteOrderMark.UTF_32LE)) {
					System.out.println("XML has a UTF_32LE BOM");
					System.out.println("BOM value is : " + bom.getCharsetName());
					System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
				} 

				//IOUtils.copy(bomStream, out);

				
				/*// verify XML doc if syntax is correct
				try
				{
					Document doc = null;
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					InputSource is = new InputSource();
					is.setEncoding("UTF-8");
					is.setByteStream(responseStream);
					doc = builder.parse(is);
					String str = doc.getDocumentElement().getNodeName();
					System.out.println(doc.getFirstChild());
				}
				
				catch(SAXException ex)
				{
					ex.printStackTrace();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}*/
				
				
			
				//InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bomStream), charSet);
				//int firstNonBOMByte = bomStream.read();
				//skip BOM


				int read = 0;
				byte[] buffer = new byte[32768];
				while((read = bomStream.read(buffer)) >0)
				{
					out.write(buffer, 0, read);
				}


				/*int read = 0;
				byte[] buffer = new byte[32768];
				while((read = responseStream.read(buffer)) >0)
				{
					out.write(buffer, 0, read);
				}*/



				/*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;

				builder = factory.newDocumentBuilder();



				Document doc = builder.parse(responseStream);
				TransformerFactory factory2 = TransformerFactory .newInstance();
				Transformer form;
				form = factory2.newTransformer();
				form.transform(new DOMSource(doc), new StreamResult(System.out));
				 */


				/*Document doc = builder.parse(responseStream);
				InputSource is = new InputSource();
				is.setEncoding("UTF-8");
				is.setByteStream(responseStream);
				doc = builder.parse(is);
				String str = doc.getDocumentElement().getNodeName();
				System.out.println(doc.getFirstChild());*/


			}

			catch(ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			} 

			finally
			{
				if(responseStream !=null)
				{
					try
					{
						responseStream.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close inputstream");
						ex.printStackTrace();
					}

				}

				if(responseStream !=null)
				{
					try
					{
						out.flush();
						out.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close inputstream");
						ex.printStackTrace();
					}

				}

			}
			return responseString;

		}

	}

}
