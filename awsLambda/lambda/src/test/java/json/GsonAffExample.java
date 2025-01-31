package json;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonBuilderFactory;


public class GsonAffExample
{
	public static final String IDDELIMITER = new String(new char[] {31});
	
	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "";
	static int loadNumber = 0;
	static String tableName = "institute_profile";
	static String afDocCount_tableName = "affiliation_doc_count";  

	private Connection con;
	BufferedWriter bw = null;

	Map<String,Object> config;   //JsonBuilder config
	JsonBuilderFactory factory; //JsonBuilder Factory
	Gson gson;

	ObjectMapper mapper;
	
	ArrayList<AFRecord> recs;
	

	public GsonAffExample()
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

		GsonAffExample af = new GsonAffExample();
		af.getDocCount();
		af.end();
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
			FileWriter fileWriter = new FileWriter(new File(currDir + "/affiliations-test.json"));
			bw = new BufferedWriter(fileWriter);
			bw.write("{");
			//bw.write("\n \"affiliations\":[");
			bw.write("\n \"affiliations\": ");

			Map <String,Object> config = new HashMap<String,Object>();
			config.put("javax.json.stream.jsonGeneratory.prettyPrinting", Boolean.valueOf(true));
			factory = Json.createBuilderFactory(config);

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
			if(bw !=null)
			{

				//bw.write("]");
				bw.write("}");

				bw.flush();
				bw.close();
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

			//Prod
			/*query = "select count(*) from " + tableName + " a, " + afDocCount_tableName + " b where a.es_status='indexed' " +
					"and a.quality>=99 and a.affid=b.INSTITUTE_ID and b.doc_count>=1000";

		*/
		/*	query = "select a.affid,a.PREFERED_NAME,a.NAME_VARIANT,b.doc_count from " + tableName + " a, " + afDocCount_tableName + " b "+
					"where a.es_status='indexed' and a.quality>=99 and a.affid=b.AFFID and b.doc_count>=1000 " +
					"order by b.doc_count desc";*/
			
			// Commented 11/30/2020
			
			/*query = "select a.affiliation_id,a.PREFERED_NAME,a.NAME_VARIANT,b.doc_count from " + tableName + " a left outer join " + afDocCount_tableName + " b "+
					"on a.affiliation_id=b.AFFID where a.quality>=99 and b.doc_count>=1000 " +
					"order by b.doc_count desc";*/
			
			query = "select a.INSTITUTION_ID,a.institution_name,a.affiliation_id,b.PREFERED_NAME,b.name_variant,c.doc_count as doc_count, "
					+ "a.INSTITUTION_COUNTRY,a.INSTITUTION_CONTINENT from " + tableName + " a, institute_profile b, " + afDocCount_tableName + " c "
					+ " where (a.affiliation_id=b.affid and a.affiliation_id=c.affid) and b.quality>=99 "
					+ "	order by a.INSTITUTION_NAME asc,c.doc_count desc";
			
			
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
	public void writeRecs(ResultSet rs) throws SQLException
	{
		AFRecord rec;
		int count =0;
		recs = new ArrayList<AFRecord>();

		try
		{
			while(rs.next())
			{
				rec = new AFRecord();  

				// for method #1 & 2

				//Name_variant
				if(rs.getString("NAME_VARIANT") !=null)
				{
					rec.setVarName(prepareMultiValues(rs.getString("NAME_VARIANT")));
				}
				//AFFID
				if(rs.getString("AFFILIATION_ID") !=null)
				{
					rec.setAffilId(rs.getString("AFFILIATION_ID"));
				}
				//Prefered_name
				if(rs.getString("PREFERED_NAME")!=null)
				{
					rec.setAffilName(rs.getString("PREFERED_NAME"));
				}	
				// Doc_count
				rec.setDocCount(rs.getString("DOC_COUNT"));



				count ++;
				//writeAfRec(rec);
				recs.add(rec);

			}
			writeAfRecs();
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

	public void writeAfRec(AFRecord rec)   // for method #1 &2

	{

		//method #2, same as what Mohan wants
		try {
			ObjectMapper mapper = new ObjectMapper();

			bw.append(mapper.defaultPrettyPrintingWriter().writeValueAsString(rec) + ", ");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}


	public void writeAfRecs() 

	{

		//method #2, same as what Mohan wants
		try {
			ObjectMapper mapper = new ObjectMapper();
			bw.append(mapper.defaultPrettyPrintingWriter().writeValueAsString(recs));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}



	}
	
	// for method #1 & 2

	private  List<String> prepareMultiValues(String str)
	{
		List<String> valuesList =new ArrayList<String>();

		if(str !=null && !(str.isEmpty()))
		{
			String[] values = str.split(IDDELIMITER);
			for(int i=0;i<values.length;i++)
			{
				valuesList.add(values[i]);
			}
		}	
		return valuesList;
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

class AFRecord
{
	List<String> varName;
	String affilId;
	String affilName;
	String docCount;

	public List<String> getVarName() 
	{
		return varName;
	}
	public void setVarName(List<String> varName) 
	{
		this.varName = varName;
	}
	public String getAffilId() 
	{
		return affilId;
	}
	public void setAffilId(String affilId) 
	{
		this.affilId = affilId;
	}
	public String getAffilName() 
	{
		return affilName;
	}
	public void setAffilName(String affilName) {
		this.affilName = affilName;
	}
	public String getDocCount() 
	{
		return docCount;
	}
	public void setDocCount(String docCount) 
	{
		this.docCount = docCount;
	}



}

