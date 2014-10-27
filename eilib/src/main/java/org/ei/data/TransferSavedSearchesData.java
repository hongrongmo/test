package org.ei.data;

import java.io.FileWriter;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ei.connectionpool.ConnectionBroker;

/* this is controll file for sql loader(searches.ctl)
LOAD DATA
APPEND
INTO TABLE SEARCHES_SAVED
FIELDS TERMINATED BY '|'
TRAILING NULLCOLS
(
                SEARCH_ID               CHAR(64),
                SESSION_ID              CHAR(64),
                USER_ID                 CHAR(64),
                MASK                    CHAR(32),
                SEARCH_TYPE             CHAR(32),
                SAVE_DATE               DATE 'YYYY-MM-DD',
                ACCESS_DATE             DATE 'YYYY-MM-DD',
                EMAIL_ALERT             CHAR(3),
                SAVED                   CHAR(3),
                VISIBLE                 CHAR(3),
                EMAILALERTWEEK          CHAR(6),
                SEARCH_PHRASE_1         CHAR(4000),
                SEARCH_OPTION_1         CHAR(64),
                BOOLEAN_1               CHAR(3),
                SEARCH_PHRASE_2         CHAR(256),
                SEARCH_OPTION_2         CHAR(64),
                BOOLEAN_2               CHAR(3),
                SEARCH_PHRASE_3         CHAR(256),
                SEARCH_OPTION_3         CHAR(64),
                RESULTS_COUNT           CHAR(32),
                SUBCOUNTS               CHAR(256),
                LANGUAGE                CHAR(32),
                START_YEAR              CHAR(4),
                END_YEAR                CHAR(4),
                AUTOSTEMMING            CHAR(3),
                SORT_OPTION             CHAR(32),
                SORT_DIRECTION          CHAR(8),
                DISPLAY_QUERY           CHAR(4000),
                DOCUMENT_TYPE           CHAR(32),
                TREATMENT_TYPE          CHAR(32),
                DISCIPLINE_TYPE         CHAR(32),
                LASTUPDATES             CHAR(32),
                DUPSET                  CHAR(4000),
                DEDUP                   CHAR(5),
                DEDUPDB                 CHAR(3),
                REFINE_STACK            CHAR(1024),
                RESULTS_STATE           CHAR(256)
 )
*/

/* this is sql loader file
sqlldr userid=ev_session/team control=searches.ctl log=search.log bad=search.bad errors=500000 data=searches.dat
*/

public class TransferSavedSearchesData
{
	private static String url = null;
	private final static String username = "ev_session";
	private final static String password = "team";
	private final static String driver = "oracle.jdbc.driver.OracleDriver";
	public static void main(String [] args)
	{
		TransferSavedSearchesData sData = new TransferSavedSearchesData();
		if(args.length>0)
		{
			url = args[0];
			if(args.length>1)
				sData.getSavedSeaches(args[1]);
			else
				sData.getSavedSeaches();
		}
		else
		{
			System.out.print("Please enter Oracle database url");
			System.exit(1);
		}
	}

	public void getSavedSeaches()
	{
		String name = "/temp/savedSearchesData.txt";
		getSavedSeaches(name);
	}

	public void getSavedSeaches(String fileName)
	{
		ConnectionBroker broker = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Map dataMap = null;
		FileWriter out = null;
		String queryString = null;
		String accessDate = null;
		Clob clob=null;
		String ecodeString = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>";
		try
		{
			out = new FileWriter(fileName);
			con = getDbCoonection(url,username,password,driver);
			pstmt = con.prepareStatement("SELECT * FROM SAVED_SEARCHES");
			rset = pstmt.executeQuery();
			while (rset.next())
			{
				dataMap = new HashMap();
				dataMap.put("SEARCH-ID",rset.getString("SEARCH_ID"));
				dataMap.put("USER-ID",rset.getString("USER_ID"));
				dataMap.put("SAVED","On");
				dataMap.put("SAVED-DATE",rset.getDate("SAVE_DATE"));
				if((rset.getString("EMAIL_ALERT")!=null)&&(rset.getString("EMAIL_ALERT").equals("001")))
				{
					dataMap.put("EMAIL-ALERT","On");
				}
				else
				{
					dataMap.put("EMAIL-ALERT","Off");
				}
				clob = rset.getClob("QUERY_STRING");
				queryString = clob.getSubString(1,(int)clob.length());
				queryString = ecodeString+queryString;
				accessDate = (rset.getString("ACCESS_DATE"));
				if(accessDate !=null)
				{
					dataMap.put("ACCESS-DATE",accessDate.substring(0,10));
				}
				SavedSearchesParser sParser= new SavedSearchesParser(dataMap);
				sParser.parseQueryString(queryString);
				outputData(dataMap,out);
			}
			out.flush();
		}
		catch (Exception sqle)
		{
			System.out.println("Exception from TransferSavedSearchesData "+sqle);
		}
		finally
		{
			if(out!=null)
			{
				try
				{
					out.close();
				}
				catch(Exception e)
				{}
			}
			if (rset != null)
			{
				try
				{
					rset.close();
					rset = null;
				}
				catch (Exception e)
				{
				}
			}
			if (pstmt != null)
			{
				try
				{
					pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
				}
			}
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (Exception e)
				{
				}
			}
		}
    }

    private void outputData(Map dataMap,Writer out)
    {
		try
		{
			out.write(((dataMap.get("SEARCH-ID")==null)?"":dataMap.get("SEARCH-ID"))+"|");
			out.write(((dataMap.get("SEARCH-ID")==null)?"":dataMap.get("SEARCH-ID"))+"|");//for session_id
			out.write(((dataMap.get("USER-ID")==null)?"":dataMap.get("USER-ID"))+"|");
			String database = null;
			if(dataMap.get("ID") != null)
			{
				String did = (String)dataMap.get("ID");

				if(did.equalsIgnoreCase("cpx"))
				{
					database ="1";
				}
				else if(did.equalsIgnoreCase("inspec"))
				{
					database = "2";
				}
				else if(did.equalsIgnoreCase("com"))
				{
					database = "3";
				}
				else if(did.equalsIgnoreCase("uspto"))
				{
					database = "8";
				}
				else if(did.equalsIgnoreCase("crc"))
				{
					database = "16";
				}

			}
			else
			{
				database = (String)dataMap.get("DATABASE-MASK");
			}
			out.write(((database==null)?"":database)+"|");
			out.write(((dataMap.get("SEARCH-TYPE")==null)?"":dataMap.get("SEARCH-TYPE"))+"|");
			out.write(((dataMap.get("SAVED-DATE")==null)?"":dataMap.get("SAVED-DATE"))+"|");
			out.write(((dataMap.get("ACCESS-DATE")==null)?"":dataMap.get("ACCESS-DATE"))+"|");
			out.write(((dataMap.get("EMAIL-ALERT")==null)?"":dataMap.get("EMAIL-ALERT"))+"|");
			out.write(((dataMap.get("SAVED")==null)?"":dataMap.get("SAVED"))+"|");
			out.write("|");//visible
			out.write(((dataMap.get("EMAILALERTWEEK")==null)?"":dataMap.get("EMAILALERTWEEK"))+"|");
			out.write(((dataMap.get("SEARCH-PHRASE-1")==null)?"":dataMap.get("SEARCH-PHRASE-1"))+"|");
			out.write(((dataMap.get("SEARCH-OPTION-1")==null)?"":dataMap.get("SEARCH-OPTION-1"))+"|");
			out.write(((dataMap.get("BOOLEAN-1")==null)?"":dataMap.get("BOOLEAN-1"))+"|");
			out.write(((dataMap.get("SEARCH-PHRASE-2")==null)?"":dataMap.get("SEARCH-PHRASE-2"))+"|");
			out.write(((dataMap.get("SEARCH-OPTION-2")==null)?"":dataMap.get("SEARCH-OPTION-2"))+"|");
			out.write(((dataMap.get("BOOLEAN-2")==null)?"":dataMap.get("BOOLEAN-2"))+"|");
			out.write(((dataMap.get("SEARCH-PHRASE-3")==null)?"":dataMap.get("SEARCH-PHRASE-3"))+"|");
			out.write(((dataMap.get("SEARCH-OPTION-3")==null)?"":dataMap.get("SEARCH-OPTION-3"))+"|");
			out.write(((dataMap.get("RESULTS-COUNT")==null)?"":dataMap.get("RESULTS-COUNT"))+"|");
			out.write(((dataMap.get("SC")==null)?"":dataMap.get("SC"))+"|");
			out.write(((dataMap.get("LANGUAGE")==null)?"":dataMap.get("LANGUAGE"))+"|");
			out.write(((dataMap.get("START-YEAR")==null)?"":dataMap.get("START-YEAR"))+"|");
			out.write(((dataMap.get("END-YEAR")==null)?"":dataMap.get("END-YEAR"))+"|");
			String autostemming=((dataMap.get("AUTOSTEMMING")==null)?"":dataMap.get("AUTOSTEMMING")).toString();
			if(autostemming.length()>3)
			{
				autostemming = autostemming.substring(0,3);
			}
			if(autostemming.equalsIgnoreCase("on"))
			{
				autostemming = "on";
			}
			else
			{
				autostemming = "off";
			}
			out.write(autostemming+"|");

			//out.write(((dataMap.get("SORT-OPTION")==null)?"":dataMap.get("SORT-OPTION"))+"|");
			String sortOption = (String)dataMap.get("SORT-OPTION");
			if(sortOption==null)
			{
				out.write("|");
			}
			else if(sortOption.equalsIgnoreCase("publicationYear"))
			{
				out.write("yr|");
			}
			else
			{
				out.write(dataMap.get("SORT-OPTION")+"|");
			}
			out.write("|");//sort-direction
			out.write(((dataMap.get("DISPLAY-QUERY")==null)?"":dataMap.get("DISPLAY-QUERY"))+"|");
			out.write(((dataMap.get("DOCUMENT-TYPE")==null)?"":dataMap.get("DOCUMENT-TYPE"))+"|");
			out.write(((dataMap.get("TREATMENT-TYPE")==null)?"":dataMap.get("TREATMENT-TYPE"))+"|");
			out.write(((dataMap.get("DISCIPLINE-TYPE")==null)?"":dataMap.get("DISCIPLINE-TYPE"))+"|");
			String lastUpdate = (String)dataMap.get("LASTFOURUPDATES");
			if(lastUpdate != null && lastUpdate.length()>0)
			{
				out.write("4|");//lastFourUpdate
			}
			else
			{
				out.write("|");//lastFourUpdate
			}

			out.write("|");//dupset
			out.write("|");//dedup
			out.write("|");//dedupdb
			out.write("|");//refine-stack
			out.write("|");//results_state
			out.write("\n");
		}
		catch(Exception e)
		{
			System.out.println("Exception from outputDate "+e);
		}
	}

	public static Connection getDbCoonection(String url,String username,String password, String driver)
	    throws Exception
	{
	    Class.forName(driver).newInstance();
	    Connection con  = DriverManager.getConnection(url, username, password);
	    return con;
  	}
}
