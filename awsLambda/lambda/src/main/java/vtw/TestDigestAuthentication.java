package vtw;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class TestDigestAuthentication {

	/**
	 * @author TELEBH
	 * @Date: 10/03/2016
	 * @Description: A simple example that uses HttpClient to execute an HTTP request against
	 * a VTW site that requires Digest authentication.
	 * 
	 * @NOTE: if i put header type as "http" and port "80", it automatically at the backend redirect to "https" which listen on port "443", and still reuires authentication
	 * but since no authentication is provided for the redirect, it raise "auauthorized" error and so can  not retrive asset metadata.
	 * but when set header type from beginning as "https" and port "443" i can get the AssetMetadata
	 * 
	 * 
	 * This below Example works very well, i can use it as basic for VTW AssetRetrival 
	 */
	

	    public static void main(String[] args) throws Exception {
	        CredentialsProvider credsProvider = new BasicCredentialsProvider();
	        credsProvider.setCredentials(
	                new AuthScope("acc.vtw.elsevier.com", 443),
	                new UsernamePasswordCredentials("engineering-village", "PWD"));
	        CloseableHttpClient httpclient = HttpClients.custom()
	                .setDefaultCredentialsProvider(credsProvider)
	                .build();
	        try {
	            HttpGet httpget = new HttpGet("https://acc.vtw.elsevier.com/search/?query=identifier:pat:EP1412238A1&Accept=application/json");

	            System.out.println("Executing request " + httpget.getRequestLine());
	            CloseableHttpResponse response = httpclient.execute(httpget);
	            try {
	                System.out.println("----------------------------------------");
	                System.out.println(response.getStatusLine());
	                System.out.println(EntityUtils.toString(response.getEntity()));
	            } finally {
	                response.close();
	            }
	        } finally {
	            httpclient.close();
	        }
	    }
	}
	

