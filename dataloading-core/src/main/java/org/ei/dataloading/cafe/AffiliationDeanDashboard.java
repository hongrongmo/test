package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import javax.json.stream.JsonGenerator;

import org.ei.common.Constants;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author TELEBH
 * @date: 05/18/2018
 * @Description: Web team developing New Service for dashboard search box & autocomplete 
 * this servive enables user to pick accurate affiliation by poping up auto complete suggestions 
 * for affiliations names while user is typing. ythe list of affiliations to pop-up has mapping with their
 * corresponding affid. but need to limit the list of affiliations to only ones 
 * 	- indexed in ES
 *  - quality >=99
 *  - doc_count >=1000
 *  
 *   Out file: JSON file with fields ( AFFID, PREFERED_NAME, NAME_VARIANT, DOC_COUNT)
 */
public class AffiliationDeanDashboard 
{

	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "";
	static int loadNumber = 0;
	static String tableName = "institute_profile";
	static String afDocCount_tableName = "affiliation_doc_count";  
	
	private Connection con;
	PrintWriter out = null;
	
	Map<String,Object> config;   //JsonBuilder config
	JsonBuilderFactory factory; //JsonBuilder Factory
	Gson gson;
	
	public AffiliationDeanDashboard()
	{
		init();
	}
	
	
	public static void main(String[] args) 
	{
		if(args.length >2)
		{
			if(args[0] !=null)
			{
				doc_type = args[0];
				if(!(doc_type.equalsIgnoreCase("ipr") || doc_type.equalsIgnoreCase("apr")))
				{
					System.out.println("Invalid document type, Re-run process with doc_type ipr or apr");
					System.exit(1);
				}
				System.out.println("doc_type: " + doc_type);
				
				if(args[1] !=null)
				{
					tableName = args[1];
					System.out.println("Table: " + tableName);
				}
				if(args[2] !=null)
				{
					afDocCount_tableName = args[2];
					System.out.println("Doc count table: " + afDocCount_tableName);
				}
			}
		}
		if(args.length >7)
		{
			if(args[3] !=null)
			{
				url = args[3];
				System.out.println(url);
			}
			if(args[4] !=null)
			{
				driver = args[4];
			}
			if(args[5] !=null)
			{
				username = args[5];
				System.out.println("Schema: " + username);
			}
			if(args[6] !=null)
			{
				password = args[6];
			}
			if(args[7] !=null)
			{
				if(Pattern.matches("^\\d*$", args[7]))
				{
					loadNumber = Integer.parseInt(args[7]);
				}
				else
				{
					System.out.println("LoadNumber has wrong format");
					System.exit(1);
				}
			}
		}
		else
		{
			System.out.println("Not Enough Parameters");
			System.exit(1);
		}
		
		AffiliationDeanDashboard afDeanDashboard = new AffiliationDeanDashboard();
		afDeanDashboard.getDocCount();
		afDeanDashboard.end();

	}
	
	private void init()
	{
		String currDir = System.getProperty("user.dir");
		File file;
		
		try
		{
			currDir = currDir + "/deandashboard";
			file =new File (currDir);
			if(!file.exists())
				file.mkdir();
			file = new File(currDir + "/affiliations-test.json");
			out = new PrintWriter(new FileWriter(file));
			out.println("{");
			out.println("\t\"affiliations\":[");
			
			config = new HashMap<String,Object>();
			config.put(JsonGenerator.PRETTY_PRINTING, true);
			factory = Json.createBuilderFactory(config);
			
			gson = new GsonBuilder().setPrettyPrinting().create();
			
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
	
	private void end()
	{
		try
		{
			if(out !=null)
			{
				
				out.write("]");
				out.println("}");
				
				out.flush();
				out.close();
			}
		}
		catch(Exception e)
		{
			System.out.println("exception at closing out file!!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void getDocCount()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query;
		
		try
		{
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			System.out.println("Running the query...");
			
			query = "select a.affid,a.PREFERED_NAME,a.NAME_VARIANT,b.doc_count from " + tableName + " a, " + afDocCount_tableName + " b "+
					"where a.es_status='indexed' and a.quality>=99 and a.affid=b.INSTITUTE_ID and b.doc_count>=100" +
					"order by b.doc_count desc";
			System.out.println(query);
			rs = stmt.executeQuery(query);
			con = getConnection(url, driver, username, password);
			writeRecs(rs);
		}
		catch(SQLException ex)
		{
			System.out.println("sql Exception: " + ex.getMessage() + " sqlstate: " + ex.getSQLState());
			ex.printStackTrace();
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void writeRecs(ResultSet rs) throws SQLException
	{
		int count = 0;
		
		AuAfCombinedRec rec;
		
		try
		{
			while(rs.next())
			{
				rec = new AuAfCombinedRec();
				
				//Name_variant
				if(rs.getString("NAME_VARIANT") !=null)
				{
					rec.put("varName", rs.getString("NAME_VARIANT"));
				}
				//AFFID
				if(rs.getString("AFFID") !=null)
				{
					rec.put("affilId", rs.getString("AFFID"));
				}
				//Prefered_name
				if(rs.getString("PREFERED_NAME")!=null)
				{
					rec.put("affilName", rs.getString("PREFERED_NAME"));
				}	
				// Doc_count
				rec.put("docCount", Integer.toString(rs.getInt("DOC_COUNT")));
				
				
				
				count ++;
				writeAfRec(rec);
				
			}
			System.out.println("Total count of AF Profiles for Dean Dashboard: " + count);
		}

		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from ResultSet for AFFID: " + rs.getString("AFFID") + " ... " + e.getMessage());
			e.printStackTrace();
		} 
		catch (Exception e) 
		{	
			e.printStackTrace();
		}
		
	}
	public void writeAfRec(AuAfCombinedRec rec) throws Exception
	{
		
		JsonObject esDocument = factory.createObjectBuilder()
						.add("varName",prepareMultiValues(notNull(rec.getString("varName"))))
						.add("affilId",notNull(rec.getString("affilId")))
						.add("affilName", notNull(rec.getString("affilName")))	
						.add("docCount",notNull(rec.getString("docCount")))
				.build();
		
		out.write(esDocument.toString() + ",");
		
	}
	
	private String notNull(String s)
    {
        String r = null;

        if (s == null)
        {
            r = "";
        }
        else
        {
            r = s;
        }

        return r;
    }
	private  JsonArray prepareMultiValues(String str)
	{
		JsonArrayBuilder builder = factory.createArrayBuilder();
		if(str !=null && !(str.isEmpty()))
		{
			String[] values = str.split(Constants.IDDELIMITER);
			for(int i=0;i<values.length;i++)
			{
				builder.add(values[i]);
			}
		}	
		return builder.build();
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
