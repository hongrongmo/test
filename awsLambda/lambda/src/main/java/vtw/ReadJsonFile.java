package vtw;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;



/**
 * 
 * @author TELEBH
 * @Date: 09/16/2016
 * @Description: Read JSON file for to simulate reading VTW SQS Message
 */
public class ReadJsonFile {

	DownloadVtwFile instance;

	public ReadJsonFile()
	{
	}
	public static void main(String[] args) {


		String message_type = null;
		String prefix = null;
		try
		{
			ReadJsonFile readJsonFile = new ReadJsonFile();
			readJsonFile.instance = new DownloadVtwFile();

			JSONParser parser = new JSONParser();
			String currDir = System.getProperty("user.dir");
			Object obj = parser.parse(new FileReader(currDir + "/vtw-json.txt"));

			JSONObject jsonObject = (JSONObject) obj;
			String msgId = (String) jsonObject.get("@id");
			String msgType = (String)jsonObject.get("msg:type");
			String msgTo = (String)jsonObject.get("msg:to");

			// only process SQS Message that meant to EV ONLY
			if(msgTo !=null && msgTo.contains("E-Village"))
			{
				// based on msgtype, check whether to process Service Call or EVentNotification Message

				if(msgType !=null)
				{
					if(msgType.contains("EventNotification"))
					{
						message_type= "msg:event";
						prefix="evt";
					}
					else if (msgType.equalsIgnoreCase("ServiceCall"))
					{
						message_type="msg:service";
						prefix = "svc";
					}
					else
					{
						System.out.println("Unknown Message Type: " + msgType + "!");
						System.exit(1);
					}


					// get Asset Pre-signed url for each of eventNotification/ServiceCall

					//Assets
					JSONObject assets = (JSONObject)jsonObject.get(message_type);

					//Resource
					JSONArray resource = (JSONArray)assets.get(prefix+":resource");
					@SuppressWarnings("unchecked")
					Iterator<String> resourceIterator = (Iterator<String>)resource.iterator();

					while(resourceIterator.hasNext())
					{
						System.out.println(prefix + " Resource: " + resourceIterator.next());
					}

					//evt:detailes or svc:detailes

					JSONObject detailes = (JSONObject)assets.get(prefix+":details");
					String signedAssetURL = (String)detailes.get(prefix+":signedAssetURL");
					System.out.println("SignedAssetURL: " + signedAssetURL);

					// download the XML file using pre-signedAssetURL

					if(signedAssetURL !=null)
					{
						readJsonFile.instance.retrieveAsset(signedAssetURL);
					}


				}
			}

			System.out.println("msg id is: " +  msgId + "message type: " + msgType);
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not found");
			ex.printStackTrace();
		}
		catch(ParseException ex)
		{
			System.out.println("Json Parser exception type: " + ex.getErrorType() + ": " +  ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

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
