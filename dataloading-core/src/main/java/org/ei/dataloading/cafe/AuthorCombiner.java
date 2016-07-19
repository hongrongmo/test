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
	static String operation = "new";

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
	
	AuAfCombinedRec rec;
	
	
	public static void main(String args[])
	{
		if(args.length >8)
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
				operation = args[8];
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
			writer.init();


			AuthorCombiner c = new AuthorCombiner();
			Connection con = c.getConnection(url,driver,username,password);
		
						
			if(loadNumber ==1)
			{
				c.writeCombinedByTable(con);
			}
			else
			{
				c.writeCombinedByWeekNumber(con);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// extract whole Author Table

	public void writeCombinedByTable(Connection con) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			String query = "select * from " +  tableName + " where authorid in (select authorid from " + metadataTableName + " where dbase='cpx')";
			System.out.println("query");

			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + tableName);
			writeRecs(rs,con);
			System.out.println("Wrote records.");
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


	public void writeCombinedByWeekNumber(Connection con) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		
		String query=null;
		int updateNumber;
		
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			if(!(operation.isEmpty()) && operation.equalsIgnoreCase("new"))
			{
				query = "select * from " +  tableName + " where loadnumber=" + loadNumber + " and authorid in (select AUTHORID from " + metadataTableName + " where dbase='cpx')";
			}
			else if(!(operation.isEmpty()) && operation.equalsIgnoreCase("update"))
			{
				updateNumber=loadNumber;
				query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and authorid in (select AUTHORID from " + metadataTableName + " where dbase='cpx')";
			}
			
			System.out.println(query);

			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + tableName);
			writeRecs(rs,con);
			System.out.println("Wrote records.");
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
						rec.put(AuAfCombinedRec.PREFERRED_INI, rs.getString("INITIALS"));
					}
					
					//PREFERRED_FIRST
					if(rs.getString("GIVENNAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_FIRST, rs.getString("GIVENNAME"));
					}
					
					//PREFERRED_LAST
					if(rs.getString("SURENAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_LAST, rs.getString("SURENAME"));
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

					if(rs.getString("E_ADDRESS") !=null)
					{
						email = ((String[])rs.getString("E_ADDRESS").split(Constants.IDDELIMITER))[1];
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
						prepareCurrentAffiliation(auaf.getAffiliationId(), currentdept_affid, con);
					}
					
					
					
				//Historical PARENT AFFILIATION INFO
					String history_affiliationIds = getStringFromClob(rs.getClob("HISTORY_AFFILIATIONID"));
					if(history_affiliationIds !=null)
					{
						prepareHistoryAffiliationIds(history_affiliationIds);
						if(affiliation_historyIds_List.size() >0)
						{
							prepareHistoryAffiliation(con);
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
					rec.put(AuAfCombinedRec.PARENT_AFFILIATION_ID,auaf.getParentAffiliationsId());
					
					//CURRENT_AND_HISTORY PARENT PREFERRED_NAME
					rec.put(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, auaf.getParentAffiliationsPreferredName());
					
					//CURRENT_AND_HOSTORY PARENT NAME_VARIANT
					rec.put(AuAfCombinedRec.AFFILIATION_VARIANT_NAME, auaf.getParentAffiliationsNameVariant());
					
					//CURRENT_AND_HISTORY PARENT CITY
					rec.put(AuAfCombinedRec.CITY, auaf.getAffiliationCity());
					
					//CURRENT_AND_HISTORY PARENT COUNTRY
					rec.put(AuAfCombinedRec.COUNTRY, auaf.getAffiliationCountry());
					
					//CURRENT_AND_HISTORY PARENT NAMEID
					rec.put(AuAfCombinedRec.NAME_ID, auaf.getAffiliationNameId());
					
					//CURRENT DEPT AFFILIATION_ID
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID, auaf.getCurrentDeptAffiliation_Id());
					
					//CURRENT DEPT AFFILIATION DISPLAY_NAME
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME, auaf.getCurrentDeptAffiliation_DisplayName());
					
					//CURRENT DEPT AFFILIATION CITY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY, auaf.getCurrentDeptAffiliation_City());
					
					//CURRENT DEPT AFFILIATION COUNTRY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY, auaf.getCurrentDeptAffiliation_Country());
				}

				writer.writeAuRec(rec);
				auaf=null;
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
							variantNameInit.append(singleVarainat[0]);
						}
						if(singleVarainat.length>2 && singleVarainat[2] != null)
						{
							variantNameLast.append(singleVarainat[2]);
						}
						if(singleVarainat.length>3 && singleVarainat[3] !=null)
						{
							variantNameFirst.append(singleVarainat[3]);
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
						sourceTitles.append(singleJournal[1]);
					}
					if(singleJournal.length>2 && singleJournal[2] !=null )
					{
						sourceTitles.append(singleJournal[2]);
					}
					
					if(singleJournal.length>3 && singleJournal[3] !=null)
					{
						issn.append(singleJournal[3]);
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
	
	private void prepareCurrentAffiliation(String parentAffId, String current_deptId, Connection con)
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
				if(rs.getString("PREFERREDNAME") !=null)
				{
					auaf.setAffiliationPreferredName(rs.getString("PREFERREDNAME"));
				}
				
				//NAME_VARIANT
				String affNameVariants = getStringFromClob(rs.getClob("NAMEVARIAN"));
				
				if(affNameVariants !=null)
				{
					auaf.setParentAffiliationsNameVariant(affNameVariants);
				}
				
				// DISPLAY_NAME
				if(rs.getString("AFDISPNAME") !=null)
				{
					auaf.setAffiliationDisplayName(rs.getString("AFDISPNAME"));
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
					auaf.setAffiliationNameId(rs.getString("AFNAMEID"));
				}
				//TEMP COMMENT FOR NOW TILL HONGRONG ADD IT TO CONVERTING PROG
				//CURRENT PARENT SORTNAME
				if(rs.getString("SORTEDNAME") !=null)
				{
					auaf.setAffiliationSortName(rs.getString("SORTEDNAME"));
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
						auaf.setCurrentDeptAffiliation_DisplayName(rsDept.getString("AFDISPNAME"));
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
							affiliation_historyIds_List.add(singleAffiliation[0].substring(singleAffiliation[0].indexOf(":")+1, singleAffiliation[0].length()));
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
	private void prepareHistoryAffiliation(Connection con)
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
					auaf.setHistoryDisplayName(rs.getString("AFDISPNAME"));
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
				if(rs.getString("PREFERREDNAME") !=null)
				{
					auaf.setParentAffiliationsPreferredName(rs.getString("PREFERREDNAME"));
				}
				
				//PARENT_AFFILIATION_NAME_VARIANT
				String affNameVariants = getStringFromClob(rs.getClob("NAMEVARIAN"));
				
				if(affNameVariants !=null)
				{
					auaf.setParentAffiliationsNameVariant(affNameVariants);
				}
				
				//PARENT_AFFILIATION_NAMEID

				if(rs.getString("AFNAMEID") !=null)
				{
					auaf.setAffiliationNameId(rs.getString("AFNAMEID"));
				}
			}
		}
		catch(SQLException ex)
		{
			System.out.println("Error Occurred reading from History Affiliations ... " + ex.getMessage());
			ex.printStackTrace();
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
