package org.ei.dataloading.bd.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//import org.jdom2.*;                 
//import org.jdom2.input.*;
//import org.jdom2.output.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;



import org.ei.dataloading.bd.loadtime.BaseTableDriver.RecordReader;

public class BdNumericalIndexingReader {
	
	private int loadNumber;
    private String databaseName;
    private String action;
    private static Connection con;
    private static String infile;
    private static FileWriter outfile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "";
	
    
	public BdNumericalIndexingReader(int loadN,String databaseName)
	{
	    this.loadNumber = loadN;
	    this.databaseName = databaseName;
	    this.action = "normal";
	}
	
	public BdNumericalIndexingReader(int loadN,String databaseName,String action)
	{
	    this.loadNumber = loadN;
	    this.databaseName = databaseName;
	    this.action = action;
	}
	
	public BdNumericalIndexingReader() 
	{				
		System.out.println("In here");
	}
	
	public static void main(String args[]) throws Exception
    {
        int loadN=0;
        long startTime = System.currentTimeMillis();
        if(args.length<8)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }

        loadN = Integer.parseInt(args[0]);

        infile = args[1];
        String databaseName = args[2];
        String action = null;
        if(args.length>3)
        {
            url = args[4];
            driver = args[5];
            username = args[6];
            password = args[7];
            action = args[3];
            System.out.println("DATABASE URL= "+url);
            System.out.println("DATABASE USERNAME= "+username);
            //System.out.println("DATABASE PASSWORD= "+password);
        }
        else
        {
            System.out.println("USING DEFAULT DATABASE SETTING");
            System.out.println("DATABASE URL= "+url);
            System.out.println("DATABASE USERNAME= "+username);
            //System.out.println("DATABASE PASSWORD= "+password);
        }
        BdNumericalIndexingReader c;

        try
        {

            if(action!=null)
            {
                c = new BdNumericalIndexingReader(loadN,databaseName,action);
                System.out.println("action="+action);
            }
            else
            {
                c = new BdNumericalIndexingReader(loadN,databaseName);
            }
            outfile = new FileWriter(infile+"."+loadN+"_numerical.out");
            //con = c.getConnection(url,driver,username,password);
            c.writeBaseTableFile(infile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if(outfile != null)
            {
            	 try
                 {
                     outfile.close();
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
            }
                  
            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        }
    }
	
	private void writeRecs(InputSource xmlReader) throws Exception
    {
		try 
		{	
			String car = null;
			String pui = null;
			String mid = null;
			Node nls = null;
			NodeList nList = null;
			StringBuffer singleRecord = null;
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Element doc = builder.parse(xmlReader).getDocumentElement();
			NodeList nsList = doc.getElementsByTagName("NumericalIndexings");
			if(nsList!=null && nsList.getLength()>0)
			{
				
				nls = nsList.item(0);
			
				//System.out.println("Root element :" + doc.getNodeName());
				if(nls!=null)	
				{
					nList = ((Element)nls).getElementsByTagName("NumericalIndexing");
				}
				NodeList carList = doc.getElementsByTagName("car-id");	
				NodeList puiList = doc.getElementsByTagName("pui-id");
				
				if(carList!=null && carList.getLength()>0)
				{
					car = getNodeValue(carList);
					if(car==null)
						car="";
				}
				
				if(puiList != null && puiList.getLength()>0)
				{
					pui = getNodeValue(puiList);
					if(pui==null)
						pui="";
				}
				
				if(pui != null)
				{
					mid = getMID(pui);
					
					if(mid==null)
						mid="";
				}
				//System.out.println("----------------------------");
				
				for (int temp = 0; temp < nList.getLength(); temp++) {
					singleRecord = new StringBuffer();
					singleRecord.append(mid+"\t"+pui+"\t"+car+"\t");
					Node nNode = nList.item(temp);
					String value = null;
					String minimum = null;
					String maximum = null;
					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
					//System.out.println("MID= "+mid+"\t"+pui+"\t"+car+"\t");	
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	
						Element eElement = (Element) nNode;
						if(eElement.getElementsByTagName("value")!=null && getNodeValue(eElement.getElementsByTagName("value"))!=null)
						{
							value = getNodeValue(eElement.getElementsByTagName("value"));
							//System.out.println("value : " + getNodeValue(eElement.getElementsByTagName("value")));
							//System.out.println("*****************************************************");
						}
						if(eElement.getElementsByTagName("unit")!=null && getNodeValue(eElement.getElementsByTagName("unit"))!=null)
						{
							//System.out.println("unit : " + getNodeValue(eElement.getElementsByTagName("unit")));
							//System.out.println("*****************************************************");
							singleRecord.append(getNodeValue(eElement.getElementsByTagName("unit")));
						}
						singleRecord.append("\t");
						if(eElement.getElementsByTagName("symbol")!=null && getNodeValue(eElement.getElementsByTagName("symbol"))!=null)
						{
							//System.out.println("symbol : " + getNodeValue(eElement.getElementsByTagName("symbol")));
							//System.out.println("*****************************************************");
							singleRecord.append(getNodeValue(eElement.getElementsByTagName("symbol")));
						}
						singleRecord.append("\t");
						if(eElement.getElementsByTagName("minimum")!=null && getNodeValue(eElement.getElementsByTagName("minimum"))!=null)
						{
							//System.out.println("minimum : " + getNodeValue(eElement.getElementsByTagName("minimum")));
							//System.out.println("*****************************************************");
							singleRecord.append(getNodeValue(eElement.getElementsByTagName("minimum")));
						}
						else if(value!=null)
						{
							singleRecord.append(value);
						}
						singleRecord.append("\t");
						
						if(eElement.getElementsByTagName("maximum")!=null && getNodeValue(eElement.getElementsByTagName("maximum"))!=null)
						{
							//System.out.println("maximum : " + getNodeValue(eElement.getElementsByTagName("maximum")));
							//System.out.println("*****************************************************");
							singleRecord.append(getNodeValue(eElement.getElementsByTagName("maximum")));
						}
						else if(value!=null)
						{
							singleRecord.append(value);
						}
						
						singleRecord.append("\t"+this.loadNumber);
						
						
					}
					outfile.write(singleRecord.toString()+"\n");
				}
			}
	    } 
		catch (Exception e) 
		{
	    	e.printStackTrace();
	    }

    }
	
	public String getNodeValue(NodeList parentNodes)
	{
		StringBuffer nodeValue = new StringBuffer();
		if(parentNodes!=null)
		{
			for(int l=0;l<parentNodes.getLength();l++)
			{
				Node parentNode = parentNodes.item(l);
				//System.out.println("NODE0= "+parentNode.getNodeName());
				if(parentNode.getNodeValue()!=null)
				{
					nodeValue.append(parentNode.getNodeValue().trim());
					//System.out.println("NODE0= "+parentNode.getNodeName()+" VALUE= "+parentNode.getNodeValue().trim());
				}
				NodeList aNodes = parentNode.getChildNodes();
				
				for(int i=0;i<aNodes.getLength();i++)
				{
					Node aNode = aNodes.item(i);
					//System.out.println("NODE1= "+aNode.getNodeName());
					if(aNode.getNodeValue()!=null)
					{
						nodeValue.append(aNode.getNodeValue().trim());
						//System.out.println("NODE1= "+aNode.getNodeName()+" VALUE= "+aNode.getNodeValue().trim());
					}
					if(aNode.hasChildNodes())
					{
						NodeList subNodes = aNode.getChildNodes();
						for(int j=0;j<subNodes.getLength();j++)
						{
							Node subNode = subNodes.item(j);
							//System.out.println("NODE2= "+subNode.getNodeName());
							if(subNode.getNodeValue()!=null)
							{
								nodeValue.append(subNode.getNodeValue().trim());
								//System.out.println("NODE2= "+subNode.getNodeName()+" VALUE= "+subNode.getNodeValue().trim());
							}
							if(subNode.hasChildNodes())
							{
								NodeList endNodes = subNode.getChildNodes();
								for(int k=0;k<endNodes.getLength();k++)
								{
									Node endNode = endNodes.item(k);
									//System.out.println("NODE3= "+endNode.getNodeName());
									if(endNode.getNodeValue()!=null)
									{
										nodeValue.append(endNode.getNodeValue().trim());
										//System.out.println("NODE3= "+endNode.getNodeName()+" VALUE= "+endNode.getNodeValue().trim());
									}
								}
							}
						}
					}
					
				}
				return nodeValue.toString();
			}
		}
		return null;

	}
	
	public void writeBaseTableFile(String infile) throws Exception
    {
		InputSource in = null;
        this.infile = infile;
        try
        {
        	          
            if(infile.toLowerCase().endsWith(".zip"))
            {
                System.out.println("IS ZIP FILE");
                ZipFile zipFile = new ZipFile(infile);
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements())
                {
                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    String name = entry.getName();                  
                    if(name.indexOf("CPXNI")>-1)
                    {
                    	in = new InputSource(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
                    	writeRecs(in);
                    }
                }
            }
            else if(infile.toLowerCase().endsWith(".xml"))
            {
                System.out.println("IS XML FILE");
                in = new InputSource(new FileReader(infile));
                writeRecs(in);
            }
            else
            {
                System.out.println("this application only handle xml and zip file");
            }         

        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
       
    }
	
	private String getMID(String pui)
	{
		Statement stmt = null;
        ResultSet rs = null;
        String mid= "";
        String sqlQuery = null;
        Connection con = null;
        try{
	        if(pui!=null)
	        {
	        	con = getConnection(this.url,this.driver,this.username,this.password);
	            stmt = con.createStatement();
	            //sqlQuery = "select m_id from bd_master_orig where database='cpx' and pui='"+pui+"'";
	            sqlQuery = "select m_id from bd_master_orig where pui='"+pui+"'";
				//System.out.println("Running the query..."+sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
				while (rs.next())
				{
					
				    if(rs.getString("M_ID") != null && rs.getString("M_ID").indexOf("cpx")>-1)
				    {
				        mid=rs.getString("M_ID");
				        //System.out.println("M_ID="+mid);
				    }				    
				}
	            
	        }
	        else
	        {
	        	return "";
	        }
        
	       
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
	          if (rs != null)
	          {
	              try
	              {
	                  rs.close();
	              }
	              catch (Exception e)
	              {
	                  e.printStackTrace();
	              }
	          }
	          if (stmt != null)
	          {
	              try
	              {
	            	  stmt.close();
	              }
	              catch (Exception e)
	              {
	                  e.printStackTrace();
	              }
	          }
	    }
        return mid;
	}
	
	 public Connection getConnection(String connectionURL,
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
