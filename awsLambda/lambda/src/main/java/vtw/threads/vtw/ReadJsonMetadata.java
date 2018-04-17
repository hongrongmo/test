package vtw.threads.vtw;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadJsonMetadata {

	
	@SuppressWarnings("unchecked")
	public static String parseJsonMetadata(String metadata)
	{
		String assetUrl = "";

		JSONParser parser = new JSONParser();
		Object obj;
		try 
		{
			obj = parser.parse(metadata);


			JSONObject jsonObject = (JSONObject) obj;
			JSONObject entries = (JSONObject) jsonObject.get("entries");
			
			JSONArray entry = (JSONArray)entries.get("entry");
			Iterator<JSONObject> entryIterator = (Iterator<JSONObject>)entry.iterator();
			
			if(entryIterator.hasNext())
			{
				//System.out.println("Entry[0]: " + entryIterator.next());
				JSONObject links = (JSONObject) entryIterator.next().get("links");
				
				JSONArray link = (JSONArray) links.get("link");
				entryIterator = (Iterator<JSONObject>)link.iterator();
				if(entryIterator.hasNext())
				{
					assetUrl = (String) entryIterator.next().get("href");
					System.out.println("Patent XML URL: "+ assetUrl);
				}
				
			}

			
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}

		return assetUrl;
	}

}
