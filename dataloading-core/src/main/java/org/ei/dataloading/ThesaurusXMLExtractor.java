package org.ei.dataloading;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

public class ThesaurusXMLExtractor
{

	private String tablename;
	private String database;
	private String username;
	private String password;
	private String driver;
	private String url;


	public static void main(String args[])
		throws Exception
	{

		String tablename = args[0];
		String database = args[1];
		String username = args[2];
		String password = args[3];
		String driver = args[4];
		String url = args[5];
		ThesaurusXMLExtractor ex = new ThesaurusXMLExtractor(tablename,
															 database,
															 username,
								  							 password,
								  							 driver,
								  							 url);
		ex.extract();
	}


	public ThesaurusXMLExtractor(String tablename,
								 String database,
								 String username,
								 String password,
								 String driver,
								 String url)
	{
		this.tablename = tablename;
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = driver;
		this.url = url;
	}


	public void extract()
		throws Exception
	{
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter out = null;

		try
		{
			out = new PrintWriter(new FileWriter(this.tablename+".xml"));
			out.println("<?xml version=\"1.0\"?>");
			out.println("<!DOCTYPE ROWSET SYSTEM \"THESROWSET.dtd\">");
			out.println("<ROWSET>");
			con = getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from "+ this.tablename);
			while(rs.next())
			{
				out.println("	<ROW>");
				int recID = rs.getInt("T_ID");
				String mainTerm = rs.getString("MAIN_TERM_SEARCH");
				String broaderTerms = rs.getString("BROADER_TERMS");
				String narrowerTerms = rs.getString("NARROWER_TERMS");
				String relatedTerms = rs.getString("RELATED_TERMS");
				String topTerms = rs.getString("TOP_TERMS");
				String priorTerms = rs.getString("PRIOR_TERMS");

				out.println("		<TDOCID>"+database+"_"+Integer.toString(recID)+"</TDOCID>");
				out.println("		<DATABASE>"+database+"</DATABASE>");
				out.println("		<SORTNUMBER>"+recID+"</SORTNUMBER>");
				out.println("		<MAINTERM><![CDATA["+mainTerm+"]]></MAINTERM>");
				out.println("		<BROADERTERMS><![CDATA["+notNull(getDelimitedString(broaderTerms))+"]]></BROADERTERMS>");
				out.println("		<NARROWERTERMS><![CDATA["+notNull(getDelimitedString(narrowerTerms))+"]]></NARROWERTERMS>");
				out.println("		<RELATEDTERMS><![CDATA["+notNull(getDelimitedString(relatedTerms))+"]]></RELATEDTERMS>");
				out.println("		<TOPTERMS><![CDATA["+notNull(getDelimitedString(topTerms))+"]]></TOPTERMS>");
				out.println("		<PRIORTERMS><![CDATA["+notNull(getDelimitedString(priorTerms))+"]]></PRIORTERMS>");
				out.println("	</ROW>");
			}
			out.println("</ROWSET>");
		}
		finally
		{
			if(rs != null)
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

			if(stmt != null)
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

			if(con != null)
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

			if(out != null)
			{
				try
				{
					out.close();
					DataValidator v = new DataValidator();
					v.validateFile(tablename+".xml");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String notNull(String s)
	{
		if(s == null)
		{
			return "";
		}
		else
		{
			return s;
		}
	}

	public String getDelimitedString(String s)
	{
		if(s == null)
		{
			return null;
		}
		StringBuffer buf = new StringBuffer("QQdelimQQ ");
		StringTokenizer tokens = new StringTokenizer(s, ";");
		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			buf.append(token);
			buf.append(" QQdelimQQ ");
		}

		return buf.toString();
	}


	private Connection getConnection()
		throws Exception
	{
		Class.forName(this.driver);
		Connection con = DriverManager.getConnection(this.url,
										  			 this.username,
										  			 this.password);
		return con;
	}
}
