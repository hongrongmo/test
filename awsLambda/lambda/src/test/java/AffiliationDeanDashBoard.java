import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.stream.JsonGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.ei.common.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author TELEBH
 * @Description: intial non-complete of creating Affiliations DeanDashboard, 
 * already done by Class "GsonAffExample.java
 */

public class AffiliationDeanDashBoard {

	static String doc_type;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "";
	static int loadNumber = 0;
	static String tableName = "institute_profile";
	static String afDocCount_tableName = "affiliation_doc_count";  
	
	Logger logger;
	
	private Connection con = null;
	private PrintWriter out = null;
	
	private Map<Integer,InstitutionRecord> institutionsList;
	
	private Map<String,Object> config;   //JsonBuilder config
	private JsonBuilderFactory factory; //JsonBuilder Factory
	private Gson gson;
	
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
		
		// Old way
		/*
		 * AffiliationDeanDashBoard af = new AffiliationDeanDashBoard();
		 * af.getDocCount(); af.end();
		 */
		
		//New way 12/18/2020
		AffiliationDeanDashBoard af = new AffiliationDeanDashBoard();
		af.getInstitutionsRecs();
		af.writeInstitutionJsonRecs();
		
	}
	
	public AffiliationDeanDashBoard()
	{
		logger = Logger.getLogger(AffiliationDeanDashBoard.class);
		institutionsList = new LinkedHashMap<>();
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
					"where a.es_status='indexed' and a.quality>=99 and a.affid=b.affid and b.doc_count>=1000 and rownum<7" +
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
		
		JsonObject rec;
		
		try
		{
			while(rs.next())
			{
				rec = new JsonObject();
				
				//Name_variant
				if(rs.getString("NAME_VARIANT") !=null)
				{
					rec.addProperty("varName", rs.getString("NAME_VARIANT"));
				}
				//AFFID
				if(rs.getString("AFFID") !=null)
				{
					rec.addProperty("affilId", rs.getString("AFFID"));
				}
				//Prefered_name
				if(rs.getString("PREFERED_NAME")!=null)
				{
					rec.addProperty("affilName", rs.getString("PREFERED_NAME"));
				}	
				// Doc_count
				rec.addProperty("docCount", Integer.toString(rs.getInt("DOC_COUNT")));
				
				
				
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
	
	public void writeAfRec(JsonObject rec)
	{
		
	}
	
	public void getInstitutionsRecs()
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			con = getConnection(url, driver, username, password);
			
			String query = "select INSTITUTION_ID, INSTITUTION_NAME , AFFILIATION_ID, AFFILIATION_NAME, PREFERED_NAME, NAME_VARIANT, DOC_COUNT, INSTITUTION_CONTINENT, "
					+ "INSTITUTION_COUNTRY,INST_COUNT from hh_deandashboard_2020 where INST_COUNT >=? order by INSTITUTION_NAME asc";
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, 1000);
			
			rs = pstmt.executeQuery();
			InstitutionRecord instRec;
			
			while(rs.next())
			{
				Affiliation aff = new Affiliation();
				
				// ONly add INstitution when it has ID
				if(rs.getInt("INSTITUTION_ID") != 0)
				{
					int instId = rs.getInt("INSTITUTION_ID");
					if(! institutionsList.containsKey(instId))
					{
						instRec = new InstitutionRecord();
					}
					else
					{
						//System.out.println("InstId ALready Exist");
						instRec = institutionsList.get(instId);
					}
					
					instRec.setInstitution_ID(rs.getInt("INSTITUTION_ID"));
					
					
					if(rs.getString("INSTITUTION_NAME") != null)
						instRec.setInstitutionName(rs.getString("INSTITUTION_NAME"));
					
					if(rs.getInt("AFFILIATION_ID") != 0)
						aff.setAffiliationId(rs.getInt("AFFILIATION_ID"));
					
					if(rs.getString("AFFILIATION_NAME") != null)
						aff.setAffiliationName(rs.getString("AFFILIATION_NAME"));
					
					if(rs.getString("PREFERED_NAME") != null)
						aff.setAffiliationPreferedName(StringEscapeUtils.unescapeHtml4(rs.getNString("PREFERED_NAME")));
					
					if(rs.getString("NAME_VARIANT") != null && !rs.getNString("NAME_VARIANT").isEmpty())
					{
						String[] nameVariants = rs.getString("NAME_VARIANT").split(Constants.IDDELIMITER);
						if(nameVariants.length >=1)
						{
							for(String namevar: nameVariants)
								aff.setAffiliationNameVariants(StringEscapeUtils.unescapeHtml4(namevar));
						}
					}
					
					if(rs.getInt("DOC_COUNT") != 0)
						aff.setAffiliationDocCount(rs.getInt("DOC_COUNT"));
					
					// add Affiliation Info to Institution
					instRec.setAffiliationInfo(aff);
					
					
					if(rs.getString("INSTITUTION_CONTINENT") != null)
						instRec.setContinent(rs.getNString("INSTITUTION_CONTINENT"));
					
					if(rs.getNString("INSTITUTION_COUNTRY") != null)
						instRec.setCountry(rs.getString("INSTITUTION_COUNTRY"));
					
					if(rs.getInt("INST_COUNT") != 0)
						instRec.setInstitutionDocCount(rs.getInt("INST_COUNT"));
					
					
					institutionsList.put(rs.getInt("INSTITUTION_ID"), instRec);
				}
			}
			
		}
		catch(SQLException ex)
		{
			logger.error("SQL Exception happened pulling IPR data");
			logger.error(ex.getCause());
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			// close resultset, statement & connection
			close(rs);
			close(pstmt);
			close(con);
			
		}
	}
	
	public void writeInstitutionJsonRecs()
	{
		if(institutionsList.size() >0)
		{
			JSONObject institutionDetails = new JSONObject(); 
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File("deandashboard_2020.json"))))
			{
				for(Map.Entry<Integer, InstitutionRecord> entry: institutionsList.entrySet())
				{
					JSONObject institution = new JSONObject();
					InstitutionRecord instRec = entry.getValue();
					int instId = instRec.getInstitution_ID();
					String instName = instRec.getInstitutionName();
					int instDocCount = instRec.getInstitutionDocCount();
					String instCountry = instRec.getCountry();
					String instContinent = instRec.getContinent();
					
					if(instId != 0)
						institution.put("institutionId", instId);
					
					if(!instName.isEmpty())
						institution.put("institutionName", instName);
					
					if(instDocCount != 0)
						institution.put("institutionDocCount", instDocCount);
					
					if(!instCountry.isEmpty())
						institution.put("institutionCountry", instCountry);
					
					if(!instContinent.isEmpty())
						institution.put("institutionContinent", instContinent);
					
					JSONArray affiliations = new JSONArray();
					for(Affiliation affRec: instRec.getAffiliationInfo())
					{
						JSONObject aff = new JSONObject();
						
						if(affRec.getAffiliationId() != 0)
							aff.put("affilId", affRec.getAffiliationId());
						
						if(!affRec.getAffiliationName().isEmpty())
							aff.put("affilName", affRec.getAffiliationName());
						
						if(!affRec.getAffiliationPreferedName().isEmpty())
							aff.put("affilPrefName", affRec.getAffiliationPreferedName());
						
						if(affRec.getAffiliationDocCount() != 0)
							aff.put("docCount", affRec.getAffiliationDocCount());
						
						
						JSONArray affNameVars = new JSONArray();
						if(affRec.getAffiliationNameVariants().size() >0)
						{
							for(String str: affRec.getAffiliationNameVariants())
							{
								if(!str.isEmpty())
									affNameVars.add(str);
							}
						}
						
						aff.put("varNames", affNameVars);
						
						affiliations.add(aff);
					}
					institution.put("affiliations", affiliations);
					
					institutionDetails.put(instId, institution);
				}
				Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
				JsonParser jparser = new JsonParser();
				JsonElement jelement = jparser.parse(institutionDetails.toString());
				String jsonPrettyPrint = gson.toJson(jelement);
				
				
				//Write Institution to JSON FILE
				writer.write(jsonPrettyPrint);
				writer.flush();
			}
	
			catch(IOException ex)
			{
				System.out.println("IOEx. Failed to write to json file");
				System.out.println("Error: " + ex.getMessage());
				ex.printStackTrace();
			}
			catch(Exception ex)
			{
				System.out.println("Failed to write to json file");
				System.out.println("Error: " + ex.getMessage());
				ex.printStackTrace();
			}
			
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

	/**
	 * @return
	 */
	private void close(ResultSet rs)
	{
		try
		{
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 */
	private void close(Statement stmt)
	{
		try
		{
			if(stmt != null)
				stmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	private void close(Connection con)
	{
		try
		{
			if(con != null)
				con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

class InstitutionRecord
{
	private Integer institution_ID;
	private String institutionName;
	private Integer institutionDocCount;
	private String continent;
	private String country;
	private List<Affiliation> affiliationInfo;
	
	
	public InstitutionRecord()
	{
		affiliationInfo = Collections.synchronizedList(new LinkedList<Affiliation>());
		//affiliationInfo = new LinkedHashMap<>();
	}
	
	//Setters & Getters
	
	public Integer getInstitution_ID() {
		return institution_ID;
	}


	public void setInstitution_ID(Integer institution_ID) {
		this.institution_ID = institution_ID;
	}


	public String getInstitutionName() {
		return institutionName;
	}


	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}


	public Integer getInstitutionDocCount() {
		return institutionDocCount;
	}


	public void setInstitutionDocCount(Integer institutionDocCount) {
		this.institutionDocCount = institutionDocCount;
	}


	public String getContinent() {
		return continent;
	}


	public void setContinent(String continent) {
		this.continent = continent;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public List<Affiliation> getAffiliationInfo() {
		return affiliationInfo;
	}


	public void setAffiliationInfo(Affiliation aff) {
		this.affiliationInfo.add(aff);
	}


	
}
class Affiliation
{
	private Integer affiliationId;
	private String affiliationName;
	private String affiliationPreferedName;
	
	private Integer affiliationDocCount;
	private List<String> affiliationNameVariants;
	
	public Affiliation()
	{
		affiliationNameVariants = Collections.synchronizedList(new LinkedList<>());
	}
	
	//Setters & Getters
	public Integer getAffiliationId() {
		return affiliationId;
	}
	public void setAffiliationId(Integer affiliationId) {
		this.affiliationId = affiliationId;
	}
	public String getAffiliationName() {
		return affiliationName;
	}
	public void setAffiliationName(String affiliationName) {
		this.affiliationName = affiliationName;
	}
	public String getAffiliationPreferedName() {
		return affiliationPreferedName;
	}

	public void setAffiliationPreferedName(String affiliationPreferedName) {
		this.affiliationPreferedName = affiliationPreferedName;
	}
	public Integer getAffiliationDocCount() {
		return affiliationDocCount;
	}
	public void setAffiliationDocCount(Integer affiliationDocCount) {
		this.affiliationDocCount = affiliationDocCount;
	}
	public List<String> getAffiliationNameVariants() {
		return affiliationNameVariants;
	}
	public void setAffiliationNameVariants(String affiliationNameVariant) {
		this.affiliationNameVariants.add(affiliationNameVariant);
	}
}
