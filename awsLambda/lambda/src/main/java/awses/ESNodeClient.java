package awses;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
/*
 * Author: HH
 * Date: 02/29/2016
 * Description: Index Document in Amazon ElasticSearch using Node Client Mode	
 */
import org.elasticsearch.node.NodeBuilder.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.elasticsearch.node.NodeBuilder.*;

public class ESNodeClient {

//HH 12/09/2021 due to compilation error with settings.settingsBuilder, I'll temp comment out till find solution

	public static void main(String[] args) 
	{
			
		/*Settings settings = Settings.settingsBuilder().put("cluster.name","230521890328:evcafeauaf")
				.build();*/
		
		/*
		Settings settings = Settings.settingsBuilder().put("cluster.name","evcafeauaf").build();
		
		Node node = nodeBuilder().clusterName("evcafeauaf").node();
		node.start();
		Client client = node.client();*/
		
		
		//Client cleint = TransportClient.builder().settings(settings).build();  // for localhost

		/*
		try {

			/*client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName("search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com"),443));  //for localhost ES "localhost:9200"
		*/
			
			
			//create JSON Author Document

		/*
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
			
			if(node !=null)
			{
				try
				{
					node.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}*/
	}
}
