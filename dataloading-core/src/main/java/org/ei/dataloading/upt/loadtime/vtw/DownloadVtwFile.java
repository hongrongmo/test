package org.ei.dataloading.upt.loadtime.vtw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;





























import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.wink.common.model.wadl.HTTPMethods;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.ClientConfiguration;

import sun.awt.image.ByteArrayImageSource;

/**
 * 
 * @author TELEBH
 * @Date: 09/16/2016
 * @Description: get the pre-signed URL from VTW SQS message to download the corresponding asset (Patent XML record) and then upload to store in S3 bucket in
 * an organized S3 subfolder for later Processing (i.e. converting)
 */
public class DownloadVtwFile {




	private final static Logger logger = Logger.getLogger(DownloadVtwFile.class);

	static String patentID = null;
	static String URL = null;
	static String downloadDir = null;

	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;


	/// Set up the request
	private static HttpGet generateRequest() {

		HttpGet request = null;
		try
		{
			request = new HttpGet(URI.create(URL)); 
			request.setConfig(VTWSearchAPI.requestConfig);
		}
		catch (Exception e) 
		{

			System.out.println("Patent URL Request exception!");
			System.out.println(e.getMessage());
			System.out.println(e.hashCode());

			e.printStackTrace();
		}  

		return request;
	}	

	/// Send the request to the server
	private static String sendRequest(HttpGet request) {

		String responseCode = null;
		try {	
			// using ResponseHandler class below
			responseHandler =  new MyHttpResponseHandler<String>();

			responseCode = client.execute(request,responseHandler);
			//System.out.println("Response is:  " +  response);


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
		
			return responseCode;
	}

	public static void main(String[] args) {

		// Generate the request
		HttpGet request = generateRequest();

		// Send the request to the server
		sendRequest(request);

	}


	public static void downloadPatent(String patentid, String url, String download_dir) {

		patentID = patentid;
		URL = url;
		downloadDir = download_dir;
		String code = null;
		
		try
		{
			// create Apache HttpClient
			client = VTWSearchAPI.getInstance();


			// sleep for 1 millisecond to avoid overloading network, and so decrease percentage of receiving "502" error or other non "200" errors
			Thread.currentThread().sleep(1000);
			
			
			// Generate the request
			HttpGet request = generateRequest();

			// Send the request to the server
			if(request !=null)
			{
				for(int i=0;i<2;i++)
				{
					code = sendRequest(request);
					
					if(code !=null && Integer.parseInt(code) ==200)
					{
						break;
					}
					else
					{
						Thread.currentThread().sleep(1000);		// sleep for "1 s" before re-try
						System.out.println("Retry downloading file.....");
						
					}
				}
				
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}



	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {

		@Override
		public String handleResponse(HttpResponse response)
		{

			InputStream responseStream = null;
			BOMInputStream  bomStream = null;
			OutputStream out = null;
			
			int responseCode;


			try
			{
				responseStream = response.getEntity().getContent();
				responseCode = response.getStatusLine().getStatusCode();
				
				if(responseCode ==200)
				{
					out = new FileOutputStream(new File(downloadDir + "/" + patentID + ".xml"));
					
					/**
					 * This class is used to wrap a stream that includes an encoded ByteOrderMark as its first bytes. 
					 * This class detects these bytes and, if required, can automatically skip them and return the subsequent byte as the first byte in the stream. 
					 */
					//BOMInputStream  bomStream = new BOMInputStream(responseStream,false);
					bomStream = new BOMInputStream(responseStream,false,
																	ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
																	ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
					
					ByteOrderMark bom = bomStream.getBOM();
					String charSet = bom.getCharsetName();

					if(bomStream.hasBOM())
					{
						System.out.println("XML has a UTF-8 BOM");
						System.out.println("BOM value is : " + charSet);
						System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
					}
					else if (bomStream.hasBOM(ByteOrderMark.UTF_16BE)) {
						// 
						System.out.println("XML has a UTF-16BE BOM");
						System.out.println("XML has a UTF-16LE BOM");
						System.out.println("BOM value is : " + charSet);
						System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
					}
					else if (bomStream.hasBOM(ByteOrderMark.UTF_16LE)) {
						System.out.println("XML has a UTF-16LE BOM");
						System.out.println("BOM value is : " + charSet);
						System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
					} 
					else if (bomStream.hasBOM(ByteOrderMark.UTF_32BE)) {
						System.out.println("XML has a UTF_32BE BOM");
						System.out.println("BOM value is : " + charSet);
						System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
					} 
					else if (bomStream.hasBOM(ByteOrderMark.UTF_32LE)) {
						System.out.println("XML has a UTF_32LE BOM");
						System.out.println("BOM value is : " + charSet);
						System.out.println("detailes about BOM: " +  bom.getBytes().toString() + "length: " + bom.getBytes().length);
					} 
					
					IOUtils.copy(bomStream, out);
					
					
					/*  // check XML syntax
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
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
					*/
				}
				else
				{
					System.out.println("download response code " + responseCode  + " is not 200, so skip this file");
					System.out.println("Respnse: " + IOUtils.toString(response.getEntity().getContent()));
				}
				
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
				if(bomStream !=null)
				{
					try
					{
						bomStream.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close inputstream");
						ex.printStackTrace();
					}

				}
				if(out !=null)
				{
					try
					{
						out.flush();
						out.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close outputstream");
						ex.printStackTrace();
					}

				}
			}
			return  Integer.toString(response.getStatusLine().getStatusCode());
		}

	}
}

