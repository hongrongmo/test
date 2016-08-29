package org.ei.dataloading.awslambda;

import com.amazonaws.ClientConfiguration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

public class AmazonEsService {


		private static AmazonEsService instance;

		private JestClient client;
		
		private static int ES_CONN_TIMEOUT = 18000 * 1000;  //1 minute timeout
		private static int ES_SOCK_TIMEOUT = 18000 * 1000;  // 1 minute timeout
				
				
		public AmazonEsService()
		{
			
		}
		
		public static AmazonEsService getInstance() 
		{
			synchronized (AmazonEsService.class){
				
			
			if(instance == null)
			{
				
				instance = new AmazonEsService();
				
				 System.out.println("Creating Jest ES clientfactory...");
				 try
				 {
					 
					 JestClientFactory factory = new JestClientFactory();
						factory.setHttpClientConfig(new HttpClientConfig
								.Builder("http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80")
								.multiThreaded(true)
								.connTimeout(ES_CONN_TIMEOUT)
								.requestCompressionEnabled(true)
								.build()
								);
						
					 instance.client = (JestHttpClient) factory.getObject();
					 
				 } catch (Exception e) {
					 
					 e.printStackTrace();
				 } 
			}
		}

			return instance;
		}
		
		
		
		 public JestClient getAmazonEsService() {
				return this.client;
			    }


}
