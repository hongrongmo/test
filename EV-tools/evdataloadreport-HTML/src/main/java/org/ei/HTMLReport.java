package org.ei;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;



/*
 * Date: 08/11/2015
 * Description: create HTML file for weekly dataloading report content, by reading refined report data
 * from DB table to an html file & upload to S3 bucket to be easily included any any other app
 */
public class HTMLReport {

	File file = null;
	
	static String url = "jdbc:oracle:thin:@localhost:15212:eid";   	
	static String driver = "oracle.jdbc.driver.OracleDriver";
//	static String username = "ap_correction1";
	static String username = "ap_report";
	static String password = "";

	Connection con;
	Statement stmt =null;
	ResultSet rs =null;


	static int loadnumber = 0;
	static int year = 2016;
	static int weekindex = 0;
	String weekRange = null;
	
	int loadnum = 201547;
	int totalErrorCount = 0;
	
	
	String bucketName = "datafabrication-reports";
	String key = "";
	
	ArrayList <Map<String,String>> rows = new ArrayList<Map<String,String>>();
	Map<String,Integer> totalCount = new HashMap<String, Integer>();
	
	
	Map <String,String>errorMessageMappingList = new HashMap<String,String>();
	
	
	
	{
		errorMessageMappingList.put("unique constraint", "Most of the rejections were due to duplicated deliveries");
		//errorMessageMappingList.put("Field in data file exceeds maximum length", "Some record(s) has extra tab and caused field shifting");
		errorMessageMappingList.put("Field in data file exceeds maximum length", "Field in data file exceeds maximum length");  //temp till find distingish between extra tab and normal exceed max length
		errorMessageMappingList.put("cannot insert NULL into", "Record(s) missing accessnumber");
		errorMessageMappingList.put("Records blocked for ISSN", "Some record(s) blocked on purpose");
		errorMessageMappingList.put("Records blocked for E-ISSN", "Some record(s) blocked on purpose");
		errorMessageMappingList.put("other","Other");
	}

	public HTMLReport()
	{
		reportContent();
		weekRange();
		CreateHTMLReport();
		uploadReportToS3();   //temp comment for testing range date  
		
		
	}



	public static void main(String[] args)
	{
		if(args[0] !=null)
		{
			loadnumber = Integer.parseInt(args[0]);
			if(args[0].length() == 6)
			{
				// since data available only @ Friday, so make default back to previous week, also because we start one back later after first week of year
				weekindex = Integer.parseInt(args[0].substring(4)) - 1;
				System.out.println("Week Number: "+ Integer.parseInt(args[0].substring(4)));
				
			}
			if(args.length >1)
			{
				if(args[1] !=null && args[1].length() >0)
				{
					url = args[1];
				}
				if(args[2] !=null && args[2].length() >0)
				{
					year = Integer.parseInt(args[2]);
				}
			}
			 
		}
		
		// create the html report, & upload to S3 bucket
		HTMLReport report = new HTMLReport();
		
	}

	public void CreateHTMLReport()
	{
		PrintWriter out = null;

		try
		{
			file = new File(loadnumber+".html");

			if(!file.exists())
			{
				file.createNewFile();
				System.out.println("New file created");
			}
			else
			{
				System.out.println("File Already Exist");
				file.delete();
			}

			key = year+"/"+file.getName();  //s3 bucket file path/name
			
			
			
			out = new PrintWriter(new BufferedWriter(new FileWriter(file.getName(),true)));
			if(file.exists())
			{
				out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
				out.println("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"+
						"<title>Weekly Data Processing Report</title>\n"+
						"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"+
						"<!--<link type=\"text/css\" rel=\"stylesheet\" href=\"reports.css\"/>-->\n"+
						"<!--<link type=\"text/css\" rel=\"stylesheet\" href=\"main.css\" />-->\n"+
						"<!--<link type=\"text/css\" rel=\"stylesheet\" href=\"layout.css\"/>-->\n"+
						"<style type=\"text/css\">\n"+
							"body{\n"+
								"width:100%;\n"+
								"min-width:100%;\n"+
								"height:100%;\n"+
								"margin-top:25px;\n"+
								"margin-bottom: 25px;\n"+
								"margin-right:15px;\n"+
								"font-family: Arial, verdana, helvetica, sans-serif;\n"+
								"font-size: 75%;\n"+
							"}\n"+
								
							"#report {\n"+
							    "font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;\n"+
							    "width: 99%;\n"+
							    "border-collapse: collapse;\n"+
							    "padding-left: 85px;\n"+
							    "padding-right: 4px;\n"+
							"}\n"+

							"#report td, #report th {\n"+
							    "font-size: 1.1em;\n"+
							    "border: 1px solid #D3D3D3;\n"+
							    "padding: 3px 7px 2px 7px;\n"+
							"}\n"+
							"#report th{\n"+
							    "font-size: 1.2em;\n"+
							    "text-align: left;\n"+
							    "padding-top: 5px;\n"+
							    "padding-bottom: 4px;\n"+
							    "background-color: #E6E6FA;\n"+
							    "color: #191970;\n"+
							"}\n"+

							"#report tr.alt td {\n"+
							    "color: #000000;\n"+
							    "background-color: #EAF2D3;\n"+
							"}\n"+
							"#report caption{\n"+
								"color:#191970;\n"+
								"background-color: white;\n"+
								"font-weight: bold;\n"+
								"font-family: cursive;\n"+
								"font-size: 1.2em;\n"+
								"text-align: center;\n"+
								 "padding-top: 5px;\n"+
								 "padding-bottom: 6px;\n"+
								 "width: 600px;\n"+
							"}\n"+
							"#report tfoot {\n"+
								"font-size: 1.2em;\n"+
							    "text-align: left;\n"+
							    "padding-top: 5px;\n"+
							    "padding-bottom: 4px;\n"+
							    "background-color: #E6E6FA;\n"+
							    "color: #000080;\n"+
							    "font-weight: bold;\n"+
							"}\n"+
							"#wrapper{\n"+
								"padding-left: 10px;\n"+
								"padding-top: 15px;\n"+
								"}\n"+
								
						"</style>\n"+
						
						"</head>\n"+
						"<body>\n"+
						"<div class=\"wrapper\" style=\"padding-left: 10px; padding-right: 10px; padding-top: 15px;\">\n"+
						"<table id=\"report\">\n"+
						"<thead>\n"+
						"<tr>\n"+
						"<caption>\n"+
						"LoadNumber: "+loadnumber+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span\n"+
						"class=\"weekdate\" style=\"float: center\">Week: "+weekRange+"</span>\n"+
						"</caption>\n"+
						"</tr>\n"+
						"</thead>\n"+
						"<thead>\n"+
						"<tr>\n"+
						"<th>DataSet</th>\n"+
						"<th>Type</th>\n"+
						"<th>Week(YYYYWK)</th>\n"+
						"<th>Source File Name</th>\n"+
						"<th>Source File Count</th>\n"+
						"<th>New Loaded</th>\n"
						);
				
				if(loadnumber >=loadnum)
				{
					out.println("<th>Updated Count</th>");  //start from week 201547 when new cpx loaded as s300 corrections
				}
				out.println(		
						"<th>Discrepancy</th>\n"+
						"<th>Reason</th>\n"+
						"</tr>\n"+
						"</thead>\n"+
						"<tfoot>\n"+
						"<tr>\n"+
						"<td>Total</td>\n"+
						"<td></td>\n"+
						"<td></td>\n"+
						"<td></td>\n");	
				
				if(totalCount !=null && totalCount.size() >0)
				{
						out.println("<td>"+totalCount.get("TOTALSRCCOUT")+"</td>");
						out.println("<td>"+totalCount.get("TOTALLOADEDCOUNT")+"</td>");
						out.println("<td>"+totalCount.get("TOTALUPDATEDCOUNT")+"</td>");
						out.println("<td>"+totalCount.get("TOTALREJECTEDCOUNT")+"</td>");
				}
				else
				{
					out.println("<td></td>");
					out.println("<td></td>");
					out.println("<td></td>");
				}
				out.println("<td></td>\n"+
							"</tr>\n"+
							"</tfoot>\n"
							);
				
				out.println("<tbody>");
				
				if(rows !=null && rows.size() >0)
				{
					for(int i=0;i<rows.size();i++)
					{
						Map <String,String>row = rows.get(i);
						
						out.println("<tr>");
						out.println("<td>"+row.get("DATASET")+"</td>");
						out.println("<td>"+row.get("OPERATION")+"</td>");
						out.println("<td>"+row.get("LOADNUMBER")+"</td>");
						out.println("<td>"+row.get("SOURCEFILENAME")+"</td>");
						out.println("<td>"+row.get("SOURCEFILECOUNT")+"</td>");
						out.println("<td>"+row.get("MASTERTABLECOUNT")+"</td>");
						
						// ONLY start from 201547 when loaded new cpx as s300 corrections
						if(Integer.parseInt(row.get("LOADNUMBER")) >=loadnum)
						{
							out.println("<td>"+row.get("UPDATE")+"</td>");
						}
						
						out.println("<td>"+row.get("SRC_MASTER_DIFF")+"</td>");
						out.println("<td>"+row.get("ERRORMESSAGE")+"</td>");
						out.println("</tr>");
					}
					
				}
				
				out.println("</tbody>\n"+
							"</table>\n"+
							"</div>");
				
				out.println("</body>\n"+
							"</html>");

			}
			out.flush();
			out.close();
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void reportContent()	
	{

		int update_count=0;
		
		String query= "select dataset , operation , loadnumber , sourcefilename  , sourcefilecount , CONVERTEDFILECOUNT, MASTERTABLECOUNT , SRC_MASTER_DIFF , ERRORMESSAGECOUNT , "
				+ "ERRORMESSAGE from dataload_report where loadnumber="+loadnumber + " order by dataset";

		try
		{
			con = getConnection(url, driver, username,password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();


			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();


				for(int i=1;i<=rsmd.getColumnCount();i++)
				{
					// Map ErrorMessage
					if(rsmd.getColumnLabel(i).equalsIgnoreCase("ERRORMESSAGE"))
					{
						row.put(rsmd.getColumnLabel(i),errorMessageMapping(rs.getString(i)));						
					}
					
					else if (rsmd.getColumnLabel(i).equalsIgnoreCase("SRC_MASTER_DIFF") && (rs.getString("dataset").equalsIgnoreCase("CPX")
							|| rs.getString("dataset").equalsIgnoreCase("AIP EEI") || rs.getString("dataset").equalsIgnoreCase("AIP TOC"))
							&& rs.getString("operation").equalsIgnoreCase("new")
							&& Integer.parseInt(rs.getString("loadnumber")) >=loadnum)
					{
						update_count = Integer.parseInt(rs.getString("CONVERTEDFILECOUNT")) - Integer.parseInt(rs.getString("MASTERTABLECOUNT"));
						
						if(rs.getString("ERRORMESSAGE") ==null)
						{
							//row.put("UPDATE",rs.getString(i));
							
							row.put("UPDATE",Integer.toString(update_count));  // start from 201550, updatenumber should be (convertedcount-MasterTableCount)
							row.put(rsmd.getColumnLabel(i),"0");
						}
						else
						{
							// set UPDATE COUNT as SRC_MASTER_DIFF, while discrepancy as sum of counts in ERRORMESSAGECOUNT
							if(rs.getString("ERRORMESSAGECOUNT") !=null)
							{
								/*if(rs.getString("ERRORMESSAGECOUNT").contains("/"))
									totalErrorCount = aggregateErrorCount(rs.getString("ERRORMESSAGECOUNT").toString());
								else
									totalErrorCount = Integer.parseInt(rs.getString("ERRORMESSAGECOUNT"));*/
								
							}
							row.put("UPDATE",Integer.toString(update_count));
							//row.put("UPDATE",Integer.toString(totalErrorCount));
							row.put(rsmd.getColumnLabel(i),rs.getString(i));
						}

					}

					else
					{
						row.put(rsmd.getColumnLabel(i),rs.getString(i));
					}
					// if any other non new cpx, set "updatedcount as 0"
					if(row.get("UPDATE") == null)
					{
						row.put("UPDATE","0");
					}
				}

				
				rows.add(row);

			}

			// get sum of sourcefilecount,MASTERTABLECOUNT,SRC_MASTER_DIFF
			countSum();
		}

		catch(SQLException sqlex)
		{
			System.out.println(sqlex.getMessage());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		finally
		{
			if(con !=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(stmt !=null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(rs !=null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}


	public String errorMessageMapping(String error)
	{
		StringBuffer errorMessage= new StringBuffer();
		int id = 1;
		
		if(error !=null && error.length() >0 && error.contains("/"))
		{
			StringTokenizer strtoken = new StringTokenizer(error, "/");
			
			while(strtoken.hasMoreTokens())
			{
				String str = strtoken.nextToken();
				
				if(errorMessage.length() >0)
				{
					errorMessage.append("</br>");
				}
				if(str.contains("unique constraint"))
				{
					errorMessage.append("("+id+") "+ errorMessageMappingList.get("unique constraint"));
				}
				else if(str.contains("Field in data file exceeds maximum length"))
				{
					errorMessage.append("("+id+") "+errorMessageMappingList.get("Field in data file exceeds maximum length"));
				}
				else if(str.contains("cannot insert NULL into"))
				{
					errorMessage.append("("+id+") "+errorMessageMappingList.get("cannot insert NULL into"));
				}
				else if((str.contains("Records blocked for ISSN") ||  str.contains("Records blocked for E-ISSN"))
						&& (!(errorMessage.toString().contains(errorMessageMappingList.get("Records blocked for ISSN")))))
				{
					errorMessage.append("("+id+") "+ errorMessageMappingList.get("Records blocked for ISSN"));
				}
				else if(str.contains("other"))
				{
					errorMessage.append("("+id+") Other");
					
				}
				
				id++;
				
			}
			
			/* due to blocked records for issn/E-Issn should have one single message, so take off (1) in case there is only 2 reasons for rejection 
			/(ISSn/E-Issn) , take off (1)*/
			
			if(errorMessage.toString().contains("(1)") && !(errorMessage.toString().contains("(2)")))
			{
				errorMessage.delete(0, 3);
				errorMessage.delete(errorMessage.toString().indexOf("<"), errorMessage.toString().indexOf(">")+1);
			}
		}
		else if (error !=null && error.length() >0 && !(error.contains("/")))
		{
			if(errorMessage.length() >0)
			{
				errorMessage.append("\n");
			}
			if(error.contains("unique constraint"))
			{
				errorMessage.append(errorMessageMappingList.get("unique constraint"));
			}
			else if(error.contains("Field in data file exceeds maximum length"))
			{
				errorMessage.append(errorMessageMappingList.get("Field in data file exceeds maximum length"));
			}
			else if(error.contains("cannot insert NULL into"))
			{
				errorMessage.append(errorMessageMappingList.get("cannot insert NULL into"));
			}
			else if(error.contains("Records blocked for ISSN") ||  error.contains("Records blocked for E-ISSN"))
			{
				errorMessage.append(errorMessageMappingList.get("Records blocked for ISSN"));
			}
			else if(error.contains("other"))
			{
				errorMessage.append("Other");
				
			}
			
		}
		
		return errorMessage.toString();
	}
	
	public void countSum()
	{
		int srcCountSum = 0;
		int masterCountSum = 0;
		int rejCountSum = 0;
		int updatedSum = 0;
		
		if(rows !=null && rows.size()>0)
		{
			for(Map<String,String> item: rows)
			{
				// Src file count sum 
				if(item.get("SOURCEFILECOUNT") !=null)
				{
					srcCountSum = srcCountSum + Integer.parseInt(item.get("SOURCEFILECOUNT"));
				}
				
				// Master table (loaded) count sum
				if(item.get("MASTERTABLECOUNT") !=null)
				{
					masterCountSum = masterCountSum + Integer.parseInt(item.get("MASTERTABLECOUNT"));
				}
				
				//Updated (only new cpx and s200) Count sum
				if(item.get("UPDATE") !=null)
				{
					updatedSum = updatedSum + Integer.parseInt(item.get("UPDATE"));
				}
				
				// Rejection Count Sum 
				if(item.get("SRC_MASTER_DIFF") !=null)
				{
					rejCountSum = rejCountSum + Integer.parseInt(item.get("SRC_MASTER_DIFF"));
				}
			}
			
			totalCount.put("TOTALSRCCOUT", srcCountSum);
			totalCount.put("TOTALLOADEDCOUNT",masterCountSum);
			totalCount.put("TOTALUPDATEDCOUNT",updatedSum);
			totalCount.put("TOTALREJECTEDCOUNT",rejCountSum);
		}
		
	}
	
	
	public void weekRange()
	{
		// Set weekNumber Date Range
		
		/*Calendar cal2 = new GregorianCalendar();
		Date trial = new Date();
		cal2.setTime(trial);*/
		
		/*int yearweek = cal2.get(Calendar.WEEK_OF_YEAR) +1;
		int diff = yearweek - (weekindex+1);*/
		
		//int yearweek = cal2.get(Calendar.WEEK_OF_YEAR);
		
		int yearweek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		int diff = yearweek - (weekindex+1);
		
		
		Calendar cal = Calendar.getInstance();
		int i = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
		
		 if(year==2015)
			{
				 cal.add(Calendar.YEAR, -1);
				 cal.add(Calendar.DATE, -((diff+1)*7)-i+2);
					
			}
			else
			{ 
				cal.add(Calendar.DATE, -(diff*7)-i+1);
			}
		 
		//cal.add(Calendar.DATE, -(diff*7)-i+1);
		
		Date start = cal.getTime();
		
		cal.add(Calendar.DATE,  4);
		
		Date end = cal.getTime();

		this.weekRange = start.toString().substring(0, 11).trim() + " - " + end.toString().substring(0, 11).trim();
		
	}
	
	public void uploadReportToS3() throws AmazonClientException,AmazonServiceException
	{
		
		AmazonS3 s3client = AmazonS3Service.getInstance().getAmazonS3Service();
		
		try
		{
			System.out.println("Uploading dataloading report:"+file.getName()+" for S3 bucket: "+bucketName+"/"+key);
			
			PutObjectResult result = s3client.putObject(new PutObjectRequest(bucketName, key, file));
			System.out.println("etag "+result.getETag()+" "+result);
		}
		catch(AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
								"to Amazon S3, but was rejected with an error response" +
								" for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	
	public int aggregateErrorCount(String errorMsgCounts)
	{
		int totalErrorsCount = 0;
		StringTokenizer token = new StringTokenizer(errorMsgCounts, "/");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				totalErrorsCount = totalErrorsCount + Integer.parseInt(token.nextToken().trim());
			}
			
		}
		return totalErrorsCount;
	}
	
	
	public Connection getConnection(String connectionURL, String driver, String username, String password)	
			throws Exception
	{
		Class.forName(driver);
		con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}


}
