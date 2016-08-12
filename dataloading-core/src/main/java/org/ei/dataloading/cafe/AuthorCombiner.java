package org.ei.dataloading.cafe;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.Constants;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.domain.DataDictionary;

/**
 * 
 * @author TELEBH
 * @Date: 07/14/2016
 * @Description: Cafe Author Profile with embeded Author Affiliation part in the Author profile
 * Only CPX AU profiles to be indexed in ES 
 * by comparing author-id with ANI metadata tables.
 */
public class AuthorCombiner {

	public String[] AuCombinedRecKeys = {AuAfCombinedRec.DOCID, AuAfCombinedRec.EID, AuAfCombinedRec.AUID, AuAfCombinedRec.DOC_TYPE,AuAfCombinedRec.STATUS, 
			AuAfCombinedRec.LOADDATE, AuAfCombinedRec.ITEMTRANSACTIONID, AuAfCombinedRec.ESINDEXTIME, 
			AuAfCombinedRec.ORCID, AuAfCombinedRec.VARIANT_FIRST, AuAfCombinedRec.VARIANT_INI, AuAfCombinedRec.VARIANT_LAST, 
			AuAfCombinedRec.PREFERRED_FIRST, AuAfCombinedRec.PREFERRED_INI, AuAfCombinedRec.PREFERRED_LAST,AuAfCombinedRec.SUBJECT_CLUSTER, 
			AuAfCombinedRec.PUBLICATION_RANGE_FIRST, AuAfCombinedRec.PUBLICATION_RANGE_LAST,AuAfCombinedRec.SOURCE_TITLE,AuAfCombinedRec.ISSN,
			AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, AuAfCombinedRec.AFFILIATION_VARIANT_NAME, AuAfCombinedRec.AFFILIATION_SORT_NAME,
			AuAfCombinedRec.ADDRESS, AuAfCombinedRec.CITY, AuAfCombinedRec.STATE, AuAfCombinedRec.ZIP, AuAfCombinedRec.COUNTRY};
	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it";
	static int loadNumber = 0;
	static String tableName = "author_profile";
	static String metadataTableName = "hh_au_metadata";
	static String action = "new";
	int updateNumber;
	static int recsPerEsbulk;
	
	static int ESdirSeq_ID = 1;
	

	// get CurrentData and Time for ESIndexTime
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	String date;

	static CombinedAuAfJSON writer;
	Perl5Util perl = new Perl5Util();

	HashSet<String> variantInitials = new HashSet<String>();
	HashSet<String> variantFirst = new HashSet<String>();
	HashSet<String> variantLast = new HashSet<String>();
	
	//Author Affiliation
	static AuAffiliation auaf;
	LinkedHashSet<String> affiliation_historyIds_List;
	
	LinkedHashSet<String> auId_deletion_list;

	
	AuAfCombinedRec rec;
	String esDir;
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
			writer.init(ESdirSeq_ID);
			//s3upload = new AuAfESIndex(doc_type);  for ES index using Jest
			
			esIndex = new AusAffESIndex(recsPerEsbulk);
			

			AuthorCombiner c = new AuthorCombiner();
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

	// extract whole Author Table

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
				query = "select * from " +  tableName + " where authorid in (select AUTHOR_ID from " + metadataTableName + " where dbase='cpx')";
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
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
				

			}
			else if(!(action.isEmpty()) && action.equalsIgnoreCase("delete"))
			{
				// need to check with Hongrong

				query = "select M_ID from " +  tableName;
				
				System.out.println(query);

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
		
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			if(!(action.isEmpty()) && action.equalsIgnoreCase("new"))
			{
				query = "select * from " +  tableName + " where loadnumber=" + loadNumber + " and authorid in (select AUTHOR_ID from " + metadataTableName + " where dbase='cpx')";
				
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
				query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and authorid in (select AUTHOR_ID from " + metadataTableName + " where dbase='cpx')";
				
				// for testing
				
				//query = "select * from " +  tableName + " where m_id='aut_M22aaa18f155dfa29a2bM5efd10178163171'";
				
				
				System.out.println(query);

				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);
				
				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time for get records from table "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
				
				writeRecs(rs,con);
				//s3upload.end();
				
				//upload ES files to S3 buckt for ES index with Lambda Function
				//UploadAuAfESToS3.UploadFileToS3(esDir,"evcafe");
				
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
				
				AuAfESIndex.DeleteFilesFromS3(auId_deletion_list, "evcafe");
				
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
		int count =0, rec_count = 1;
		String currentdept_affid= null;
		String email="";
		while (rs.next())
		{
			try
			{

				rec = new AuAfCombinedRec();
				auaf = new AuAffiliation();
				
				 date= dateFormat.format(new Date());
				
				if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
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
					
					//AUTHORID
					if(rs.getString("AUTHORID") !=null)
					{
						rec.put(AuAfCombinedRec.AUID, rs.getString("AUTHORID"));
					}
					
					//ORCID, comment for now bc it is not in author_profile table yet
					if(rs.getString("ORCID") !=null)
					{
						rec.put(AuAfCombinedRec.ORCID, rs.getString("ORCID"));
					}
					
					//PREFERRED_INI
					if(rs.getString("INITIALS") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_INI, DataLoadDictionary.mapUnicodeEntity(rs.getString("INITIALS")));
					}
					
					//PREFERRED_FIRST
					if(rs.getString("GIVENNAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_FIRST, DataLoadDictionary.mapUnicodeEntity(rs.getString("GIVENNAME")));
					}
					
					//PREFERRED_LAST
					if(rs.getString("SURENAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_LAST, DataLoadDictionary.mapUnicodeEntity(rs.getString("SURENAME")));
					}
					
					//NAME_VARAINT (INITIALS, First, Last)
					if(rs.getString("NAME_VARIANT") !=null)
					{
						prepareNameVariant(rs.getString("NAME_VARIANT"));
					}
					
					
					//SUBJABBR
					if(rs.getString("CLASSIFICATION_SUBJABBR") !=null)
					{
						rec.put(AuAfCombinedRec.CLASSIFICATION_SUBJABBR, rs.getString("CLASSIFICATION_SUBJABBR"));
						
						//SUBJECT_CLUSETR
						prepareSubjabbr(rs.getString("CLASSIFICATION_SUBJABBR"));
					}
					
					//PUBLICATION_RANGE
					if(rs.getString("PUBLICATION_RANGE") !=null)
					{
						String[] ranges = rs.getString("PUBLICATION_RANGE").split("-");
						if(ranges[0] !=null)
						{
							rec.put(AuAfCombinedRec.PUBLICATION_RANGE_FIRST, ranges[0]);
						}
						if(ranges[1] !=null)
						{
							rec.put(AuAfCombinedRec.PUBLICATION_RANGE_LAST, ranges[1]);
						}
					}
					
					//SOURCE_TITLE & ISSN
					
					String journals = getStringFromClob(rs.getClob("JOURNALS"));
					
						if(journals !=null)
						{
							prepareSourceTitle(journals);
						}
						// as per XFAB transformation instructions doc
						else
						{
							rec.put(AuAfCombinedRec.SOURCE_TITLE, "undefined");
						}

					if(rs.getString("E_ADDRESS") !=null)
					{
						String []e_mail = rs.getString("E_ADDRESS").split(Constants.IDDELIMITER);
						if(e_mail.length >1)
						{
							email = e_mail[1];
						}
						
						rec.put(AuAfCombinedRec.EMAIL_ADDRESS, email);
					}
					
					//CURRENT_AFFILIATION_ID
					if(rs.getString("CURRENT_AFF_ID") !=null)
					{
						if(rs.getString("CURRENT_AFF_TYPE") !=null && rs.getString("CURRENT_AFF_TYPE").equalsIgnoreCase("parent"))
						{
							auaf.setAffiliationId(rs.getString("CURRENT_AFF_ID"));
						}
						else
						{
							currentdept_affid = rs.getString("CURRENT_AFF_ID");
						}
						
					}
					//CURRENT_PARENT_AFFILIATION_ID
					if(rs.getString("PARENT_AFF_TYPE") !=null && rs.getString("PARENT_AFF_ID") !=null)
					{
						if(rs.getString("PARENT_AFF_TYPE").trim().equalsIgnoreCase("parent"))
						{
							auaf.setAffiliationId(rs.getString("PARENT_AFF_ID"));
						}
					}

					//Current PARENT AFFILIATION INFO
					if(!(auaf.getAffiliationId().isEmpty()))
					{
						//prepareCurrentAffiliation(auaf.getAffiliationId(), currentdept_affid, con);
						prepareCurrentAffiliation(auaf.getAffiliationId(), currentdept_affid);
					}
					
					
					
				//Historical PARENT AFFILIATION INFO
					String history_affiliationIds = getStringFromClob(rs.getClob("HISTORY_AFFILIATIONID"));
					if(history_affiliationIds !=null)
					{
						prepareHistoryAffiliationIds(history_affiliationIds);
						if(affiliation_historyIds_List.size() >0)
						{
							//prepareHistoryAffiliation(con);
							prepareHistoryAffiliation();
						}
					}
					
					//CITY
					auaf.setAffiliationCity();
					
					//COUNTRY
					auaf.setAffiliationCountry();
					
					//Parents AFFILIATION ID
					auaf.setParentAffiliationsId();
					
					
					//CURRENT PARENT AFFILIATION_ID
					rec.put(AuAfCombinedRec.AFID, auaf.getAffiliationId());
					
					//CURRENT PARENT DISPLAY_NAME
					rec.put(AuAfCombinedRec.DISPLAY_NAME, auaf.getAffiliationDisplayName());
					
					//CURRENT PARENT DISPLAY_CITY
					rec.put(AuAfCombinedRec.DISPLAY_CITY, auaf.getAffiliationDisplayCity());
					
					//CURRENT PARENT DISPLAY_COUNTRY
					rec.put(AuAfCombinedRec.DISPLAY_COUNTRY, auaf.getAffiliationDisplayCountry());
					
					//CURRENT PARENT SORT_NAME
					rec.put(AuAfCombinedRec.AFFILIATION_SORT_NAME, auaf.getAffiliationSortName());
					
					//PARENT AFFILIATION HISTORY_ID
					rec.put(AuAfCombinedRec.AFFILIATION_HISTORY_ID, auaf.getHistoryAffid());
					
					// PARENT AFFILIATION HISTORY_DISPLAY_NAME
					rec.put(AuAfCombinedRec.HISTORY_DISPLAY_NAME, auaf.getHistoryDisplayName());
					
					// PARENT AFFILIATION HISTORY_CITY
					rec.put(AuAfCombinedRec.HISTORY_CITY, auaf.getHistoryCity());
					
					// PARENT AFFILIATION HISTORY_COUNTRY
					rec.put(AuAfCombinedRec.HISTORY_COUNTRY, auaf.getHistoryCountry());
					
					//CURRENT_AND_HiSTORY PARENT AFFILIATION_ID
					//rec.put(AuAfCombinedRec.PARENT_AFFILIATION_ID,auaf.getParentAffiliationsId());
					
					//CURRENT_AND_HISTORY PARENT PREFERRED_NAME
					rec.put(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, auaf.getParentAffiliationsPreferredName());
					
					//CURRENT_AND_HOSTORY PARENT NAME_VARIANT
					rec.put(AuAfCombinedRec.AFFILIATION_VARIANT_NAME, auaf.getParentAffiliationsNameVariant());
					
					/*//CURRENT_AND_HISTORY PARENT CITY
					rec.put(AuAfCombinedRec.CITY, auaf.getAffiliationCity());
					
					//CURRENT_AND_HISTORY PARENT COUNTRY
					rec.put(AuAfCombinedRec.COUNTRY, auaf.getAffiliationCountry());*/  //HH 07/25/2016 Copied @ ES Profile Level
					
					//CURRENT_AND_HISTORY PARENT NAMEID
					rec.put(AuAfCombinedRec.NAME_ID, auaf.getAffiliationNameId());
					
					//CURRENT DEPT AFFILIATION_ID
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID, auaf.getCurrentDeptAffiliation_Id());
					
					//CURRENT DEPT AFFILIATION DISPLAY_NAME
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME, DataLoadDictionary.mapUnicodeEntity(auaf.getCurrentDeptAffiliation_DisplayName()));
					
					//CURRENT DEPT AFFILIATION CITY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY, auaf.getCurrentDeptAffiliation_City());
					
					//CURRENT DEPT AFFILIATION COUNTRY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY, auaf.getCurrentDeptAffiliation_Country());
				}

				/*if (rec_count >= 100)
				{
					ESdirSeq_ID ++;
					writer.init(ESdirSeq_ID);
					//upload ES files to S3 buckt for ES index with Lambda Function
					//UploadAuAfESToS3.UploadFileToS3(writer.getEsDirName(),"evcafe");
					
					rec_count = 0;
				}*/
				
				writer.writeAuRec(rec);		
				auaf=null;
				count ++;
				//rec_count++;
				

			}
			catch (SQLException e) 
			{
				System.out.println("Error Occurred reading from ResultSet for DOCID: " + rs.getString("M_ID") + " ... " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		System.out.println("Total records count: " +  count);
	}

	//Name Variant
	public void prepareNameVariant(String name_variant)
	{
		String[] name_variants = null;
		String singleName_Variant = "";
		
		String [] singleVarainat;
		
		// add to the record
		StringBuffer variantNameInit = new StringBuffer();
		StringBuffer variantNameFirst = new StringBuffer();
		StringBuffer variantNameLast = new StringBuffer();

		
			name_variants = name_variant.split(Constants.AUDELIMITER);
			
			for(int i=0;i<name_variants.length;i++)
			{
				singleName_Variant = name_variants[i].trim();
				if(singleName_Variant !=null && !(singleName_Variant.isEmpty()))
				{
					singleVarainat = singleName_Variant.split(Constants.IDDELIMITER);
					
						if(singleVarainat.length>0 && singleVarainat[0] != null)
						{
							variantNameInit.append(DataLoadDictionary.mapUnicodeEntity(singleVarainat[0]));
						}
						if(singleVarainat.length>2 && singleVarainat[2] != null)
						{
							variantNameLast.append(DataLoadDictionary.mapUnicodeEntity(singleVarainat[2]));
						}
						if(singleVarainat.length>3 && singleVarainat[3] !=null)
						{
							variantNameFirst.append(DataLoadDictionary.mapUnicodeEntity(singleVarainat[3]));
						}
						if(i<name_variants.length -1)
						{
							variantNameInit.append(Constants.IDDELIMITER);
							variantNameLast.append(Constants.IDDELIMITER);
							variantNameFirst.append(Constants.IDDELIMITER);
						}
				}
			}
			
			rec.put(AuAfCombinedRec.VARIANT_INI, variantNameInit.toString());
			rec.put(AuAfCombinedRec.VARIANT_FIRST, variantNameFirst.toString());
			rec.put(AuAfCombinedRec.VARIANT_LAST, variantNameLast.toString());
			
			//clearout all stringbuffers
			variantNameInit.delete(0, variantNameInit.length());
			variantNameFirst.delete(0,variantNameFirst.length());
			variantNameLast.delete(0, variantNameLast.length());

	}
	
	//SUBJABBR LIST
	public void prepareSubjabbr(String classification_Subjabbr)
	{
		StringBuffer subjabbrCode = new StringBuffer();
		String[]  single_subjabbr;
		
		String [] subjabbrs = classification_Subjabbr.split(Constants.AUDELIMITER);
		for(int i=0; i<subjabbrs.length; i++)
		{
			single_subjabbr = subjabbrs[i].split(Constants.IDDELIMITER);
			subjabbrCode.append(single_subjabbr[1]);
			if(i< subjabbrs.length -1)
				subjabbrCode.append(Constants.IDDELIMITER);
		}
			
		rec.put(AuAfCombinedRec.SUBJECT_CLUSTER, subjabbrCode.toString());
		// clearout subjabbr
		subjabbrCode.delete(0, subjabbrCode.length());
		
	}
	
	//JOURNALS
	public void prepareSourceTitle(String journals)
	{
		String [] author_Journals = null;
		String author_Journal = null;
		String [] singleJournal = null;

		StringBuffer sourceTitles = new StringBuffer();
		StringBuffer issn = new StringBuffer();

		author_Journals = journals.split(Constants.AUDELIMITER);
		for(int i=0; i<author_Journals.length; i++)
			{
				author_Journal = author_Journals[i].trim();
				if(author_Journal !=null && !(author_Journal.isEmpty()))
				{
					singleJournal = author_Journal.split(Constants.IDDELIMITER);

					if(singleJournal.length>1 && singleJournal[1] !=null)
					{
						sourceTitles.append(DataLoadDictionary.mapUnicodeEntity(singleJournal[1]));
					}
					else if(singleJournal.length>2 && singleJournal[2] !=null )
					{
						sourceTitles.append(DataLoadDictionary.mapUnicodeEntity(singleJournal[2]));
					}
					
					if(singleJournal.length>3 && singleJournal[3] !=null)
					{
						issn.append(DataLoadDictionary.mapUnicodeEntity(singleJournal[3]));
					}
					
					if(i<author_Journals.length -1)
					{
						sourceTitles.append(Constants.IDDELIMITER);
						issn.append(Constants.IDDELIMITER);
					}
						
				}
			}
		
		rec.put(AuAfCombinedRec.SOURCE_TITLE, sourceTitles.toString());
		rec.put(AuAfCombinedRec.ISSN, issn.toString());
		
		//clearout stringbuffers
		sourceTitles.delete(0, sourceTitles.length());
		issn.delete(0, issn.length());
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
	
	//private void prepareCurrentAffiliation(String parentAffId, String current_deptId, Connection con)
	private void prepareCurrentAffiliation(String parentAffId, String current_deptId)
	{
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rsDept = null;
		
		try
		{
			stmt = con.createStatement();
			String query = "select * from AUTHOR_AFF where AFFID='" + parentAffId + "'";
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				//PREFERRED_NAME
				if(rs.getString("PREFERED_NAME") !=null)
				{
					auaf.setAffiliationPreferredName(DataLoadDictionary.mapUnicodeEntity(rs.getString("PREFERED_NAME")));
				}
				
				//NAME_VARIANT
				String affNameVariants = getStringFromClob(rs.getClob("NAME_VARIANT"));
				
				if(affNameVariants !=null)
				{
					auaf.setParentAffiliationsNameVariant(DataLoadDictionary.mapUnicodeEntity(affNameVariants));
				}
				
				// DISPLAY_NAME
				if(rs.getString("AFDISPNAME") !=null)
				{
					auaf.setAffiliationDisplayName(DataLoadDictionary.mapUnicodeEntity(rs.getString("AFDISPNAME")));
				}
				
				//DISPLAY_CITY
				if(rs.getString("CITY") !=null)
				{
					auaf.setAffiliationDisplayCity(rs.getString("CITY"));
				}
				
				//DISPLAY_CITY_GROUP
				if(rs.getString("CITYGROUP") != null)
				{
					auaf.setAffiliationDisplayCity(rs.getString("CITYGROUP"));
				}
				
				//DISPLAY_COUNTRY
				if(rs.getString("COUNTRY") !=null)
				{
					auaf.setAffiliationDisplayCountry(rs.getString("COUNTRY"));
				}
				
				//CURRENT NAMEID
				if(rs.getString("AFNAMEID") !=null)
				{
					auaf.setAffiliationNameId(DataLoadDictionary.mapUnicodeEntity(rs.getString("AFNAMEID")));
				}
				//TEMP COMMENT FOR NOW TILL HONGRONG ADD IT TO CONVERTING PROG
				//CURRENT PARENT SORTNAME
				if(rs.getString("SORTED_NAME") !=null)
				{
					auaf.setAffiliationSortName(DataLoadDictionary.mapUnicodeEntity(rs.getString("SORTED_NAME")));
				}
					
			}
			
			//Current DepartmentID ONLY FOR DISPLAY			

			//dept id
			if(current_deptId !=null)
			{
				query = "select AFDISPNAME,CITY,COUNTRY from AUTHOR_AFF where AFFID='" + current_deptId + "'";
				rsDept = stmt.executeQuery(query);
				
				auaf.setCurrentDeptAffiliation_Id(current_deptId);
				while(rsDept.next())
				{
					if(rsDept.getString("AFDISPNAME") !=null)
					{
						auaf.setCurrentDeptAffiliation_DisplayName(DataLoadDictionary.mapUnicodeEntity(rsDept.getString("AFDISPNAME")));
					}
					if(rsDept.getString("CITY") !=null)
					{
						auaf.setCurrentDeptAffiliation_City(rsDept.getString("CITY"));
					}
					if(rsDept.getString("COUNTRY") !=null)
					{
						auaf.setCurrentDeptAffiliation_Country(rsDept.getString("COUNTRY"));
					}
				}
			}

		}
		catch(SQLException ex)
		{
			System.out.println("Error Occurred reading from Author_Aff for Parent affid: " + parentAffId + " ... " + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if(rs!=null)
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
			if(rsDept !=null)
			{
				try
				{
					rsDept.close();
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
			
		}	
	}
	
	
	private void prepareHistoryAffiliationIds(String afhistids)
	{	
		affiliation_historyIds_List= new LinkedHashSet<String>();
		String[] affiliation_historyIds = afhistids.split(Constants.AUDELIMITER);
		String[] singleAffiliation_history = null;
		String[] singleAffiliation = null;
		
		for(int i=0; i<affiliation_historyIds.length;i++)
		{
			if(affiliation_historyIds[i] !=null && !(affiliation_historyIds[i].trim().isEmpty()))
			{
				singleAffiliation_history = affiliation_historyIds[i].split(Constants.IDDELIMITER);
				for(int j=0;j<singleAffiliation_history.length;j++)
				{
					if(singleAffiliation_history[j] !=null && !(singleAffiliation_history[j].trim().isEmpty()))
					{
						singleAffiliation = singleAffiliation_history[j].split(";");
						
						if(singleAffiliation[1] !=null && singleAffiliation[1].equalsIgnoreCase("parent"))
						{
							if(!(affiliation_historyIds_List.contains(singleAffiliation[0].substring(singleAffiliation[0].indexOf(":")+1, singleAffiliation[0].length()))))
							{
								affiliation_historyIds_List.add(singleAffiliation[0].substring(singleAffiliation[0].indexOf(":")+1, singleAffiliation[0].length()));
							}
							
						}
					}
				}
			}
		}
		
		//cafe duplicate current affiliation in affiliation history, so in this case ignore the one in history
		if(affiliation_historyIds_List.contains(auaf.getAffiliationId()))
			affiliation_historyIds_List.remove(auaf.getAffiliationId());
		
	}
	
	//Historical Affiliations
	//private void prepareHistoryAffiliation(Connection con)
	private void prepareHistoryAffiliation()
	{
		ResultSet rs = null;
		Statement stmt = null;
		StringBuffer parent_histaffids = new StringBuffer();
		
		int i=0;
		
		for(String afhistid: affiliation_historyIds_List)
		{
			parent_histaffids.append("'"+afhistid+"'");
			i++;
			if(i<affiliation_historyIds_List.size()-1)
				parent_histaffids.append(",");	
		}
	
		try
		{
			stmt = con.createStatement();
			String query = "select * from AUTHOR_AFF where AFFID in ("+ parent_histaffids + ")";
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				
				//HISTORY_AFFILIATION_ID
				if(rs.getString("AFFID") !=null)
				{
					auaf.setHistoryAffid(rs.getString("AFFID"));
				}
				
				//HISTORY_DISPLAY_NAME
				if(rs.getString("AFDISPNAME") !=null)
				{
					auaf.setHistoryDisplayName(DataLoadDictionary.mapUnicodeEntity(rs.getString("AFDISPNAME")));
				}
				
				//HISTORY_CITY
				if(rs.getString("CITY") !=null)
				{
					auaf.setHistoryCity(rs.getString("CITY"));
				}
				
				//HISTORY_CITYGROUP
				if(rs.getString("CITYGROUP") !=null)
				{
					auaf.setHistoryCity(rs.getString("CITYGROUP"));
				}
				
				//HISTORY_COUNTRY
				if(rs.getString("COUNTRY") !=null)
				{
					auaf.setHistoryCountry(rs.getString("COUNTRY"));
				}
				
				//PARENTS_AFFILIATION_PREFERRED_NAME
				if(rs.getString("PREFERED_NAME") !=null)
				{
					auaf.setParentAffiliationsPreferredName(DataLoadDictionary.mapUnicodeEntity(rs.getString("PREFERED_NAME")));
				}
				
				//PARENT_AFFILIATION_NAME_VARIANT
				String affNameVariants = getStringFromClob(rs.getClob("NAME_VARIANT"));
				
				if(affNameVariants !=null)
				{
					auaf.setParentAffiliationsNameVariant(DataLoadDictionary.mapUnicodeEntity(affNameVariants));
				}
				
				//PARENT_AFFILIATION_NAMEID

				if(rs.getString("AFNAMEID") !=null)
				{
					auaf.setAffiliationNameId(DataLoadDictionary.mapUnicodeEntity(rs.getString("AFNAMEID")));
				}
			}
		}
		catch(SQLException ex)
		{
			System.out.println("Error Occurred reading from History Affiliations ... " + ex.getMessage());
			ex.printStackTrace();
		}
		
		finally
		{
			if(rs!=null)
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
			
		}	
	}
	
	
	private void getDeletionList(ResultSet rs)
	{
		auId_deletion_list = new LinkedHashSet<String>();
		try {
			while (rs.next())
			{
				if(rs.getString("M_ID") !=null)
				{
					auId_deletion_list.add(rs.getString("M_ID"));
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Total Aff records to be deleted from S3 & ES: " + auId_deletion_list.size());
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
