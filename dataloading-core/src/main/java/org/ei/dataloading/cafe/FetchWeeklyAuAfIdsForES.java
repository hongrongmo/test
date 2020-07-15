package org.ei.dataloading.cafe;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amazonaws.services.iot.model.SqlParseException;

import java.sql.DriverManager;

/**
 * 
 * @author TELEBH
 * @Date: 07/14/2020
 * @Description, To keep AuID/AfId doc-count for AUthor/affiliation search result page in sync with EV doc search, Harold used to 
 * send file with all AUIDS list with corresponding doc-count from Fast for us to compare with previous week list to find updated doc_count and so re-index 
 * those impacted author profiles to AWS elsaticsearch. Since we migrating from fast to ES, shared search service can't provide the same list as it used to be for Fast
 * after discussion with Hawk team and EV team, the temp solution would be only get list of AUids/AFids for CPX docs processed for current week only, instead of checking all millions
 * of auids/afids. By this we can limit # of Ids to check and so wont cause overhead on shared search service/ES
 * 
 * Weekly CPX data will include (BD new, update, delete, cafe update, delete)
 * 
 */
public class FetchWeeklyAuAfIdsForES {
	
	Connection con;
	ResultSet rs;
	
	//Logger logger = 
	public static void main(String[] args)
	{
		FetchWeeklyAuAfIdsForES obj = new FetchWeeklyAuAfIdsForES();
		obj.fetchWeeklyIds();
	}

	public void fetchWeeklyIds()
	{
		String query = "select author,author_1, affiliation, affiliation_1, count(*) over() total_rows from cafe_master where pui in "
				+ "(select distinct a.pui from cafe_master a, cafe_pui_list_master b, cpx_weekly_load c where a.pui = b.pui and b.puisecondary = c.pui)"; 
		try
		{
			
		}
		catch(SqlParseException ex)
		{
			
		}
	}
	
	private Connection getConnection(String connectionURL,
			String driver,
			String username,
			String password)
					throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
				username,
				password);
		return con;
	}

	
}
