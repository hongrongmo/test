package vtw.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.QueryPatents;

/**
 * 
 * @author TELEBH
 * @Date: this class servers as the starting point to query VTW Metadata API to download the missed Patents
 * Or the ones got update for missing title for download and then process with weekly Patent files
 */
public class StartVtwMetadataApi {

	
	 String url = "jdbc:oracle:thin:@localhost:1521:eid";
	 String driver = "oracle.jdbc.driver.OracleDriver";
	 String[] secretArns;
	 int loadNumber;
	 
	 QueryVtwMetadataApi apiObj = null;
	 
	 Map<String, String> credentials = new HashMap<>();

	 List<String> patentIds = new ArrayList<>();
	 
	 public static void main(String[] args) throws Exception
	    {

		 //Minumum need to pass over DB Info, and secret credentials for both RDS & VTW
		 	StartVtwMetadataApi startObj = new StartVtwMetadataApi();
	    	startObj.parseInput(args);
	    	startObj.run();
	    }
	 
	 public void parseInput(String[] args)
	 {
		 if(args.length <4)
		 {
			 System.out.println("Not enough parameters");
			 System.exit(1);
		 }
		 if(args[0] != null && !(args[0].isBlank()))
		 {
			 url = args[0];
		 }
		 if(args[1] != null && !(args[1].isBlank()))
		 {
			 driver = args[1];
		 }
		 if(args[2] != null && !(args[2].isBlank()))
		 {
			 if(args[2].contains(";"))
			 {
				secretArns = args[2].split(";"); 
			 }
		 }
		 if(args[3] != null && !args[3].isBlank())
		 {
			 loadNumber = Integer.parseInt(args[3]);
			 System.out.println("LoadNumber: " + loadNumber);
		 }
	 }
	 public void run()
	 {
		 try
	    	{
			 apiObj = new QueryVtwMetadataApi();
			 
			 //retrieve AWS secret manager credentials for DB
			 apiObj.retrieveCredentials(secretArns[0]);
			 credentials = apiObj.getCredentials();
			 
			 long startTime = System.currentTimeMillis()/1000;
			 	
	    		fetchPatentIds();
	    		
	    		long endTime = System.currentTimeMillis()/1000 - startTime;
	    		System.out.println("Total time It took to fetch PatentsIds from Oracle: " + endTime);
	    		
	    		
		    	for(String patent: patentIds)
		    	{
		    		//retrieve AWS secret manager credentials for VTW MetadataAPI
		    		apiObj.retrieveCredentials(secretArns[1]);
		        	ObjectNode node = apiObj.retrieveExistingMetadata("pat", patent);
		        	apiObj.traverseMetadata(node);
		    	}
		    	
		    	endTime = System.currentTimeMillis()/1000 - endTime;
	    		System.out.println("Total time It took to retrieve VTW Metadata: " + endTime);
	    		
		    	apiObj.downloadPatents();
		    	
		    	endTime = System.currentTimeMillis()/1000 - endTime;
	    		System.out.println("Total time It took to download patent XML files: " + endTime);
	    		
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	 }
	 public void fetchPatentIds()
	 {
		 QueryPatents dbObj = null;
		 try
		 {
			 dbObj = new QueryPatents();
			 
			 dbObj.init(url, driver, credentials.get("username"), credentials.get("password"));
	 		 ResultSet rs = dbObj.fetchPatents(loadNumber);
	 		
	 		while(rs.next())
	 		{
	 			if(rs.getString(1) != null)
	 			{
	 				patentIds.add(rs.getString(1));
	 			}
	 		}
	 		System.out.println("Total Number of PatentIds to download from VTW using VTWMetadataApi: " + patentIds.size());
	 		//close result set
	 		dbObj.closeResultSet();
		 }
			catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	finally
	    	{
	    		dbObj.closeResultSet();
	    	}
		
	 }
}
