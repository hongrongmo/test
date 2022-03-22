package vtw.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
	 String userName = "ap_correction1";
	 String passwd = "";
	 
	 List<String> patentIds = new ArrayList<>();
	 
	 public static void main(String[] args) throws Exception
	    {
		 
			/*
			 * List<String> patents = new ArrayList<>(); patents.add("EP3933456A1");
			 * patents.add("EP3928993A4"); patents.add("US11192742B2");
			 */
	    
	    		StartVtwMetadataApi startObj = new StartVtwMetadataApi();
	    		startObj.run();
	    }
	 
	 public void run()
	 {
		 try
	    	{
	    		fetchPatentIds();
	    		QueryVtwMetadataApi obj = new QueryVtwMetadataApi();
		    	for(String patent: patentIds)
		    	{
		        	ObjectNode node = obj.retrieveExistingMetadata("pat", patent);
		        	obj.traverseMetadata(node);
		    	}
		    	
		    	obj.downloadPatents();
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
			 dbObj.init(url, driver, userName, passwd);
	 		ResultSet rs = dbObj.fetchPatents(202299);
	 		
	 		while(rs.next())
	 		{
	 			if(rs.getString(1) != null)
	 			{
	 				patentIds.add(rs.getString(1));
	 			}
	 		}
	 		System.out.println("Total Number of PatentIds to download from VTW using VTWMetadataApi: " + patentIds.size());
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
