package org.ei.dataloading.inspec.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.bd.loadtime.BaseTableWriter;
import org.ei.dataloading.bd.loadtime.BdReverseRecordBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class InspecReverseRecordBuilder
{
	private static BaseTableWriter baseWriter;
    private String accessnumber;
    private int loadNumber;
    private String databaseName;
    private String tableName = "ins_master";
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"US-ASCII\"?><!DOCTYPE inspec SYSTEM \"inspec_xml.dtd\"><inspec>";        																								
    private static String endRootElement   ="</inspec>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ap_ev_search";
    private static String password = "";
    private DataLoadDictionary dictionary = new DataLoadDictionary();
 
    public InspecReverseRecordBuilder()
    {
    	
    }
    public InspecReverseRecordBuilder(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
   
    }
    
	public static void main(String args[]) throws Exception
    {
        int loadN=0;
        long startTime = System.currentTimeMillis();      
        
        if(args.length>3)
        {
            url = args[3];
            driver = args[4];
            username = args[5];
            password = args[6];          
        }
        else
        {
        	
        	infile = args[0];
        	String xdsName = args[1];
        	System.out.println("Validating XML file "+infile);
        	InspecReverseRecordBuilder b = new InspecReverseRecordBuilder();
        	if(b.validatedXml(infile,xdsName))
            {
            	 System.out.println("File "+infile+" is valid");
            }
        	else
        	{
        		System.out.println("File "+infile+" is NOT valid");
        	}
        	System.exit(1);
        }
        loadN = Integer.parseInt(args[0]);
        String databaseName = args[1];
        String tableName = args[2];
        InspecReverseRecordBuilder c;

        try
        {

          
            c = new InspecReverseRecordBuilder(loadN,databaseName);            
            con = c.getConnection(url,driver,username,password);
            c.writeFile(con,loadN,databaseName,tableName);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        }
    }
	
	 private boolean validatedXml(String filename,String xsdFileName) throws Exception
	{
		// parse an XML document into a DOM tree
		 javax.xml.parsers.DocumentBuilder parser =  javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 Document document = parser.parse(new File(filename));

		// create a SchemaFactory capable of understanding WXS schemas
		//SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		 SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		//Source schemaFile = new StreamSource(new File("ani512.xsd"));
		Source schemaFile = new StreamSource(new File(xsdFileName));
		Schema schema = factory.newSchema(schemaFile);

		// create a Validator instance, which can be used to validate an instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
		   //validator.validate(new DOMSource(document));
			validator.validate(new StreamSource(new File(filename)));
			
		    //System.out.println(filename+" is valid!");
		    return true;
		} catch (SAXException e) {
			System.out.println(filename+" is invalid!");
			e.printStackTrace();
			return false;
		    // instance document is invalid!
		}
		
	}
	 
	 public void writeFile(Connection con,int loadN,String databaseName, String tableName) throws Exception
    {
    	StringBuffer initialString = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	initialString.append("<bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" ");
    	initialString.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    	initialString.append("xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" ");
    	initialString.append("xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" ");
    	initialString.append("xmlns=\"http://www.elsevier.com/xml/ani/ani\">\n");
    	 Statement stmt = null;
         ResultSet rs = null;
         int count = 0;
         boolean checkResult = false;
         FileWriter file = null;
         String xsdFileName="ani515/ani515.xsd";
         File path = null;
         Hashtable recordData = new Hashtable();     	
         try
         {
			stmt = con.createStatement();			
			rs = stmt.executeQuery("select *  from new_ins_master where load_number="+loadN);

			path=new File(databaseName);
			if(!path.exists())
			{
			     path.mkdir();
			}
			path=new File(databaseName+"/xml");
			if(!path.exists())
			{
			     path.mkdir();
			}
         	path=new File(databaseName+"/xml/"+loadN);
        	if(!path.exists())
            {
                path.mkdir();
            }
         	 
             while (rs.next())
             {
            	 String m_id=rs.getString("M_ID");
            	 String filename = path+"/"+m_id+".xml";
                 file = new FileWriter(filename);
                 file.write(initialString.toString());                
                 //recordData=getInspcDataFromDatabase(rs);                
                 //writeRecord(recordData,file);
                 file.write("</inspec>");
                 file.close();
                 
                 if(!validatedXml(filename,xsdFileName))
                 {
                	 continue;
                 }
             }
            
         }
         catch (Exception e)
         {
        	 System.out.println("problem with accessnumber="+this.accessnumber);
        	 e.printStackTrace();
             System.exit(1);
         }
         finally
         {
             if(rs != null)
             {
                 rs.close();
             }
             
             if(file != null)
             {
            	 file.close();
             }
         }
    	
    }
	 
	public Connection getConnection(String connectionURL,String driver,String username,String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,username,password);
		return con;
	}
	 
}