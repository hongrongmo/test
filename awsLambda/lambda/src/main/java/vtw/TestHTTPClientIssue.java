package vtw;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import vtw.VTWAssetAPI.MyHttpResponseHandler;

public class TestHTTPClientIssue {

	private static final String HOST = "acc.vtw.elsevier.com";
	private static final String username = "engineering-village";
	private static final String password = "";
	
	// get the list of Patent-Ids; with their signedAssetURL if any;  to download 
	private static Map<String,String> patentIds = new LinkedHashMap<String,String>();   // only for testing

	static CloseableHttpClient client;
	static MyHttpResponseHandler<String> responseHandler;


	// only for testing
	public static void main(String [] args)
	{

		TestHTTPClientIssue test = new TestHTTPClientIssue();
		try {
			
			VTWAssetAPI api = new VTWAssetAPI(args[0], Integer.parseInt(args[1]), null);
			//
			patentIds.put("US7368003B2", "");
			api.downloadPatent(patentIds, client, "forward", null, null);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// create HTTPClient

	public static CloseableHttpClient getInstance() throws Exception
	{
		try
		{
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(HOST, 443), 
					new UsernamePasswordCredentials(username, password));

			//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download

			client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
					.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
			//responseHandler = new BasicResponseHandler();
			responseHandler = new MyHttpResponseHandler<String>();

			System.out.println(responseHandler.toString());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}


		return client;
	}


}
