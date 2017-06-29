package awses;

/*
 * author: HH
 * Date: 02/29/2016
 * Description: Create Transport Cleint Elastic Search for Later Index Document(s)
 */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class ESTransportCleint {

	public void ESTransportCleint(){}
	
	public static void main(String[] args) {
		
		ESTransportCleint escleintobj = new ESTransportCleint();
		escleintobj.createTransportCleint();
		

	}
	
	public void createTransportCleint()
	{
		/*Settings settings = Settings.settingsBuilder().put("cluster.name","elasticsearch")
			.build();    //for localhost
		*/
		//Client cleint = TransportClient.builder().settings(settings).build();  // for localhost
		
		//for prod
		Settings settings = Settings.settingsBuilder().put("cluster.name","230521890328:evcafeauaf")
				.build();
		
		
		

		Client client = null;
		try {

			/*client = TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));  //for localhost ES "localhost:9200"
*/			
			// and localhost kibana "localhost:5601"
			
			
			// for Prod
			/*client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com"),80));  //for localhost ES "localhost:9200"
			
			*/
			
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com"),80));  //for localhost ES "localhost:9200"
			
			
			
			


			//create JSON Author Document

			Map<String,String> json_author = new HashMap<String, String>();
			json_author.put("initials", "K.L.");
			json_author.put("indexed-name", "Martin K.");
			json_author.put("first_name","Kimberly L.");
			json_author.put("last_name" ,"Martin");
			json_author.put("age","30");
			json_author.put("about","I love Bacteriology and Biochemistry");
			json_author.put("interests","Bacteriology , Biochemistry");
			json_author.put("e-address","noemailavailable@noemailavailable.com");
			json_author.put("sourcetitle","Infection and Immunity");


			// Instance a json mapper
			ObjectMapper mapper = new ObjectMapper();  //create once, then reuse it

			//generate json
			byte[] json = mapper.writeValueAsBytes(json_author) ;

			IndexResponse response = client.prepareIndex("cafe", "author", "10039359900")
					.setSource(json).get();

			// Index Info
			//Index Name
			System.out.println(response.getIndex());

			//Type Name
			System.out.println(response.getType());

			//Document ID (generated or not)
			System.out.println(response.getId());

			//Version (if this is the 1st time index this document, you will get 1
			System.out.println(response.getVersion());

			//isCreated() is true if the document is new one, false if it is updated
			System.out.println(response.isCreated());
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (JsonProcessingException e) 
		{
			e.printStackTrace();
		}

		finally
		{
			if(client !=null)
			{
				try
				{
					client.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
