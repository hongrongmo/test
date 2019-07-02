package org.ei.dataloading;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.domain.ISearchForm;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
/*import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;*/
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.*;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

/*
 * Author: HT
 * Date: 09/14/2015
 * Description: Check weekly Data Processing & send SNS when there is error
 */
public class DataLoadingErrorCheck {

	private static Connection con = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;


	static String url="jdbc:oracle:thin:@localhost:1521:eid";
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction";
	static String password="ei3it";
	static String fileName=null;
	static String sourceFileName=null;
	static String operation = null;
	static String database;
	static String doctype;
	static int updateNumber=0;
	static int loadNumber=0;

	static String dbRDS = "";

	static int srcCount = 0;
	int convertedCount = 0;
	int tempCount = 0;
	int masterCount = 0;

	ArrayList <String> errorsList = new ArrayList<String>();
	ArrayList<String> accessNumberList = new ArrayList<String>();
	ArrayList<String> puiList = new ArrayList<String>();

	//List of other errors & their count that in log file (i.e. records not converted due to having "null" accessnumber,... (for S300)
	Map<String,Integer> otherErrors = new HashMap<String, Integer>();

	static DataLoadingErrorCheck dl = null;

	DataLoadingErrorCheck()
	{

	}

	public static void main(String[] args) throws Exception
	{
		dl= new DataLoadingErrorCheck();


		if(args[0] !=null)
		{
			fileName = args[0];
		}

		if(args[1] !=null)
		{
			database = args[1];
		}

		if(args[2] !=null)
		{
			operation = args[2];
		}

		if(args[3] !=null)
		{
			doctype = args[3];
			//System.out.println("DocType is : " + doctype);
		}

		if(args.length >4)
		{

			if((operation !=null) && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")))
			{
				updateNumber = Integer.parseInt(args[4]);
			}
			else if((operation !=null) && (operation.equalsIgnoreCase("new")))
			{
				loadNumber = Integer.parseInt(args[4]);
			}
			else
			{
				//System.out.println("UpdateNumber/LoadNumber has wrong format");
			}

			srcCount = Integer.parseInt(args[5]);
		}

		if(args.length >6)
		{
			if(args[6] !=null)
			{
				url = args[6];

				//System.out.println("RDS: " + url);

				dbRDS = url.substring(url.lastIndexOf(":")+1,url.length()).trim();
			}
			if(args[7] !=null)
			{
				username = args[7];
				//System.out.println("Schema: " + username);
			}
			if(args[8] !=null)
			{
				password = args[8];
			}
		}

		if(args.length >9)
		{
			if(args[9] !=null)
			{
				sourceFileName = args[9];

				//System.out.println("Source Data File is : " + source_DataFileName);
			}

		}

		// Since conversion happen once, so call it once
		if(dbRDS !=null && dbRDS.equalsIgnoreCase("eid"))
		{
			dl.ReadLogFile();
		}


		dl.checkCountDiff();

		dl.alertSNS();


	}


	public void ReadLogFile() throws Exception
	{
		String line = null;
		try
		{
			File file = new File(fileName);

			if(!file.exists())
			{
				System.out.println("File Not Found");
				System.exit(1);
			}

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);


			String accessNumber = "";
			String pui = "";
			StringBuffer recordIdentifier = new StringBuffer();

			while (dis.available() !=0)
			{
				line = dis.readLine();

				if(line !=null && (line.contains("Exception in thread") || line.contains("java.sql.SQLException")))
				{
					errorsList.add("Exception: " + line);
				}

				if(line !=null && line.contains("Exception:"))
				{
					errorsList.add("Exception: " + line);
				}

				// need to catch the accessnumber for this record 
				if(line.contains("<itemid idtype=\"CPX\">") && line.contains("</itemid>"))
				{
					//accessNumber = line.substring(line.indexOf("idtype=\"CPX\">")+13, line.indexOf("</itemid>")-9);
					accessNumber = line.substring(line.indexOf("idtype=\"CPX\">")+13,line.lastIndexOf("</itemid>"));
					if(recordIdentifier.length() >0)
					{
						recordIdentifier.append(" / ");
					}
					recordIdentifier.append(accessNumber);
					
					
				}

				// need to catch the pui for this record 

				if(line.contains("<itemid idtype=\"PUI\">") && line.contains("</itemid>"))
				{
					//pui = line.substring(line.indexOf("idtype=\"PUI\">")+13, line.indexOf("</itemid>")-9);
					pui = line.substring(line.indexOf("idtype=\"PUI\">")+13, line.indexOf("</itemid>"));
					if(recordIdentifier.length() >0)
					{
						recordIdentifier.append(" / ");
					}
					recordIdentifier.append(pui);
					
				}
				
				// to add to the LIST of AN/PUI
				if(line.contains("<itemid idtype=\"CPX\">") && line.contains("</itemid>") || line.contains("<itemid idtype=\"PUI\">") && line.contains("</itemid>"))
				{
					accessNumberList.add(recordIdentifier.toString());
				}
				if(line !=null && line.contains("records was loaded into the temp table"))
				{
					tempCount = Integer.parseInt(line.substring(0,line.indexOf("records")).trim()); 

					//System.out.println("Temp Table Count is: " + Integer.toString(tempCount));
				}


				if(line !=null && line.contains("records with null accessnumber"))
				{
					int count = Integer.parseInt(line.substring(line.indexOf("are")+3, line.indexOf("records")).trim());
					otherErrors.put("records with null accessnumber", count);
				}	

				/**** to avoid confusion of SNS messages, the records with blocked issn/e-issn should not be considered as error, so skip this part, uncomment when needed
					Commented on Tuesday 07/02/2019, WK [ 201928]
				*****/
				/*
				// check records blocked for issn/e-issn block to append 
				if(line !=null && line.contains("block record") && line.contains("for issn"))
				{
					// 1. get count of blocked issn
					if(otherErrors.containsKey("Records blocked for ISSN"))
					{
						otherErrors.put("Records blocked for ISSN", otherErrors.get("Records blocked for ISSN") + 1) ;
					}
					else
					{
						otherErrors.put("Records blocked for ISSN", 1);
					}

				}

				if(line !=null && line.contains("block record") && line.contains("for eissn"))
				{
					// 1. get count of blocked issn
					if(otherErrors.containsKey("Records blocked for E-ISSN"))
					{
						otherErrors.put("Records blocked for E-ISSN", otherErrors.get("Records blocked for E-ISSN") + 1) ;
					}
					else
					{
						otherErrors.put("Records blocked for E-ISSN", 1);
					}
				}
				*/
				
				// reset for next iteration, so no accumulation
				
				recordIdentifier.delete(0, recordIdentifier.length());

			}
			
			if (accessNumberList.size() >0)
			{
				StringBuffer allIds = new StringBuffer("AccessNumber/PUI with exception: ");
				
				for(int k=0;k<accessNumberList.size();k++)
				{
					if(k >1)
					{
						allIds.append(",");
					}
					allIds.append(accessNumberList.get(k));
				}
				
				errorsList.add(allIds.toString());
				
			}
			
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		}

	}

	public void checkMasterCount()
	{
		String query = "";
		if(operation !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete") || operation.equalsIgnoreCase("new")) &&
				(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("aip")
						|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("chm")
						|| database.equalsIgnoreCase("elt") || database.equalsIgnoreCase("geo")))
		{
			// since new cpx and aip are now loaded as S300 correction, so to get each individual file count
			if(doctype.equalsIgnoreCase("cpxnew"))
			{
				query = "select count(*) count from "+ masterTableMapping() + " where updatenumber='"+updateNumber+"' and database='"+database +"'"
						+ " and updateresource like '%" + sourceFileName + "%'";

			}
			else
			{
				query = "select count(*) count from "+ masterTableMapping() + " where updatenumber='"+updateNumber+"' and database='"+database +"'";
			}

		}

		else if(operation !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")))
		{
			query = "select count(*) count from "+ masterTableMapping() + " where updatenumber="+updateNumber;
		}

		else if(operation !=null && operation.equalsIgnoreCase("new"))
		{
			query = "select count(*) count from "+ masterTableMapping() + " where load_number="+loadNumber;
		}

		try
		{
			con = getConnection(url,driver,username,password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			if(rs.next())
			{
				masterCount = rs.getInt(1);
			}

			//System.out.println("Master Table Count: " + masterCount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			if (rs != null)
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
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}


	public void checkTempCount()
	{
		String query = "";

		if(operation !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")) &&
				(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("aip")
						|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("chm")
						|| database.equalsIgnoreCase("elt") || database.equalsIgnoreCase("geo")))
		{
			query = "select count(*) count from "+ tempTableMapping() + " where updatenumber='"+updateNumber+"' and database='"+database +"'";
		}
		else
		{
			query = "select count(*) count from "+ tempTableMapping() + " where updatenumber="+updateNumber;
		}

		System.out.println("Running query: " + query);
		try
		{
			con = getConnection(url,driver,username,password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			if(rs.next())
			{
				tempCount = rs.getInt(1);
			}

			//System.out.println("Temp Table Count: " + tempCount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			if (rs != null)
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
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}




	public void checkCountDiff()
	{
		int temp_master_diff = 0;
		String snsMessage = "";


		checkMasterCount();

		if(dbRDS !=null && !(dbRDS.equalsIgnoreCase("eid")) && operation !=null && !(operation.equalsIgnoreCase("new")))
		{
			checkTempCount();
		}

		if(operation !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")))
		{
			temp_master_diff = tempCount - masterCount;

			if(temp_master_diff >0 && operation.equalsIgnoreCase("update"))
			{
				snsMessage = "Temp Table Count:: " + tempCount + " is greater than Master Table Count:: " + masterCount + " by:: " + temp_master_diff;

				errorsList.add(snsMessage);
			}

			//if nothing loaded to Temp table, still need to report as issue
			if(tempCount==0)
			{
				snsMessage = "No Records were loaded to Temp Table";

				errorsList.add(snsMessage);
			}


			//if nothing loaded to Master table, still need to report as issue
			if(masterCount==0 && operation.equalsIgnoreCase("update"))
			{
				snsMessage = "No Records were loaded to Master Table";

				errorsList.add(snsMessage);
			}

		}
		/*else if(operation !=null && operation.equalsIgnoreCase("new"))
    	{
    		src_master_diff = srcCount - masterCount;
    		snsMessage = "Source Count:: " + srcCount + " is greater than Master Table Count:: " + masterCount + " by:: " + src_master_diff;
    	}*/


	}


	public void alertSNS()
	{
		int j=1;
		int converted_RejectedCount = 0;
		StringBuffer errors = new StringBuffer();

		// add otherErrors to errorlist first & convertedcount

		if(otherErrors.size() >0)
		{
			for(String Key: otherErrors.keySet())
			{
				errorsList.add(Key+ " [" + otherErrors.get(Key) + "]");
				converted_RejectedCount = converted_RejectedCount + otherErrors.get(Key);
			}

			convertedCount = srcCount - converted_RejectedCount;
		}

		else
		{
			convertedCount = srcCount;
		}




		// preliminary information & errorsList 
		if(errorsList.size() >0)
		{

			//EID
			if(dbRDS !=null && dbRDS.equalsIgnoreCase("eid"))
			{
				if(operation !=null && operation.equalsIgnoreCase("new"))
				{
					/*errors.append(new Date().toString() + "::- " + operation + " " + database + " [" + doctype + "]  on [" + dbRDS + "] WK [" + loadNumber + "] and File: [" + sourceFileName 
							//+ " of Total SRC Count: " + srcCount 
							//+ " and Converted Count: " + convertedCount + " converted with issues:-");
							+ "] converted with issues:-");*/    // original before running new cpx as corr
					
					errors.append("[" + doctype + "]:: [" + dbRDS + "] - Exception(s) detected while processing file [" + sourceFileName + "]"
					+ " with load/update number [" + loadNumber + "]:");
				}
				else if (operation !=null && (operation.equalsIgnoreCase("update")|| operation.equalsIgnoreCase("delete")))
				{
					/*errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  process on [" + dbRDS + "] for week [" + updateNumber + 
            				"] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + " and Converted Count: " + convertedCount
            						+ " and Temp table Count: " + tempCount + " and Master Count: " + masterCount + " processed with issues:-");*/

					/*errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  on [" + dbRDS + "] WK [" + updateNumber + 
							"] and File: [" + sourceFileName + "] processed with issues:-");   // 2nd update
					 */
					
					errors.append("[" + doctype + "]:: [" + dbRDS + "] - Exception(s) detected while processing file [" + sourceFileName + "]"
							+ " with load/update number [" + updateNumber + "]:");

				}
			}

			//EIB/EIA
			else if(dbRDS !=null && !(dbRDS.equalsIgnoreCase("eid")))
			{

				if(operation !=null && operation.equalsIgnoreCase("new"))
				{
					/*errors.append(new Date().toString() + "::- " + operation + " " + database + " [" + doctype + "]  process on [" + dbRDS + "] for week [" + loadNumber + 
        					"] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + " converted with issues:-");*/

					/*errors.append(new Date().toString() + "::- " + operation + " " + database + " [" + doctype + "]  on [" + dbRDS + "] WK [" + loadNumber + 
							"] and File: [" + sourceFileName + "] converted with issues:-");*/   //2nd update
					
					errors.append("[" + doctype + "]:: [" + dbRDS + "] - Exception(s) detected while processing file [" + sourceFileName + "]"
							+ " with load/update number [" + loadNumber + "]:");
					

				}
				else if (operation !=null && (operation.equalsIgnoreCase("update")|| operation.equalsIgnoreCase("delete")))
				{
					/*errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  process on [" + dbRDS + "] for week [" + updateNumber + 
            				"] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + " and Temp table Count: " + tempCount + 
            				" and Master Count: " + masterCount + " processed with issues:-");*/

					/*errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  on [" + dbRDS + "] WK [" + updateNumber + 
							"] and File: [" + sourceFileName + "] processed with issues:-");*/   //2nd update
					
					errors.append("[" + doctype + "]:: [" + dbRDS + "] - Exception(s) detected while processing file [" + sourceFileName + "]"
							+ " with load/update number [" + updateNumber + "]:");


				}

			}


			for(int i=0;i<errorsList.size();i++)
			{
				if(errors.length() >0)
				{
					errors.append("\n");
				}

				errors.append("(" + j + ") " + errorsList.get(i));
				j++;
			}
		}

		// if no errors , as per Andre request not to send SNS if not issues, so comment this out

		/*
    	else if (errorsList.size() ==0 && operation !=null && (operation.equalsIgnoreCase("update")|| operation.equalsIgnoreCase("delete")))
    	{
    		//EID
    		if(dbRDS !=null && dbRDS.equalsIgnoreCase("eid"))
    		{
    			errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  process on [" + dbRDS + "] for week [" + updateNumber + 
        				"] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + " and Converted Count: " + convertedCount +
        				" and Temp table Count: " + tempCount + " and Master Count: " + masterCount + " is complete with no issues");

    		}
    		else
    		{
    			errors.append(new Date().toString() + "::- " + database + " [" + doctype + "]  process on [" + dbRDS + "] for week [" + updateNumber + 
        				"] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + " and Temp table Count: " + tempCount + 
        				" and Master Count: " + masterCount + " is complete with no issues");


    		}

    	}
    	else if (errorsList.size() ==0 && operation !=null && operation.equalsIgnoreCase("new"))
    	{
    		//EID
    		if(dbRDS !=null && dbRDS.equalsIgnoreCase("eid"))
    		{
    			errors.append(new Date().toString() + "::- " + operation + " " + database + " [" + doctype + "]  process on [" + dbRDS + "] "
    					+ "for week [" + loadNumber + "] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + 
        				" and Converted Count: " + convertedCount +  " converted with no issues");
    		}
    		else
    		{
    			errors.append(new Date().toString() + "::- " + operation + " " + database + " [" + doctype + "]  process on [" + dbRDS + "] "
    					+ "for week [" + loadNumber + "] and File: [" + sourceFileName + " of Total SRC Count: " + srcCount + 
        				" converted with no issues");
    		}


    	} */

		CheckReturnValueForShell();
		
		if(!(errors.toString().isEmpty()))
			sendAwsSNS(errors.toString());
	}


	private void sendAwsSNS(String snsMessage)
	{
		//AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider("AwsCredentials.properties"));

		
		InstanceProfileCredentialsProvider awsCreds = new InstanceProfileCredentialsProvider();    // for EC2

		AmazonSNSClient snsClient = new AmazonSNSClient(awsCreds);
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		//Publish to SNS Topic
		//String topicArn = "arn:aws:sns:us-east-1:230521890328:HananEmail";   // only for me
		String topicArn = "arn:aws:sns:us-east-1:230521890328:EVDataLoading";
		String message = snsMessage;
		PublishRequest publishRequest =  new PublishRequest(topicArn, message);
		PublishResult publishResult = snsClient.publish(publishRequest);

		// print MessageId of message published to SNS topic
		//System.out.println("MessageId - " + publishResult.getMessageId());

	}



	private static String masterTableMapping()
	{
		String tableName = null;

		if(operation !=null && database !=null)
		{
			if((operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")) &&
					(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("aip")
							|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("chm")
							|| database.equalsIgnoreCase("elt") || database.equalsIgnoreCase("geo")))
			{
				tableName = "BD_MASTER_ORIG";
			}
			else if (operation.equalsIgnoreCase("new")&&(database.equalsIgnoreCase("cpx") 
					|| database.equalsIgnoreCase("aip") || database.equalsIgnoreCase("pch")
					|| database.equalsIgnoreCase("chm") || database.equalsIgnoreCase("elt") 
					|| database.equalsIgnoreCase("geo")))
			{
				tableName="BD_MASTER";
			}
			else if ((operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")) && database.equalsIgnoreCase("grf"))
			{
				tableName= "GEOREF_MASTER_ORIG";
			}
			else if (operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("grf"))
			{
				tableName = "GEOREF_MASTER_2014";
			}
			else if (operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("ins"))
			{
				tableName = "INS_2014_MASTER";
			}
			else if (operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("nti"))
			{
				tableName = "NTIS_MASTER";
			}
			else if (operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("cbn"))
			{
				tableName="CBN_MASTER";
			}
			else if(operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("upt"))
			{
				tableName="UPT_MASTER";
			}
			else if (operation.equalsIgnoreCase("new") && database.equalsIgnoreCase("ept"))
			{
				tableName = "EPT_MASTER";
			}

		}
		return tableName;
	}



	private static String tempTableMapping()
	{
		String tableName = null;

		if(operation !=null && database !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete")))
		{
			if(database.equalsIgnoreCase("cpx") && doctype!=null && (doctype.equalsIgnoreCase("s300") || doctype.equalsIgnoreCase("cpxnew") || doctype.equalsIgnoreCase("cpxdel")
					|| doctype.contentEquals("cpxupd")))
			{
				tableName = "BD_AIP_TEMP";
			}
			/*
			 * else if(database.equalsIgnoreCase("cpx") && doctype!=null &&
			 * !(doctype.equalsIgnoreCase("s300"))) { tableName = "BD_CORRECTION_TEMP"; }
			 */
			else if(database.equalsIgnoreCase("geo"))
			{
				tableName = "GEO_CORRECTION_TEMP";
			}
			else if(database.equalsIgnoreCase("elt"))
			{
				tableName = "ELT_CORRECTION_TEMP";
			}
			else if(database.equalsIgnoreCase("pch"))
			{
				tableName = "PCH_CORRECTION_TEMP";
			}
			else if(database.equalsIgnoreCase("chm"))
			{
				tableName = "CHM_CORRECTION_TEMP";
			}
			else if(database.equalsIgnoreCase("grf"))
			{
				tableName = "GEOREF_CORRECTION_TEMP";
			}
			else if(database.equalsIgnoreCase("ins"))
			{
				tableName = "INS_CORRECTION_TEMP";
			}


		}
		return tableName;
	}


	public void CheckReturnValueForShell()
	{
		if(errorsList.size() >0)
		{
			for(int i=0;i<errorsList.size();i++)
			{
				if(errorsList.get(i).contains("Exception in thread") || errorsList.get(i).contains("java.sql.SQLException") || 
						errorsList.get(i).contains("Exception:"))
				{
					System.out.println(errorsList.get(i));
				}
				if(errorsList.get(i).contains("No Records were loaded to Temp Table")&& (operation !=null && (operation.equalsIgnoreCase("update") || operation.equalsIgnoreCase("delete"))))
				{
					System.out.println("Temp Table count is 0");
				}

				if(errorsList.get(i).contains("No Records were loaded to Master Table")&& (operation !=null && operation.equalsIgnoreCase("update")))
				{
					System.out.println("Master Table count is 0");
				}

			}
		}

	}

	private static Connection getConnection(String connectionURL,String driver, String username, String password)
			throws Exception
	{
		Class.forName(driver);

		Connection con = DriverManager.getConnection(connectionURL, username, password);

		return con;
	}

}
