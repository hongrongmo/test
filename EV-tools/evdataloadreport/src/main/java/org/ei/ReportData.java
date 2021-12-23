package org.ei;

import java.sql.ResultSet;

public class ReportData {

	boolean hasdata = false;
	ResultSet rs2  = null;
	public ReportData()
	{
		
	}
	public void setRecords(ResultSet rs) throws Exception
	{
		
		if(rs.next())
		{
			rs2 = rs;
		}
		
	}
	
	
	public ResultSet getRecords()
	{
		return rs2;
	}
}
