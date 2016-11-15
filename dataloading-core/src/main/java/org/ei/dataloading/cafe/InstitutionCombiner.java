package org.ei.dataloading.cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.CombinerTimestamp;
import org.ei.dataloading.DataLoadDictionary;

/**
 * 
 * @author TELEBH
 * @date: 06/21/2016
 * @description: Institution ElasticSearch index file
 * Only CPX AU/AF profiles to be indexed in ES 
 * by comparing affiliation-id with ANI metadata tables.
 */
public class InstitutionCombiner{



	public String[] AfCombinedRecKeys = {AuAfCombinedRec.DOCID, AuAfCombinedRec.EID, AuAfCombinedRec.STATUS, AuAfCombinedRec.AFID, 
			AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, AuAfCombinedRec.AFFILIATION_SORT_NAME, AuAfCombinedRec.AFFILIATION_VARIANT_NAME, 
			AuAfCombinedRec.ADDRESS, AuAfCombinedRec.CITY, AuAfCombinedRec.STATE, AuAfCombinedRec.ZIP, AuAfCombinedRec.COUNTRY};
	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it";
	static int loadNumber = 0;
	static String tableName = "institute_profile";
	static String metadataTableName = "hh_af_metadata";
	static String action = "new";
	static int recsPerEsbulk;

	// get CurrentData and Time for ESIndexTime
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	String date;

	LinkedHashSet<String> affId_deletion_list;

	static CombinedAuAfJSON writer;
	Perl5Util perl = new Perl5Util();

	static AuAfESIndex s3upload;
	static AusAffESIndex esIndex;

	Connection con = null;
	
	
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();


	public static void main(String args[])
	{
		if(args.length >9)
		{
			if(args[0] !=null)
			{
				doc_type = args[0];
			}
			if(args[1] !=null)
			{
				url = args[1];
			}
			if(args[2] != null)
			{
				driver = args[2];
			}
			if(args[3] !=null)
			{
				username = args[3];
			}
			if(args[4] !=null)
			{
				password = args[4];
			}
			if(args[5] !=null && args[5].length() >0)
			{
				if(Pattern.matches("^\\d*$", args[5]))
				{
					loadNumber = Integer.parseInt(args[5]);
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}

			}
			if(args[6] !=null)
			{
				tableName = args[6];
			}
			if(args[7] !=null)
			{
				metadataTableName = args[7];
			}
			if(args[8] !=null)
			{
				action = args[8];
			}
			if(args[9] !=null)
			{
				try
				{
					recsPerEsbulk = Integer.parseInt(args[9]);
					
					System.out.println("ES Documents per Bulk: " + recsPerEsbulk);
				}
				catch(NumberFormatException ex)
				{
					recsPerEsbulk = 10;
				}
			}

		}
		else
		{
			System.out.println("Not Enough Parameters");
			System.exit(1);
		}

		try
		{
			writer = new CombinedAuAfJSON(doc_type,loadNumber);
			writer.init(1);

			//s3upload = new AuAfESIndex(doc_type);
			esIndex = new AusAffESIndex(recsPerEsbulk);

			InstitutionCombiner c = new InstitutionCombiner();
			c.con = c.getConnection(url,driver,username,password);

			midTime = System.currentTimeMillis();
			endTime = System.currentTimeMillis();
			System.out.println("Time for finish reading input parameter & ES initialization "+(endTime-startTime)/1000.0+" seconds");
			System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");


			if(loadNumber ==1)
			{
				c.writeCombinedByTable();
			}
			else
			{
				c.writeCombinedByWeekNumber();
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// extract whole table for au/af

	public void writeCombinedByTable() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			String query = "select * from " +  tableName + " where affid in (select INSTITUTE_ID from " + metadataTableName + " where dbase='cpx')";
			System.out.println("query");

			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + tableName);
			
			midTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
			System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
			
			
			writeRecs(rs,con);
			
			esIndex.ProcessBulk();
			esIndex.end();
			
			System.out.println("Wrote records.");
			
			midTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("time for run ES extract & index "+(endTime-midTime)/1000.0+" seconds");
			System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
			
		}

		finally
		{
			if(rs !=null)
			{
				try
				{
					rs.close();
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
		}
	}


	public void writeCombinedByWeekNumber() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;

		String query=null;
		int updateNumber;

		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			if(!(action.isEmpty()) && action.equalsIgnoreCase("new"))
			{
				query = "select * from " +  tableName + " where loadnumber=" + loadNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + " where dbase='cpx')";

				System.out.println(query);

				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

				writeRecs(rs,con);
				
				esIndex.ProcessBulk();
				esIndex.end();
				
				
				System.out.println("Wrote records.");
				
				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for run ES extract & index "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
			}
			else if(!(action.isEmpty()) && action.equalsIgnoreCase("update"))
			{
				updateNumber=loadNumber;
				query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + " where dbase='cpx')";

				//for testing
				//query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + " where dbase='cpx') and rownum<2";

				System.out.println(query);

				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


				writeRecs(rs,con);
				
				esIndex.ProcessBulk();
				esIndex.end();
				
				
				System.out.println("Wrote records.");

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for run ES extract & index "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

			}

			else if(!(action.isEmpty()) && action.equalsIgnoreCase("delete"))
			{
				// need to check with Hongrong

				updateNumber=loadNumber;
				query = "select M_ID from " +  tableName + " where updatenumber=" + updateNumber;

				System.out.println(query);

				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);
				getDeletionList(rs);
				
				//deletion part moved to CafeDownloadFileFromS3AllTypes

			}

		}

		finally
		{
			if(rs !=null)
			{
				try
				{
					rs.close();
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
		}

	}


	public void writeRecs(ResultSet rs, Connection con) throws Exception
	{
		int count = 0;
		while (rs.next())
		{
			try
			{

				AuAfCombinedRec rec = new AuAfCombinedRec();

				date= dateFormat.format(new Date());



				if(doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
				{


					//M_ID
					rec.put(AuAfCombinedRec.DOCID, rs.getString("M_ID"));

					//LOADNUMBER
					if(rs.getString("LOADNUMBER") !=null)
					{
						rec.put(AuAfCombinedRec.LOAD_NUMBER, Integer.toString(rs.getInt("LOADNUMBER")));
					}

					//EID
					if(rs.getString("EID") !=null)
					{
						rec.put(AuAfCombinedRec.EID, rs.getString("EID"));
					}

					//DOC_TYPE
					if(doc_type !=null)
					{
						rec.put(AuAfCombinedRec.DOC_TYPE, doc_type);
					}

					//STATUS
					if(rs.getString("STATUS") !=null)
					{
						rec.put(AuAfCombinedRec.STATUS, rs.getString("STATUS"));
					}

					//TIMESTAMP in DB, in ES called "LOADDATE"
					if(rs.getString("TIMESTAMP") !=null)
					{
						//rec.put(AuAfCombinedRec.TIMESTAMP, timeStampFormat(rs.getString("TIMESTAMP")));
						rec.put(AuAfCombinedRec.LOADDATE, rs.getString("TIMESTAMP"));
					}

					//INDEXDATE in DB, in ES called "ITEMTRANSACTIONID"
					if(rs.getString("INDEXED_DATE") !=null)
					{
						rec.put(AuAfCombinedRec.ITEMTRANSACTIONID, rs.getString("INDEXED_DATE"));
					}

					// EPOCH in DB, in ES called "INDEXEDDATE"
					if(rs.getString("EPOCH") !=null)
					{
						rec.put(AuAfCombinedRec.INDEXEDDATE, rs.getString("EPOCH"));
					}

					// ES Index Data (Current DateTime)
					rec.put(AuAfCombinedRec.ESINDEXTIME, date);

					//AFFILIATION_ID
					if(rs.getString("AFFID") !=null)
					{
						rec.put(AuAfCombinedRec.AFID, rs.getString("AFFID"));
						//System.out.println("AFFID from AuAfCombinedRec: " + rec.getString(AuAfCombinedRec.AFFILIATION_ID));
					}

					//PREFEREDNAME
					if(rs.getString("PREFERED_NAME") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, DataLoadDictionary.mapEntity(rs.getString("PREFERED_NAME")));
					}

					//SORTNAME
					if(rs.getString("SORT_NAME") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_SORT_NAME, DataLoadDictionary.mapEntity(rs.getString("SORT_NAME")));
					}

					//NAMEVARIANT
					if(rs.getString("NAME_VARIANT") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_VARIANT_NAME, DataLoadDictionary.mapEntity(rs.getString("NAME_VARIANT")));
					}

					//ADDRESSPART
					if(rs.getString("ADDRESS_PART") !=null)
					{
						rec.put(AuAfCombinedRec.ADDRESS, DataLoadDictionary.mapEntity(rs.getString("ADDRESS_PART")));
					}

					//CITY
					if(rs.getString("CITY") !=null)
					{
						rec.put(AuAfCombinedRec.CITY, rs.getString("CITY"));
					}

					//STATE
					if(rs.getString("STATE") !=null)
					{
						rec.put(AuAfCombinedRec.STATE, rs.getString("STATE"));
					}

					//POSTALCODE
					if(rs.getString("POSTAL_CODE") !=null)
					{
						//System.out.println("RS postcode: " + rs.getString("POSTALCODE"));
						rec.put(AuAfCombinedRec.ZIP, DataLoadDictionary.mapEntity(rs.getString("POSTAL_CODE")));
						//System.out.println("AuAfCombinedRec Postcode: " + rec.getString(AuAfCombinedRec.POST_CODE));
					}

					//COUNTRY
					if(rs.getString("COUNTRY") !=null)
					{
						rec.put(AuAfCombinedRec.COUNTRY, rs.getString("COUNTRY"));
					}

					//QUALITY
					if(rs.getString("QUALITY") !=null)
					{
						rec.put(AuAfCombinedRec.QUALITY, rs.getString("QUALITY"));
					}

					//CERTAINITY_SCORES
					if(rs.getString("CERTAINTY_SCORES") !=null)
					{
						rec.put(AuAfCombinedRec.CERTAINITY_SCORES, rs.getString("CERTAINTY_SCORES"));
					}
				}

				writer.writeAfRec(rec);

				count ++;

			}
			catch (SQLException e) 
			{
				System.out.println("Error Occurred reading from ResultSet for DOCID: " + rs.getString("M_ID") + " ... " + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("Total records count: " +  count);
	}


	private void getDeletionList(ResultSet rs)
	{
		affId_deletion_list = new LinkedHashSet<String>();
		try {
			while (rs.next())
			{
				if(rs.getString("M_ID") !=null)
				{
					affId_deletion_list.add("affiliation/"+rs.getString("M_ID")+".json");
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Total Aff records to be deleted from S3 & ES: " + affId_deletion_list.size());
	}



	public String timeStampFormat(String timestamp)
	{
		StringBuffer time_stamp = new StringBuffer();
		if(timestamp == null)
		{
			return "";
		}
		switch (timestamp.length()) {
		case 8:
			if(timestamp.matches("\\d{4}\\d{2}\\d{2}"))
			{
				time_stamp.append(timestamp.substring(0, 4));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(4, 6));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(6, 8));

			}
			else
				System.out.println("Invalide Timestamp: " + timestamp);
			break;
		case 7:

			if(timestamp.matches("\\d{4}\\d{2}\\d{1}"))
			{
				time_stamp.append(timestamp.substring(0, 4));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(4, 6));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(6));

			}
			else
				System.out.println("Invalide Timestamp: " + timestamp);
			break;

		case 6:

			if(timestamp.matches("\\d{4}\\d{2}"))
			{
				time_stamp.append(timestamp.substring(0, 4));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(4, 6));
			}
			else
				System.out.println("Invalide Timestamp: " + timestamp);
			break;

		case 5:

			if(timestamp.matches("\\d{4}\\d{1}"))
			{
				time_stamp.append(timestamp.substring(0, 4));
				time_stamp.append("-");
				time_stamp.append(timestamp.substring(4));
			}
			else
				System.out.println("Invalide Timestamp: " + timestamp);
			break;
		}

		return time_stamp.toString();
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