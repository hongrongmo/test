package vtw.api;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;

import org.apache.http.HttpResponse;

/**
 * 
 * @author TELEBH
 * @Date: 04/21/2022
 * @Description: Create HttpClientBuilder using java 11 java.net.http
 *
 */

public class TestHttpClientNewBuilder {

	
	public void getHttpClient()
	{
		  
		HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).
				build();
		try {
			sendHttpRequest(client);
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void sendHttpRequest(HttpClient client) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://vtw.elsevier.com/asset/pat/WO2012012496A2?ver=78ce44aac012315c3dd5528e9f3ef62f"))
        		.header("Authorization", getBasicAuthenticationHeader("engineering-village", "evVtw!23"))
        		.build();
       client.send(request, BodyHandlers.ofString());
       
      
    }
	
	private static final String getBasicAuthenticationHeader(String username, String password) {
	    String valueToEncode = username + ":" + password;
	    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}
  
	public static void main(String[] args)
	{
		TestHttpClientNewBuilder obj = new TestHttpClientNewBuilder();
		obj.getHttpClient();
	}
	
}
