package org.ei;

public class RdsMapping {
	
	private static String url = "";
	private static String password = "";
	
	
	public static String singleRdsMapping()
	{
		synchronized (url) {
			//url = "jdbc:oracle:thin:@localhost:15212:eid";   //works for localhost
			url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";   // for deployment
		}
		
		return url;
	}
	
	public static String[] multipleRdsMapping(int rdsId)
	{
		String[] rdsInfo = new String[2];
		
		switch(rdsId)
		{
			case 1: 
				//url = "jdbc:oracle:thin:@localhost:15210:eia";  // for localhost testing
				url = "jdbc:oracle:thin:@eia.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eia";
				password = "";
				break;
			case 2:
				//url = "jdbc:oracle:thin:@localhost:15211:eib";  // for localhost
				url = "jdbc:oracle:thin:@eib.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eib";
				password = "";
				break;
			case 3: 
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost
				url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				password = "";
				break;
			default:
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost
				url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				password = "";
				break;
		}
		
		rdsInfo[0] = url;
		rdsInfo[1] = password;
		
		return rdsInfo;
		
	}
	
	
	public static String mapUrl(int rds)
	{
		if(rds >0 )
		{
			switch (rds) {
			case 1:
				url = "jdbc:oracle:thin:@eia.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eia";  // for deplyment
				//url = "jdbc:oracle:thin:@localhost:15210:eia";   // for localhost testing
				break;
			case 2:
				url = "jdbc:oracle:thin:@eib.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eib";
				//url = "jdbc:oracle:thin:@localhost:15211:eib";   // for localhost testing
				break;
			case 3:
				url="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost testing
				break;

			default:
				url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost testing
				break;
			}
		}
		
		return url;
	}

	
	public static String mapPassword(int rds)
	{
		if(rds >0)
		{
			switch(rds) {
			case 1:
				password = "";
				break;
			case 2: 
				password = "";
				break;
			case 3:
				password = "";
				break;
			default:
				password = " ";
				break;
			}
		
			}
		return password;
	}
}
