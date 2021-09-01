package org.ei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.DocType;

import org.ei.dataloading.knovel.loadtime.*;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.awss3.UploadFileToS3;
import org.ei.dataloading.awss3.UploadFileToS3Thread;

public class SplitXmlFile
{
	private Namespace aitNamespace=Namespace.getNamespace("ait","http://www.elsevier.com/xml/ait/dtd");
	private Namespace ceNamespace=Namespace.getNamespace("ce","http://www.elsevier.com/xml/common/dtd");
	private Namespace noNamespace=Namespace.getNamespace("","");
	private Namespace aniNamespace=Namespace.getNamespace("ani","http://www.elsevier.com/xml/ani/ani");
	private Namespace xmlNamespace=Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace");
	private Namespace xoeNamespace=Namespace.getNamespace("xoe","http://www.elsevier.com/xml/xoe/dtd");
	private Namespace xocsNamespace=Namespace.getNamespace("xocs","http://www.elsevier.com/xml/xocs/dtd");
	private Namespace aiiNamespace=Namespace.getNamespace("aii","http://www.elsevier.com/xml/ani/internal");
	private String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String username = "ap_ev_search";
	private String password = "ei3it";
	private File filepath;
		
	public static void main(String[] args) throws Exception
    {
        String xmlFileName = null;
        String loadnumber = null;
        String database = null;
        if(args.length>2)
        {
        	xmlFileName = args[0];
        	loadnumber = args[1];
        	database = args[2];
        }
        else
        {
        	System.out.println("please enter 3 prarmeters");
        	System.exit(1);
        }
        
        SplitXmlFile s = new SplitXmlFile();
        s.createFilePath(database,loadnumber);
        s.readFileintoInputStream(xmlFileName,database,loadnumber);               
    }
	
	private void createFilePath(String database, String loadnumber)
	{
		filepath = new File(database);
        if(!filepath.exists())
        {
            filepath.mkdir();
        }
        filepath = new File(database+"/"+loadnumber);
        if(!filepath.exists())
        {
            filepath.mkdir();
        }
        System.out.println("create new file path "+filepath.getName());
	}
	
	private void readFileintoInputStream(String infile, String database, String loadnumber) throws Exception
    {
		InputStream in = null;
       
        try
        {
          
            if(infile.toLowerCase().endsWith(".zip"))
            {
                System.out.println("IS ZIP FILE");
                if(database.equalsIgnoreCase("upt") || database.equalsIgnoreCase("wop") || database.equalsIgnoreCase("eup") || database.equalsIgnoreCase("upa"))
                {
                	renameUptZipFileName(infile,database,loadnumber);              
                }
                else
                {               
	                ZipFile zipFile = new ZipFile(infile);
	                Enumeration entries = zipFile.entries();
	                while (entries.hasMoreElements())
	                {
	                    ZipEntry entry = (ZipEntry)entries.nextElement();
	                    in = zipFile.getInputStream(entry);   
	                    convertXMLFileToXMLDocument(in,database,loadnumber);
	                    
	                }
                }
                
            }
            else if(infile.toLowerCase().endsWith(".xml"))
            {
                System.out.println("IS XML FILE");
               
                File file = new File(infile);
                if(database.equalsIgnoreCase("upt") || database.equalsIgnoreCase("wop") || database.equalsIgnoreCase("eup") || database.equalsIgnoreCase("upa"))
                {
                	renameUptXmlFileName(infile,database,loadnumber);
                }
                else if(database.equalsIgnoreCase("knc") || database.equalsIgnoreCase("kna"))
                {
                	renameKnovelFileName(infile,database,loadnumber);
                }
                else
                {
	                in = new FileInputStream(file);  
	                convertXMLFileToXMLDocument(in,database,loadnumber);
                }
            }
            else if(infile.toLowerCase().endsWith(".pat"))
            {
            	if(database.equalsIgnoreCase("ept"))
            	{
            		outputEptSingleFile(infile,database,loadnumber);
            	}
            }
            else
            {
            	 File folder = new File(infile);
            	 if(folder.isDirectory())
                 {          		
            		 File[] listOfFiles = folder.listFiles();

            		 for (File file : listOfFiles) 
            		 {
            		     if (file.isFile()) 
            		     {
            		    	 String fullFileName = file.getAbsolutePath();
            		         //System.out.println(fullFileName);
            		         readFileintoInputStream(fullFileName,database,loadnumber);
            		     }
            		 }
                 }
            	 else
            	 {
            		 System.out.println("this application only handle xml and zip file");
            	 }
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
       
    }
	
	private void outputEptSingleFile(String filename, String database, String loadnumber)
	{
		Connection conn = null;
		File file = new File(filename);	
		FileWriter out = null;
		StringBuffer sBuffer = new StringBuffer();
		BufferedReader in = null;
		String line = null;
		try
		{
			conn = getConnection(this.url,this.driver,this.username,this.password);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while((line=in.readLine())!=null)
            {
				if(line.trim().equalsIgnoreCase("BIBL"))
				{
					//System.out.println("new record");
					if(out != null && sBuffer.length()>0)
					{
						//System.out.println(sBuffer.toString());
						out.write(sBuffer.toString());
						out.close();
						out = null;
					}
					sBuffer = new StringBuffer();
					
				}
				sBuffer.append(line+"\n");
				if(line.length()>=2 && line.substring(0,2).equalsIgnoreCase("DN"))
				{
					String accessnumber = line.substring(2).trim();
					String m_id = getMID(conn,accessnumber,database);
					if(m_id==null)
					{
						System.out.println("accessnumber "+accessnumber+" is not in database");
						continue;
					}
					else
					{ 
						//System.out.println("new file name="+m_id+".pat");
						out = new FileWriter(this.filepath+File.separator+m_id+".pat");
					}
				}
            }
			out.write(sBuffer.toString());
			out.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				{
					e.printStackTrace();
				}
			}
			
			if(in!=null)
			{
				try
				{
					in.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(conn!=null)
			{
				try
				{
					conn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void renameKnovelFileName(String groupFileName,String database,String loadNumber) throws Exception
	{
		HashMap fileMap = new HashMap();
		KnovelReader knovel = new KnovelReader();
		fileMap = knovel.getFilesFromGroupFile(groupFileName);
		Iterator<String> fileIterator = fileMap.keySet().iterator(); 
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		
		while(fileIterator.hasNext())
		{
			String name = (String)fileIterator.next();
			File file = new File(name);
			String fileName = file.getName();
			String accessnumber = fileName.replaceAll("book-cid-", "").replaceAll("-atom.xml", "");
			String m_id = getMID(conn,accessnumber,database);
			String newFileName = this.filepath+File.separator+m_id+".xml";
			File dest = new File(newFileName);
			if(m_id!=null)
			{
				file.renameTo(dest);
				//System.out.println(name+"--"+accessnumber+" -- "+newFileName);	
			}
			else
			{
				System.out.println("accessnumber "+accessnumber+" is not in oracle");
			}
					
			
		}
	}
	
	private void renameUptZipFileName(String zipFileName,String database,String loadNumber) throws Exception
	{
		FileInputStream fis;
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try 
        {
            fis = new FileInputStream(zipFileName);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String oldfileName = ze.getName();
                String newFileName = getNewFileName(conn,oldfileName);
                File newFile = new File(this.filepath + File.separator + newFileName);
                //System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                //new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
	
	private void renameUptXmlFileName(String oldfileName,String database,String loadNumber) throws Exception
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		String newFileName = null;
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		try
		{
			newFileName = getNewFileName(conn,oldfileName);
			fis = new FileInputStream(oldfileName);			
			fos = new FileOutputStream(this.filepath+File.separator+newFileName);
			byte[] buffer = new byte[1024];
	        int len;
	        while ((len = fis.read(buffer)) > 0) 
	        {
	        	fos.write(buffer, 0, len);
	        }
	        fos.close();
	        fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fos != null) {
				try {
					fos.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	private void renameUptFileName(String oldname,String database,String loadNumber) throws Exception
	{
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		// File (or directory) with old name
		File file = new File(oldname);
		String newname = getNewFileName(conn,oldname.replace(".xml", ""));
		// File (or directory) with new name
		
		File file2 = new File(this.filepath+"/"+newname);

			
		if (file2.exists())
		{
			System.out.println("file exists, delete it");
			String newfilename = file2.getName();
			if(file2.delete())
				System.out.println("file "+ newfilename +" deleted");
			else
				System.out.println("unable to delete file "+ newfilename);
		}

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (!success) 
		{
		   System.out.println("Can't rename file "+oldname+" to "+newname);
		   System.out.println("path is "+file2.getAbsolutePath());
		}
	}
	
	
	
	private void WriteBDSingleRecordXmlFile(Document xmlDocument, String database, String loadnumber) throws Exception
	{
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		Element rootElement = xmlDocument.getRootElement();
		String namespaceURI = rootElement.getNamespace().getURI();
		if(namespaceURI!=null && namespaceURI.length()>0)
		{
			noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");
		}
						
		//List itemElements = rootElement.getChildren("item",noNamespace);
		List itemElements = rootElement.getChildren("item",noNamespace);
		for(int i=0;i<itemElements.size();i++)
		{
			String m_id=null;
			Element e = (Element)itemElements.get(i);
			String accessnumber = null;
			if(e!=null && e.getChild("bibrecord",noNamespace)!=null)
			{
				if(e.getChild("bibrecord",noNamespace).getChild("item-info",noNamespace)!=null)
				{
					accessnumber = getAccessnumber(e.getChild("bibrecord",noNamespace).getChild("item-info",noNamespace),database);
					//System.out.println("accessnumber="+accessnumber);
				}				
				else
				{
					System.out.println("<item-info> is null");
				}
			}
			else
			{
				System.out.println("<bibrecord> is null");
			}
			if(accessnumber!=null)
			{
				m_id = getMID(conn,accessnumber,database);
				if(m_id==null)
				{
					System.out.println("accessnumber "+accessnumber+" is not in database");
					continue;
				}
			}
			Document document = new Document();
			Element dist = new Element("bibdataset",noNamespace );						
			dist.addNamespaceDeclaration(aitNamespace);
			dist.addNamespaceDeclaration(ceNamespace);
			dist.addNamespaceDeclaration(xmlNamespace);
			dist.addNamespaceDeclaration(xoeNamespace);
			dist.addNamespaceDeclaration(xocsNamespace);
			dist.addNamespaceDeclaration(aiiNamespace);		
			//dist.addNamespaceDeclaration(aniNamespace);
			dist.addContent(e.detach());			
			document.setRootElement(dist);		

	        // Create an XMLOutputter object with pretty formatter. Calling
	        // the outputString(Document doc) method convert the document
	        // into string data.
	        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	        String xmlString = outputter.outputString(document);
	        //st.append(xmlString);
	        //st.append("</bibdataset>");
	        File filepath = new File(database);
	        if(!filepath.exists())
            {
                filepath.mkdir();
            }
	        filepath = new File(database+"/"+loadnumber);
	        if(!filepath.exists())
            {
                filepath.mkdir();
            }
	        FileWriter out = new FileWriter(filepath+"/"+m_id+".xml");
	        out.write(xmlString);
	        out.close();
			
		}
	}
	
	private void WriteGRFSingleRecordXmlFile(Document xmlDocument, String database, String loadnumber) throws Exception
	{
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		Element rootElement = xmlDocument.getRootElement();
		
		String namespaceURI = rootElement.getNamespace().getURI();
		if(namespaceURI!=null && namespaceURI.length()>0)
		{
			noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");
		}
								
		List itemElements = rootElement.getChildren("Reference");
		
		for(int i=0;i<itemElements.size();i++)
		{
			String m_id=null;			
			Element e = (Element)itemElements.get(i);
			String accessnumber = null;
			if(e!=null && e.getChild("Z01")!=null)
			{				
				accessnumber = e.getChildText("Z01");			
				
			}
			else
			{
				System.out.println("<Z01> not found");
			}
			
			if(accessnumber!=null)
			{
				m_id = getMID(conn,accessnumber,database);
				if(m_id==null)
				{
					System.out.println("accessnumber "+accessnumber+" is not in database");
					continue;
				}
			}
			Document document = new Document();
			Element dist = new Element("Distribution");
			dist.addContent(e.detach());			
			document.setRootElement(dist);

	        // Create an XMLOutputter object with pretty formatter. Calling
	        // the outputString(Document doc) method convert the document
	        // into string data.
	        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	        String xmlString = outputter.outputString(document); 
	        FileWriter out = new FileWriter(filepath+"/"+m_id+".xml");
	        out.write(xmlString);
	        out.close();
			
		}
	}
	
	private void WriteNTISSingleRecordXmlFile(Document xmlDocument, String database, String loadnumber) throws Exception
	{
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		Element rootElement = xmlDocument.getRootElement();
							
		List itemElements = rootElement.getChildren("Record");
		
		for(int i=0;i<itemElements.size();i++)
		{
			String m_id=null;			
			Element e = (Element)itemElements.get(i);
			String accessnumber = null;
			if(e!=null && e.getChild("AccessionNumber")!=null)
			{				
				accessnumber = e.getChildText("AccessionNumber");			
				
			}
			else
			{
				System.out.println("<AccessionNumber> not found");
			}
			
			if(accessnumber!=null)
			{
				m_id = getMID(conn,accessnumber,database);
				if(m_id==null)
				{
					System.out.println("accessnumber "+accessnumber+" is not in database");
					continue;
				}
			}
			Document document = new Document();
			Element dist = new Element("Records");
			dist.addContent(e.detach());			
			document.setRootElement(dist);

	        // Create an XMLOutputter object with pretty formatter. Calling
	        // the outputString(Document doc) method convert the document
	        // into string data.
	        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	        String xmlString = outputter.outputString(document);	      
	        FileWriter out = new FileWriter(filepath+"/"+m_id+".xml");
	        out.write(xmlString);
	        out.close();
			
		}
	}
	
	private void WriteINSPECSingleRecordXmlFile(Document xmlDocument, String database, String loadnumber) throws Exception
	{
		Connection conn = getConnection(this.url,this.driver,this.username,this.password);
		Element rootElement = xmlDocument.getRootElement();
							
		List itemElements = rootElement.getChildren("article");
		System.out.println("Ins size="+itemElements.size());
		
		for(int i=0;i<itemElements.size();i++)
		{
			String m_id=null;			
			Element e = (Element)itemElements.get(i);
			String accessnumber = null;
			if(e!=null && e.getChild("contg")!=null && e.getChild("contg").getChild("accn")!=null )
			{				
				accessnumber = e.getChild("contg").getChildText("accn");			
				
			}
			else
			{
				System.out.println("<contg><accn> not found");
			}
			
			if(accessnumber!=null)
			{
				m_id = getMID(conn,accessnumber,database);
				if(m_id==null)
				{
					System.out.println("accessnumber "+accessnumber+" is not in database");
					continue;
				}
			}
			Document document = new Document();
			DocType inspecDTD = new DocType("inspec","inspec_xml.dtd");
			document.setDocType(inspecDTD);
			Element ins = new Element("inspec");
			ins.addContent(e.detach());			
			document.setRootElement(ins);

	        // Create an XMLOutputter object with pretty formatter. Calling
	        // the outputString(Document doc) method convert the document
	        // into string data.
	        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	        String xmlString = outputter.outputString(document); 	        
	        FileWriter out = new FileWriter(filepath+"/"+m_id+".xml");
	        out.write(xmlString);
	        out.close();
			
		}
	}
 
    private Document convertXMLFileToXMLDocument(InputStream in,String database, String loadnumber) 
    {
        
    	SAXBuilder builder;
        try
        {
            //Create DocumentBuilder with default configuration
        	builder = new SAXBuilder();
    		builder.setExpandEntities(false);
    		builder.setFeature( "http://xml.org/sax/features/namespaces", true );
    		
    		//Parse the content to Document object
    		Document xmlDocument = builder.build(in);
    		xmlDocument.setBaseURI("."); 
    		if(database.equalsIgnoreCase("grf"))
    			WriteGRFSingleRecordXmlFile(xmlDocument,database,loadnumber);
    		else if(database.equalsIgnoreCase("ntis"))
    			WriteNTISSingleRecordXmlFile(xmlDocument,database,loadnumber);
    		else if(database.equalsIgnoreCase("ins"))
    			WriteINSPECSingleRecordXmlFile(xmlDocument,database,loadnumber);    		
    		else
    			WriteBDSingleRecordXmlFile(xmlDocument,database,loadnumber);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public Connection getConnection(String connectionURL,String driver,String username,String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,username,password);
		return con;
	}
    
    private String getAccessnumber(Element e,String database) throws Exception
    {
    	String accessnumber=null;
    	if(e !=null && e.getChild("itemidlist",noNamespace)!=null)
    	{
    		Element itemidList = e.getChild("itemidlist",noNamespace);   		
    		List itemids = itemidList.getChildren("itemid",noNamespace);
    		if(itemids!=null)
    		{
    			for(int i=0;i<itemids.size();i++)
    			{
    				Element itemid = (Element)itemids.get(i);
    				String itemidContent = itemid.getTextTrim();
    				String itemidType = itemid.getAttributeValue("idtype");
    				if(database.equalsIgnoreCase("pch"))
    				{
    					database="CPX";
    				}
    				else if (database.equalsIgnoreCase("chm"))
    				{
    					database="CHEM";
    				}
    				else if (database.equalsIgnoreCase("elt"))
    				{
    					database="APILIT";
    				}
    				else if (database.equalsIgnoreCase("cbn"))
    				{
    					database="CBNB";
    				}
    				
    				if(itemidType!=null && itemidType.equalsIgnoreCase(database))
    				{
    					accessnumber = itemidContent;
    				}
    			}
    		}
    		
    	}   	
    	else
    	{
    		System.out.println("itemidlist element is null");
    	}
    	
    	return accessnumber;
    }
    
    private String getNewFileName(Connection conn,String oldfileName) throws Exception
	{
		
		String newfileName = null;
		String patentAC = null;
		String patentNumber = null;
		String patentKind = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String m_id = null;
		try
		{
			if(oldfileName!=null && oldfileName.length()>5)
			{
				oldfileName = oldfileName.replace(".xml", "");
				patentAC = oldfileName.substring(0,2);
				Boolean flag = Character.isLetter(oldfileName.charAt(oldfileName.length()-2));
				if(!flag)
				{
					patentKind = oldfileName.substring(oldfileName.length()-1);
					patentNumber = oldfileName.substring(2,oldfileName.length()-1);
				}
				else
				{
					patentKind = oldfileName.substring(oldfileName.length()-2);
					patentNumber = oldfileName.substring(2,oldfileName.length()-2);
				}
				
				//System.out.println("oldName="+oldfileName+" patentAC="+patentAC+" patentKind="+patentKind+" patentNumber="+patentNumber);
				stmt = conn.createStatement();
				sqlQuery = "select m_id from upt_master where ac='"+patentAC+"' and pn='"+patentNumber+"' and kc='"+patentKind+"'";
				rs = stmt.executeQuery(sqlQuery);			
				while (rs.next())
				{
					m_id = rs.getString("M_ID");	
					if(m_id!=null)
					{
						newfileName=m_id+".xml";
					}
				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return newfileName;
	}
    
    private String getMID(Connection con, String accessnumber, String database) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String m_id = null;
		List midList = new ArrayList();
		
		try
		{
			
			stmt = con.createStatement();	
			if(database.equalsIgnoreCase("cbn"))
			{
				sqlQuery = "select M_id from ap_ev_search.cbn_master where abn='"+accessnumber+"'";
			}
			else if(database.equalsIgnoreCase("ntis"))
			{
				sqlQuery = "select M_id from ap_ev_search.ntis_master where an='"+accessnumber+"'";
			}
			else if(database.equalsIgnoreCase("grf"))
			{
				sqlQuery = "select M_id from ap_ev_search.georef_master where id_number='"+accessnumber+"'";
			}
			else if(database.equalsIgnoreCase("ins"))
			{
				sqlQuery = "select M_id from ap_ev_search.new_ins_master where anum='"+accessnumber+"'";
			}
			else if(database.equalsIgnoreCase("ept"))
			{
				sqlQuery = "select M_id from ap_ev_search.ept_master where dn='"+accessnumber+"'";
			}
			else if(database.equalsIgnoreCase("knc"))
			{
				sqlQuery = "select M_id from ap_ev_search.knovel_master where database='knc' and accessnumber='"+accessnumber+"'";
			}
			else
			{
				sqlQuery = "select M_id from ap_ev_search.bd_master where database='"+database+"' and accessnumber='"+accessnumber+"'";
			}
	        rs = stmt.executeQuery(sqlQuery);			
			while (rs.next())
			{
				m_id = rs.getString("M_ID");				
			}
					
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return m_id;
	    
	}
    
   
	 
    
}