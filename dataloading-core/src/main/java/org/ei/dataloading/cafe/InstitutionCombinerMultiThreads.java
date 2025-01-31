package org.ei.dataloading.cafe;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.dataloading.DataLoadDictionary;

/**
 * 
 * @author TELEBH
 * @Date: 06/16/2017
 * @Description: Index Cafe Institution Profile using Multithreading 
 * Only CPX AF profiles to be indexed in ES 
 * by comparing AFFID with ANI metadata/lookup tables, index to ES by
 * 			1. write ES docs into out files as bulk contents, where each file contains max up to 1000 profiles
 * 			2. index to ES using multithreading that each thread assigned to group of out files to index
 */

public class InstitutionCombinerMultiThreads {


	public String[] AfCombinedRecKeys = {AuAfCombinedRec.DOCID, AuAfCombinedRec.EID, AuAfCombinedRec.STATUS, AuAfCombinedRec.AFID, 
			AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, AuAfCombinedRec.AFFILIATION_SORT_NAME, AuAfCombinedRec.AFFILIATION_VARIANT_NAME, 
			AuAfCombinedRec.ADDRESS, AuAfCombinedRec.CITY, AuAfCombinedRec.STATE, AuAfCombinedRec.ZIP, AuAfCombinedRec.COUNTRY};
	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "";
	static int loadNumber = 0;
	static String tableName = "institute_profile";
	static String metadataTableName = "hh_af_metadata";
	static String action = "new";
	static int recsPerEsbulk;
	private static String esDomain = "search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
	private static String tableToBeTruncated = "IPR_ES_INDEXED";
	private static String esIndexedIdsSqlldrFileName = "iprESIndexedIdsFileLoader.sh";
	static String esIndexType = "file";
	static int numOfThreads = 1;

	static int ESdirSeq_ID = 1;

	// get CurrentData and Time for ESIndexTime
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	String date;

	List<String> affId_deletion_list;
	static List<String> esIndexed_docs_list = new ArrayList<String>();	


	static CombinedAuAfJSON writer;
	Perl5Util perl = new Perl5Util();

	String esDir;
	static AuAfESIndex s3upload;
	static WriteEsDocToFile docWrite;


	Connection con = null;


	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();


	public static void main(String args[])
	{
		if(args.length >14)
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
			if(args[10] !=null)
			{
				esDomain = args[10];

				System.out.println("ES Domain name: " + esDomain);
			}
			if(args[11] !=null)
			{
				tableToBeTruncated = args[11];
			}
			if(args[12] !=null)
			{
				esIndexedIdsSqlldrFileName = args[12];
				System.out.println("APR ES Indexed IDS sqlldrFileName: " + esIndexedIdsSqlldrFileName);
			}

			if(args[13] !=null)
			{
				esIndexType = args[13];
				if(esIndexType.equalsIgnoreCase("file") || esIndexType.equalsIgnoreCase("direct"))

					System.out.println("ES Index Type: " + esIndexType);
				else
				{
					System.out.println("Invalid ES Index Type, re-try with EStype either direct or file");
					System.exit(1);
				}
			}
			if(args[14] !=null)
			{
				if(Pattern.matches("^\\d*$", args[14]))
				{
					numOfThreads = Integer.parseInt(args[14]);

					System.out.println("Number of Threads: " + numOfThreads);
				}
				else
				{
					System.out.println("Number of Threads has wrong format");
					System.exit(1);
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
			// doc_type should be "ipr"
			if(doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
				System.out.println("Start ES Extract for Doc_type: " + doc_type);
			else
			{
				System.out.println("Invalid document type!!, please re-run with document type ipr");
				System.exit(1);
			}

			docWrite = new WriteEsDocToFile(recsPerEsbulk);

			writer = new CombinedAuAfJSON(doc_type,loadNumber,  docWrite, esIndexType);
			//writer.init(ESdirSeq_ID);


			InstitutionCombinerMultiThreads c = new InstitutionCombinerMultiThreads();
			c.con = c.getConnection(url,driver,username,password);

			c.esDir = writer.getEsDirName();


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
		String query = null;

		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");

			if(!(action.isEmpty()) && (action.equalsIgnoreCase("new") || action.equalsIgnoreCase("update")))
			{
				query = "select * from " +  tableName + " where affid in (select INSTITUTE_ID from " + metadataTableName + 
						" where STATUS='matched' and dbase='cpx') and PARENTID is null";
				System.out.println("query");

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


				writeRecs(rs,con);

				docWrite.close();
				System.out.println("Wrote records.");


				IndexESDocFilesToES();

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for run ES extract & index "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
			}

			else if(!(action.isEmpty()) && action.equalsIgnoreCase("delete"))
			{

				query = "select M_ID from " +  tableName ;

				System.out.println(query);

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);
				getDeletionList(rs);
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

			/*if(con !=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}*/
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
				query = "select * from " +  tableName + " where loadnumber=" + loadNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + 
						" where STATUS='merged' and dbase='cpx') and PARENTID is null";

				System.out.println(query);

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

				writeRecs(rs,con);

				docWrite.close();
				System.out.println("Wrote records.");

				IndexESDocFilesToES();

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for run ES extract & index "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

			}
			else if(!(action.isEmpty()) && action.equalsIgnoreCase("update"))
			{
				updateNumber=loadNumber;

				query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + 
						" where STATUS='matched' and dbase='cpx') and PARENTID is null";


				//for testing
				//query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and affid in (select INSTITUTE_ID from " + metadataTableName + " where dbase='cpx') and rownum<2";

				// 04/04/2017, only index AU profile that has BD CPX abstract records in fast DEV for Dayton to test EV App

				/*	query =  "select * from " +  tableName + "  where AFFID in (select INSTITUTE_ID from ap_correction1.Cafe_af_lookup where pui "
						+ " in (select pui from ap_correction1.AUTHOR_MID))";*/



				System.out.println(query);

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


				writeRecs(rs,con);

				docWrite.close();
				System.out.println("Wrote records.");

				IndexESDocFilesToES();


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

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);
				getDeletionList(rs);

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

			/*if(con !=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}*/
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

					// UPDATEEPOCH (place holder for future filling with SQS epoch)
					rec.put(AuAfCombinedRec.UPDATEEPOCH, "");

					//LOADNUMBER
					if(rs.getString("LOADNUMBER") !=null)
					{
						rec.put(AuAfCombinedRec.LOAD_NUMBER, Integer.toString(rs.getInt("LOADNUMBER")));
					}
					//UPDATENUMBER
					if(rs.getString("UPDATENUMBER") !=null)
					{
						rec.put(AuAfCombinedRec.UPDATE_NUMBER, Integer.toString(rs.getInt("UPDATENUMBER")));
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

					//PARENTID (always set to "0") bc only Parent Institutions indexed to ES, set to non-Zero for child/departments
					if(rs.getString("PARENTID") !=null)
					{
						rec.put(AuAfCombinedRec.PARAFID, rs.getString("PARENTID"));
						rec.put(AuAfCombinedRec.AFTYPE, "dept");
					}
					else
					{
						rec.put(AuAfCombinedRec.PARAFID, "0");
						rec.put(AuAfCombinedRec.AFTYPE, "parent");
					}

					//PREFEREDNAME
					if(rs.getString("PREFERED_NAME") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, DataLoadDictionary.mapEntity(rs.getString("PREFERED_NAME")));
					}

					//PREFPARNAME
					if(rs.getString("PARENT_PREFERED_NAME") !=null)
					{
						rec.put(AuAfCombinedRec.PARENT_PREFERED_NAME, rs.getString("PARENT_PREFERED_NAME"));
					}

					//SORTNAME
					if(rs.getString("SORT_NAME") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_SORT_NAME, DataLoadDictionary.mapEntity(rs.getString("SORT_NAME")));
					}

					//NAMEVARIANT
					if(rs.getString("NAME_VARIANT") !=null)
					{
						rec.put(AuAfCombinedRec.AFFILIATION_VARIANT_NAME, DataLoadDictionary.mapEntity(getStringFromClob(rs.getClob("NAME_VARIANT"))));
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
		affId_deletion_list = new ArrayList<String>(); 
		try {
			while (rs.next())
			{
				if(rs.getString("M_ID") !=null)
				{
					//affId_deletion_list.add("affiliation/"+rs.getString("M_ID")+".json");
					affId_deletion_list.add(rs.getString("M_ID"));  // M_ID list to delete from ES
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


	private String getStringFromClob(Clob clob)
	{
		String str = null;
		try
		{
			if(clob !=null)
			{
				str = clob.getSubString(1, (int) clob.length());
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
		return str;
	}

	/**
	 * Added: 06/16/2017
	 * to physically index generated ES bulks to ES using multithreading 
	 */

	public void IndexESDocFilesToES()
	{
		int numOfThreads = 4;
		CountDownLatch latch = null;
		try
		{
			if(action !=null && (action.equalsIgnoreCase("update") || action.equalsIgnoreCase("new")))
			{
				if(WriteEsDocToFile.esFilesList.size() >0)
				{
					int listSize = WriteEsDocToFile.esFilesList.size();

					// #ofThreads must be <= # of total files to process
					if(numOfThreads > listSize)
					{
						numOfThreads = listSize;
						System.out.println("#ofThreads > # of total ES Files, so reset #ofThreads to be = # of total ES Files");
						latch = new CountDownLatch(numOfThreads);
					}

					latch = new CountDownLatch(numOfThreads);

					double sublist = listSize/numOfThreads;
					int subListSize = (int)sublist;
					int start = 0;
					int last = (subListSize -1);



					System.out.println("Total files to process per single thread: " + subListSize);
					if(numOfThreads ==1)
						last = subListSize -1;



					System.out.println("STARTING................." + new Date().getTime());


					for(int i=0;i<numOfThreads;i++)
					{
						AusAffESIndexMultiThreads esIndexThread = new AusAffESIndexMultiThreads("Thread " + i,latch,recsPerEsbulk, esDomain, action, docWrite, start,last);
						esIndexThread.init();
						esIndexThread.start();

						Thread.sleep(4000);   // sleep for 4 seconds
						synchronized (esIndexThread) {
							start = last + 1;
							if(i<(numOfThreads-2))
								last = start + (subListSize -1);
							else
								last = listSize -1;

							System.out.println("***********************");	
						}
					}
					latch.await();

					System.out.println("In Main thread after completion of " + numOfThreads + " threads");
					System.out.println("FINISHED................." + new Date().getTime());

					//shutdown Amazon Http clinet
					AmazonHttpClientService.getInstance().end();

					//added 05/10/2017 to update status = "indexed" for the docs that successfully indexed to ES
					UpdateProfileTableESStatus profileESUpdate = new UpdateProfileTableESStatus(doc_type, action,username, password,loadNumber,tableToBeTruncated,url,esIndexedIdsSqlldrFileName);
					profileESUpdate.writeIndexedRecs(esIndexed_docs_list);

				}

			}
			else if(action !=null && action.equalsIgnoreCase("delete"))
			{
				int start = 0;
				int last = affId_deletion_list.size() -1;
				latch = new CountDownLatch(0);
				System.out.println("action is delete, so nothing to do with updating AU Profile's status column, just delete from ES");

				AusAffESIndexMultiThreads esIndexThread = new AusAffESIndexMultiThreads("Thread1",latch,recsPerEsbulk, esDomain, action, docWrite, start, last);
				esIndexThread.init();
				esIndexThread.createBulkDelete(doc_type, affId_deletion_list);

				//shutdown Amazon Http clinet
				AmazonHttpClientService.getInstance().end();
			}

	}
	catch(Exception e)
	{
		e.printStackTrace();
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
