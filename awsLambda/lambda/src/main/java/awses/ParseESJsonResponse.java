package awses;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 * @author TELEBH
 * @Date: 05/08/2017
 * @Description: parse AWS ES index JSON response message to fetch the list of M_ID for successfully indexed profiles
 * 	to use the list of M_ID/doc_indexID to update profile table set status="indexed" so do not need to re-index doc every time
 *  run ES index unless it got new updates where status should be back to "null" then after re-index update back to "indexed" 
 */
public class ParseESJsonResponse {

	public static void main(String[] args) {

		JSONParser parser = new JSONParser();



		String awsESJsonResponse = "{\"items\":[{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a6010178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7aa110178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a8310178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a8210178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a5e10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a5f10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a5d10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a7f10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7a7e10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}},"+
				"{\"index\":{\"_index\":\"evcafe\",\"_type\":\"author\",\"_id\":\"aut_M7351e2de156b8ab50beM7b2e10178163171\",\"_version\":2,\"result\":\"updated\",\"_shards\":{\"total\":2,\"successful\":2,\"failed\":0},\"created\":false,\"status\":200}}"+
				"]}";


		System.out.println("parse: " + awsESJsonResponse);
		try
		{
			Object obj = parser.parse(awsESJsonResponse);

			JSONObject jsonObject = (JSONObject) obj;

			//Items
			JSONArray items = (JSONArray) jsonObject.get("items");


			@SuppressWarnings("unchecked")
			Iterator<JSONObject> indexesIterator = (Iterator<JSONObject>)items.iterator();

			while(indexesIterator.hasNext())
			{
				//indexes list
				 JSONObject indexes = indexesIterator.next();
				
				JSONObject index = (JSONObject)indexes.get("index");
				String _id = (String)index.get("_id");
				Long _version = (Long)index.get("_version");
				String _index = (String)index.get("_index");
				Long status = (Long)index.get("status");
				System.out.println("ID: "+ _id + "  ES indexed with index: " + _index + " version: " + _version + " status: " + status);
			}
		}
		catch (org.json.simple.parser.ParseException e) 
		{
			e.printStackTrace();
		}

	}

}
