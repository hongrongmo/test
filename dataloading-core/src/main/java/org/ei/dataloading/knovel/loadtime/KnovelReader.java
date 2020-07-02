package org.ei.dataloading.knovel.loadtime;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.GUID;
import org.ei.dataloading.DataLoadDictionary;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.*;

public class KnovelReader
{
	private HashMap output_record = null;
	private Document doc = null;
	private Iterator rec =null;
	private Perl5Util perl = new Perl5Util();
	private HashMap dupMap = new HashMap();
	public static final char IDDELIMITER = (char)31;
	public static final char DELIM = '\t';
	public static final char AUDELIMITER = (char) 30;
	private int chapterCount = 0;
	private int bookCount = 0;
	private int totalChapterCount = 0;
	private int totalExceptionCount = 0;
	private String lastModify = null;
	/*
	 * If there is field delimiter that is 2 or more values for one field eg, A;B;C,
	 * use 'AUDELIMITER' between A, B and C
	 * If there is subfields in one value of the field use 'IDDELIMITER' ,
	 * for eg. A:1;B:2;C:3 USE AUDELIMITER between A, B and C
	 * To separate fields use 'IDDELIMITER between A and 1, B and 2 and C and 3.
	 */
	private Namespace groupNamespace=Namespace.getNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");
	private Namespace noNamespace=Namespace.getNamespace("http://www.w3.org/2005/Atom");
	private Namespace dcNamespace=Namespace.getNamespace("dc","http://purl.org/dc/elements/1.1/");
	private Namespace dctermsNamespace=Namespace.getNamespace("dcterms","http://purl.org/dc/terms/");
	private Namespace knovelNamespace=Namespace.getNamespace("knovel","https://federation.knovel.com/discovery/v1");
	public Element bibliographic_data;
    public Element abstractData;
    private String path;
    PrintWriter out;
    PrintWriter outDelete;
    PrintWriter out_chapter;
	static String loadNumber;
	static String filename;
	static String xmlFileName;
	private static List<String> excludedSubjectList;
	
	//HH 03/18/2016 for S3
	GetKnovelFilesFromS3 knovelS3 = null;
	//static String bucketName = "datafabrication-reports";
	static String bucketName;
	static String key="";

	 public static void main(String[] args)
	        throws Exception
	 {
		loadNumber = args[0];
		filename = args[1];
		String database = args[2];
		
		String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
		String username = "ap_correction1";
		String password = "";
		String driver = "oracle.jdbc.driver.OracleDriver";
		if(args.length>6)
		{
			url=args[3];
			username = args[4];
			password = args[5];
			bucketName = args[6];  //HH 03/18/2016 for S3			
			key = args[7];			//HH 05/06/2016 for S3
			System.out.println("bucketName="+bucketName+" key="+key);
		}

		KnovelReader r = new KnovelReader();

		try
		{ 
			Connection con = r.getConnection(url,driver,username,password);
            r.setExcludedSubjectList(con);
			if(filename.indexOf("/")>-1)
			{			
				r.path = filename.substring(0,filename.lastIndexOf("/"));
				filename = filename.substring(filename.lastIndexOf("/")+1);
			}
			r.init(database,filename);
			//r.readGroupFile(r.path, filename);
			
			//HH 03/18/2016 for S3
			if(bucketName !=null && bucketName.length() >0)
			{				
				r.readGroupFilefromS3(r.path, filename);
			}
			else
			{				
				r.readGroupFile(r.path, filename);
			}
			
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			r.close();
			System.exit(1);
		}
	}
	
	public KnovelReader()
	{
	}
	 
	public KnovelReader(String updateNumber,String database,String filename)
	{
		this.loadNumber = updateNumber;
		init(database,filename);
	}
	
	//HH 03/22/2016 for knovel correction by getting files from S3 bucket
	
	public KnovelReader(String updateNumber,String database,String filename, String bucketName, String key)
	{
		this.loadNumber = updateNumber;
		this.bucketName = bucketName;
		this.key = key;
		init(database,filename);
	}
		
	 
	public void readGroupFile(String path, String filename) throws Exception
	{
		HashMap fileMap = new HashMap();
		String subpath ="";
		if(path!=null && path.length()>0)
		{
			this.path = path;
		}
				
		if(this.path!=null && this.path.length()>0)
		{
			fileMap = getFilesFromGroupFile(this.path+"/"+filename);
		}
		else
		{
			fileMap = getFilesFromGroupFile(filename);
		}
		Iterator<String> fileIterator = fileMap.keySet().iterator(); 
		
		while(fileIterator.hasNext())
		{
			String name = (String)fileIterator.next();
			this.lastModify = (String)fileMap.get(name);
			
			try
			{
				File inputFiles = null;
				if(path !=null && path.length()>0)
				{					
						name = path+"/"+name;
						this.path = name.substring(0,name.lastIndexOf("/"));				
				}
				else
				{
					this.path = name.substring(0,name.lastIndexOf("/"));
				}
				
				inputFiles = new File(name);
				
				if(inputFiles.exists())
				{					
					fileReader(inputFiles,lastModify);
				}
				else
				{					
					System.out.println("File "+name+" does not exist");												
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	//HH 03/18/2016 read files from S3 bucket

	public void readGroupFilefromS3(String path, String filename) throws Exception
	{
		String subpath ="";
		this.path = path;
		//System.out.println("in readGroupFilefromS3="+path+":"+filename);
		HashMap fileMap = getFilesFromGroupFileInS3(filename);
		//System.out.println("SIZE="+fileMap.size());
		Iterator<String> fileIterator = fileMap.keySet().iterator(); 

		while(fileIterator.hasNext())
		{
			String name = (String)fileIterator.next();
			this.lastModify = (String)fileMap.get(name);

			try
			{
				//File inputFiles = null;
				InputStream inputFiles = null;
				if(path !=null && path.length()>0)
				{					
					name = path+"/"+name;
					this.path = name.substring(0,name.lastIndexOf("/"));				
				}
				else if(name !=null && name.contains("/"))
				{
					this.path = name.substring(0,name.lastIndexOf("/"));
				}

				//inputFiles = new File(name);
				//Thread.sleep(5000);   // sleep for 5 seconds
				knovelS3.getFileContentFromS3(name);

				//if(inputFiles.exists())
				if(knovelS3.objectData !=null)
				{
					//fileReader(inputFiles,lastModify);
					fileReaderFromS3(knovelS3.objectData,name,lastModify);
				}
				else
				{					
					System.out.println("File "+name+" has no content");												
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				if(knovelS3.objectData !=null)
				{
					try
					{
						knovelS3.objectData.close();
					}
					catch(IOException ioex)
					{
						ioex.printStackTrace();
					}
				}
			} 
		}


	}
		 
	public HashMap getFilesFromGroupFile(String name)
	{
		HashMap groupMap = new HashMap();
		try
		{
			//System.out.println("GROUPFILENAME="+name);
			File inputFiles = new File(name);		
			
			SAXBuilder builder = new SAXBuilder();
			
			Document doc = builder.build(inputFiles);
			Element root = doc.getRootElement();
			
			List urls = root.getChildren("url",groupNamespace);
			
			for(int i=0;i<urls.size();i++)
			{
				
				Element url = (Element)urls.get(i);
				String loc = url.getChildText("loc",groupNamespace);
				if(loc!=null)
				{					
					loc = loc.replaceAll("/federation/","");
				}
				String lastmod = url.getChildText("lastmod",groupNamespace);
				Timestamp lastupdate = convertStringToTimestamp(lastmod);
				groupMap.put(loc,lastupdate.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return groupMap;		
	}
	
	//HH 03/18/2016
	private HashMap getFilesFromGroupFileInS3(String name)
	{
		HashMap groupMap = new HashMap();
		try
		{
			// download file from S3 bucket
			//System.out.println("bucketName="+bucketName+" key="+key);
			knovelS3 = new GetKnovelFilesFromS3(bucketName,key);
			//System.out.println("GROUPFILENAME="+name.substring(name.lastIndexOf("/")+1, name.length()));
			knovelS3.downloadGroupFileFromS3(name.substring(name.lastIndexOf("/")+1, name.length()).trim());	

						
			File inputFiles = new File(name);		

			SAXBuilder builder = new SAXBuilder();

			Document doc = builder.build(inputFiles);
			Element root = doc.getRootElement();

			List urls = root.getChildren("url",groupNamespace);

			for(int i=0;i<urls.size();i++)
			{

				Element url = (Element)urls.get(i);
				String loc = url.getChildText("loc",groupNamespace);
				if(loc!=null)
				{					
					loc = loc.replaceAll("/federation/","");
				}
				String lastmod = url.getChildText("lastmod",groupNamespace);
				Timestamp lastupdate = convertStringToTimestamp(lastmod);
				groupMap.put(loc,lastupdate.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return groupMap;		
	}

	public static Timestamp convertStringToTimestamp(String str_date) {
	    try {
	      SimpleDateFormat formatter;
	      formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	      String dateString = str_date.substring(0,str_date.indexOf('+'));	      
	     
	      Date date = formatter.parse(dateString);
	      java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());	  
	      return timeStampDate;
	    } catch (ParseException e) {
	    	e.printStackTrace();
	      return null;
	    }
	  }
	

	private void fileReader(File root,String lastModify)
	{			
			BufferedReader xmlReader = null;		
			try
			{
				xmlReader = new BufferedReader(new InputStreamReader(new FileInputStream(root)));
				knovelXmlParser(xmlReader,root.getName(),lastModify);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					xmlReader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	}

	//HH 03/18/2016 read each file content from S3 Bucket

	private void fileReaderFromS3(InputStream s3FileContent,String fileName, String lastModify)
	{			
		BufferedReader xmlReader = null;		
		try
		{
			xmlReader = new BufferedReader(new InputStreamReader(s3FileContent));
			knovelXmlParser(xmlReader,fileName,lastModify);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				xmlReader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

		
	private void close()
	{
		System.out.println("Close..............");

		if (out != null)
			out.close();
		if (outDelete != null)
			outDelete.close();
    }

	private void init(String database,String outFile)
	{
		try
		{
			System.out.println("init..............");
			this.filename = outFile;
			outFile = outFile.replace(".xml","");
			if(out==null)
			{
				out = new PrintWriter(new FileWriter("out"+File.separator+database+"_"+outFile+"_master_"+loadNumber+".out"), true);
			}
			
			if(out_chapter==null)
			{
				out_chapter = new PrintWriter(new FileWriter("out"+File.separator+database+"_"+outFile+"_master_"+loadNumber+"_chapter.out"), true);
			}
			
			if(outDelete==null)
			{
				outDelete = new PrintWriter(new FileWriter("out"+File.separator+database+"_"+outFile+"_Delete_"+loadNumber+".out"), true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void knovelXmlParser(Reader r, String filename, String lastModify) throws Exception
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		this.doc = builder.build(r);
		Element root = doc.getRootElement();
	
		if(root.getName().equals("entry"))
		{
			//System.out.println("****************"+bookCount + "-" + chapterCount + " - "+filename+" **********************");
			chapterCount = 0;
			//System.out.println( ++bookCount + "-" + chapterCount + "-" + totalChapterCount + ": FILENAME="+filename);
			String database = "knc";
			output_record = getRecord(root,filename);
			if(output_record != null)
			{
				if(output_record.get("KNC_DATABASE")!=null && ((String)output_record.get("KNC_DATABASE")).equalsIgnoreCase("knc"))
				{
					outputKnovelRecord(output_record,out,lastModify,"knc");
				}
				
				if(output_record.get("KNA_DATABASE")!=null && ((String)output_record.get("KNA_DATABASE")).equalsIgnoreCase("kna"))
				{
					outputKnovelRecord(output_record,out,lastModify,"kna");
				}
			}
		}
		else if(root.getName().equals("deleted-entry"))
		{
			output_record = getDeleteRecord(root,filename);	
			outputKnovelDeleteRecord(output_record,outDelete,lastModify);
		}
			
	}
	
	private HashMap getDeleteRecord(Element rec,String filename)
	{
		HashMap record = null;
		String accessnumber = "0";
		if(rec==null)
		{
			System.out.println("REC is null");
		}
		
		try
		{
			if(rec!=null)
			{
				String namespaceURI = rec.getNamespace().getURI();
				if(namespaceURI!=null && namespaceURI.length()>0)
				{
					noNamespace = Namespace.getNamespace("","http://www.w3.org/2005/Atom");
				}
				record = new HashMap();

				if(rec.getChild("id",noNamespace) != null)
				{
					accessnumber=getContent(rec.getChildText("id",noNamespace));
					record.put("ACCESSNUMBER", accessnumber);
					System.out.println("ACCESSNUMBER FROM id field");
				}
				else if(rec.getChild("identifier",dcNamespace) != null)
				{
					accessnumber=getContent(rec.getChildText("identifier",dcNamespace));
					record.put("ACCESSNUMBER", accessnumber);
					System.out.println("ACCESSNUMBER FROM dc:identifier field");
				}
				
				if(rec.getChild("type",dcNamespace) != null)
				{
					record.put("documentType", rec.getChildText("type",dcNamespace));
				}
				
				if(rec.getChild("title",dcNamespace) != null)
				{
					record.put("TITLE", DataLoadDictionary.mapEntity(rec.getChildText("title",dcNamespace)));
				}
				
			}
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+accessnumber);
			e.printStackTrace();

		}
		return record;
			
	}
	
	private void outputKnovelDeleteRecord(HashMap singleRecord, PrintWriter out, String lastModify)
	{
		String type=null;
		String accessnumber=null;
		String mid=null;
		try
		{
			if(singleRecord.get("documentType")!=null)
			{
				type = (String)singleRecord.get("documentType");
			}
			
			if(singleRecord.get("ACCESSNUMBER")!=null)
			{
				accessnumber = (String)singleRecord.get("ACCESSNUMBER");					
			}
			
			out.println(accessnumber+"|"+type+"\n");
		}
		catch(Exception e)
		{
			System.out.println("there is an exception on delete file "+filename);
			e.printStackTrace();
		}
		
	}

	private void outputKnovelRecord(HashMap singleRecord, PrintWriter out, String lastModify,String database)
	{
		StringBuffer outputBuffer = new StringBuffer();
		try
		{
			// M_ID
			if(database!=null && database.equalsIgnoreCase("knc"))
			{
				outputBuffer.append(singleRecord.get("MID"));
			}
			else if (database!=null && database.equalsIgnoreCase("kna"))
			{
				outputBuffer.append(singleRecord.get("KNA_MID"));
			}
			else
			{
				System.out.println("no database found");
			}
			outputBuffer.append(DELIM);
			
			// DATABASE
			outputBuffer.append(database);
			outputBuffer.append(DELIM);

			// FULLTEXT_LINK
			if(singleRecord.get("FULLTEXTLINK")!=null)
			{
				outputBuffer.append(singleRecord.get("FULLTEXTLINK"));
			}
			outputBuffer.append(DELIM);

			// SOURCE_FILE
			if(filename!=null && singleRecord.get("FILENAME")!=null)
			{				
				outputBuffer.append(filename+"/"+(String)singleRecord.get("FILENAME"));
			}
			else if(singleRecord.get("FILENAME")!=null)
			{
				outputBuffer.append((String)singleRecord.get("FILENAME"));
			}
			outputBuffer.append(DELIM);

			//FILE_ACTION
			
			outputBuffer.append("create");			
			outputBuffer.append(DELIM);

			// CREATION_TIME
			
			if(singleRecord.get("UPDATEDTIME")!=null)
			{
				outputBuffer.append((String)singleRecord.get("UPDATEDTIME"));
			}
			outputBuffer.append(DELIM);

			// ACCESSNUMBER
			
			if(singleRecord.get("ACCESSNUMBER")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ACCESSNUMBER"));					
			}
			outputBuffer.append(DELIM);

			// BOOK_COVER                                         

			if(singleRecord.get("ICON")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ICON"));
			}
			outputBuffer.append(DELIM);

			// LOGO

			if(singleRecord.get("LOGO")!=null)
			{
				outputBuffer.append((String)singleRecord.get("LOGO"));
			}
			outputBuffer.append(DELIM);

			// DOC_TYPE

			if(singleRecord.get("documentType")!=null)
			{
				outputBuffer.append((String)singleRecord.get("documentType"));
			}
			outputBuffer.append(DELIM);

			// DOI
			
			if(singleRecord.get("DOI")!=null)
			{
				outputBuffer.append(getContent((String)singleRecord.get("DOI")));
			}
			outputBuffer.append(DELIM);

			// PII
			if(singleRecord.get("PII")!=null)
			{
				outputBuffer.append(getContent((String)singleRecord.get("PII")));
			}
			outputBuffer.append(DELIM);

			// OAI

			if(singleRecord.get("OAI")!=null)
			{
				outputBuffer.append(getContent((String)singleRecord.get("OAI")));
			}
			outputBuffer.append(DELIM);

			// ISBN
			if(singleRecord.get("ISBN")!=null)
			{
				outputBuffer.append(getContent((String)singleRecord.get("ISBN")));
			}
			outputBuffer.append(DELIM);
			// EISBN
			if(singleRecord.get("EISBN")!=null)
			{
				outputBuffer.append(getContent((String)singleRecord.get("EISBN")));
			}
			outputBuffer.append(DELIM);

			// LANGUAGE

			if(singleRecord.get("LANGUAGE")!=null)
			{
				outputBuffer.append((String)singleRecord.get("LANGUAGE"));
			}
			outputBuffer.append(DELIM);

			// TITLE

			if(singleRecord.get("TITLE")!=null)
			{
				outputBuffer.append((String)singleRecord.get("TITLE"));
			}
			outputBuffer.append(DELIM);

			// AUTHOR

			if(singleRecord.get("AUTHOR")!=null)
			{
				outputBuffer.append((String)singleRecord.get("AUTHOR"));
			}
			outputBuffer.append(DELIM);
			
			// AFFILIATION

			if(singleRecord.get("AFFILIATION")!=null)
			{
				outputBuffer.append((String)singleRecord.get("AFFILIATION"));
			}
			outputBuffer.append(DELIM);

			// PUBLISHER

			if(singleRecord.get("PUBLISHER")!=null)
			{
				outputBuffer.append((String)singleRecord.get("PUBLISHER"));
			}
			outputBuffer.append(DELIM);

			// DOC_FORMAT
			if(singleRecord.get("FORMAT")!=null)
			{
				outputBuffer.append((String)singleRecord.get("FORMAT"));
			}
			outputBuffer.append(DELIM);

			// PUBLISH_DATE
			if(singleRecord.get("ISSUEDATE")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ISSUEDATE"));
			}
			outputBuffer.append(DELIM);

			// PARENTID
			if(singleRecord.get("PARENTID")!=null)
			{
				outputBuffer.append((String)singleRecord.get("PARENTID"));
			}
			outputBuffer.append(DELIM);

			// JOURNAL_NAME
			if(singleRecord.get("JOURNAL_NAME")!=null)
			{
				outputBuffer.append((String)singleRecord.get("JOURNAL_NAME"));
			}
			outputBuffer.append(DELIM);

			// JOURNAL_SUBNAME
			if(singleRecord.get("JOURNAL_SUBNAME")!=null)
			{
				outputBuffer.append((String)singleRecord.get("JOURNAL_SUBNAME"));
			}
			outputBuffer.append(DELIM);

			// VOLUME
			if(singleRecord.get("VOLUME")!=null)
			{
				outputBuffer.append((String)singleRecord.get("VOLUME"));
			}
			outputBuffer.append(DELIM);

			// ISSUE
			if(singleRecord.get("ISSUE")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ISSUE"));
			}
			
			outputBuffer.append(DELIM);
			
			//ABSTRACT
			if(singleRecord.get("ABSTRACT")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ABSTRACT"));
			}
			outputBuffer.append(DELIM);
			
			//SUBJECT
			if(singleRecord.get("SUBJECT")!=null)
			{
				outputBuffer.append((String)singleRecord.get("SUBJECT"));
			}
			outputBuffer.append(DELIM);
			
			// ACCESS_RIGHT
			if(singleRecord.get("ACCESSRIGHT")!=null)
			{
				outputBuffer.append((String)singleRecord.get("ACCESSRIGHT"));
			}
			
			outputBuffer.append(DELIM);
			
			//TABLE_OF_CONTENT
			if(singleRecord.get("FULLTEXTTOC")!=null)
			{
				outputBuffer.append((String)singleRecord.get("FULLTEXTTOC"));
			}
			outputBuffer.append(DELIM);
						
			//COPYRIGHT
			if(singleRecord.get("COPYRIGHT")!=null)
			{
				outputBuffer.append((String)singleRecord.get("COPYRIGHT"));
			}
			outputBuffer.append(DELIM);			

			// LOAD_NUMBER
			//System.outputBuffer.appendln("loadNumber="+loadNumber);
			outputBuffer.append(loadNumber);
			outputBuffer.append(DELIM);
			
			// LAST MODIFY
			outputBuffer.append(this.lastModify);
			outputBuffer.append(DELIM);
			
			//************ added for chapter infomation at 03/16/2018 ***********
			/*
			// parenteisbn
			outputBuffer.append((String)singleRecord.get("PARENTEISBN"));
			outputBuffer.append(DELIM);
			
			//parentisbn
			outputBuffer.append((String)singleRecord.get("PARENTISBN"));
			outputBuffer.append(DELIM);
			
			//sourcename
			outputBuffer.append((String)singleRecord.get("SOURCENAME"));
			outputBuffer.append(DELIM);
			
			//pagerange
			outputBuffer.append((String)singleRecord.get("PAGERANGE"));
			outputBuffer.append(DELIM);
			
			//coverage
			outputBuffer.append((String)singleRecord.get("COVERAGE"));
			outputBuffer.append(DELIM);
			
			//parentfilename
			outputBuffer.append((String)singleRecord.get("PARENTFILENAME"));
			outputBuffer.append(DELIM);
			
			//parenttilename
			outputBuffer.append((String)singleRecord.get("PARENTTITLENAME"));
			outputBuffer.append(DELIM);
			*/
			String outString = outputBuffer.toString().replaceAll("\n","");
			
			//only output record when database is knc or kna
			if(database!=null && (database.equalsIgnoreCase("knc") || database.equalsIgnoreCase("kna")))
			{
				out.println(outString);
				//System.out.println(outString);
			}
		}
		catch(Exception e)
		{
			System.out.println("there is an exception on file "+filename);
			e.printStackTrace();
		}
	}

	private HashMap getRecord(Element rec,String filename)
	{
		HashMap record = null;
		String accessnumber = "0";
		if(rec==null)
		{
			System.out.println("REC is null");
		}
		
		try
		{
			if(rec!=null)
			{
				String namespaceURI = rec.getNamespace().getURI();
				if(namespaceURI!=null && namespaceURI.length()>0)
				{
					noNamespace = Namespace.getNamespace("","http://www.w3.org/2005/Atom");
				}
				record = new HashMap();

				record.put("MID","knc_" + new GUID().toString());
				
				//added for kna record by hmo at 5/11/2017
				record.put("KNA_MID","kna_" + new GUID().toString());
				//record.put("DATABASE","knc");
				
				if(rec.getChild("audience",dctermsNamespace) != null)
				{
					List audiences = rec.getChildren("audience",dctermsNamespace);
					for(int i=0;i<audiences.size();i++)
					{
						Element audience = (Element)audiences.get(i);
						String audienceContent = audience.getTextTrim();
						
						if(audienceContent!=null && audienceContent.equalsIgnoreCase("academic"))
						{
							//System.out.println("KNA_DATABASE");
							record.put("KNA_DATABASE","kna");
						}
						if(audienceContent!=null && audienceContent.equalsIgnoreCase("corporate"))
						{
							//System.out.println("KNC_DATABASE");
							record.put("KNC_DATABASE","knc");
						}
					}
					
				}
				
				if(rec.getChildren("link") != null)
				{
					List links = rec.getChildren("link",noNamespace);
					getLinkElements(links,record);
				}
				
				if(filename != null)
				{					
					record.put("FILENAME", filename);
					//System.out.println("SOURCEFILE="+filename);
				}

				if(rec.getChild("updated",noNamespace) != null)
				{
					//System.out.println("UPDATEDTIME"+rec.getChildText("updated"));
					Timestamp updated = convertStringToTimestamp(rec.getChildText("updated",noNamespace));
					record.put("UPDATEDTIME", updated.toString());
				}

				if(rec.getChild("id",noNamespace) != null)
				{
					accessnumber=getContent(rec.getChildText("id",noNamespace));
					record.put("ACCESSNUMBER", accessnumber);
					//System.out.println("ACCESSNUMBER"+accessnumber);
				}

				if(rec.getChild("icon",noNamespace) != null)
				{
					//System.out.println("ICON"+rec.getChildText("icon",noNamespace));
					record.put("ICON", rec.getChildText("icon",noNamespace));
				}

				if(rec.getChild("logo",noNamespace) != null)
				{
					//System.out.println(rec.getChildText("logo",noNamespace));
					record.put("LOGO", rec.getChildText("logo",noNamespace));
				}

				
				if(rec.getChild("type",dcNamespace) != null)
				{
					//System.out.println("DOCUMENTTYPY "+rec.getChildText("type",dcNamespace));
					record.put("documentType", rec.getChildText("type",dcNamespace));
				}
				
				if(rec.getChildren("identifier",dcNamespace) != null)
				{
					List identifiers = rec.getChildren("identifier",dcNamespace);
					getIdentifiers(identifiers,record);
				}
				
				if(rec.getChild("language",dcNamespace) != null)
				{
					//System.out.println("LANGUAGE "+rec.getChildText("language",dcNamespace));
					record.put("LANGUAGE", rec.getChildText("language",dcNamespace));
				}
				
				if(rec.getChild("title",dcNamespace) != null)
				{
					//System.out.println("TITLE "+rec.getChildText("title",dcNamespace));
					record.put("TITLE", DataLoadDictionary.mapEntity(rec.getChildText("title",dcNamespace)));
				}
				
				if(rec.getChildren("creator",dcNamespace) != null)
				{
					List authors = rec.getChildren("creator",dcNamespace);
					getAuthors(authors,record);
				}
				
				if(rec.getChildren("contributor",dcNamespace) != null)
				{
					List affilliations = rec.getChildren("contributor",dcNamespace);
					getAffiliations(affilliations,record);
				}
				
				if(rec.getChild("publisher",dcNamespace) != null)
				{				
					record.put("PUBLISHER", DataLoadDictionary.mapEntity(rec.getChildText("publisher",dcNamespace)));
				}
				
				if(rec.getChildren("format",dcNamespace) != null)
				{
					List formats = rec.getChildren("format",dcNamespace);
					getFormats(formats,record);
				}
				
				if(rec.getChild("rights",dcNamespace) != null)
				{
					//System.out.println("COPYRIGHT "+rec.getChildText("rights",dcNamespace));
					record.put("COPYRIGHT", DataLoadDictionary.mapEntity(rec.getChildText("rights",dcNamespace)));
				}
				
				if(rec.getChild("issued",dctermsNamespace) != null)
				{
					//System.out.println("ISSUEDATE "+rec.getChildText("issued",dctermsNamespace));
					record.put("ISSUEDATE", rec.getChildText("issued",dctermsNamespace));
				}
				
				if(rec.getChild("abstract",dctermsNamespace) != null && rec.getChildText("abstract",dctermsNamespace).length()>0)
				{
					//System.out.println("ABSTRACT "+rec.getChildText("abstract",dctermsNamespace));
					record.put("ABSTRACT", DataLoadDictionary.mapEntity(rec.getChildText("abstract",dctermsNamespace).replaceAll("\n","<br/>").replaceAll("\t"," ")));
				}
				else
				{
					//System.out.println("NO ABSTRACT ");
					if(rec.getChild("content",noNamespace) != null)
					{
						
						String fullText = rec.getChildText("content",noNamespace);
						if(fullText!=null)
						{
							fullText = fullText.replaceAll("\n","<br/>");
						}
						//System.out.println("FULLTEXT="+fullText);
						record.put("ABSTRACT", DataLoadDictionary.mapEntity(fullText));
					}
				}
				
				
				if(rec.getChild("subject",dcNamespace) != null)
				{
					List subjects = rec.getChildren("subject",dcNamespace);
					getSubjects(subjects,record);
				}
				
				
				
				if(rec.getChild("accessRights",dctermsNamespace) != null)
				{
					//System.out.println("ACCESSRIGHT "+rec.getChildText("accessRights",dctermsNamespace));
					record.put("ACCESSRIGHT", rec.getChildText("accessRights",dctermsNamespace));
				}
				
				if(rec.getChild("isPartOf",dctermsNamespace) != null)
				{
					List isPartOfList = rec.getChildren("isPartOf",dctermsNamespace);
					for(int i=0;i<isPartOfList.size();i++)
					{
						Element isPartOf = (Element)isPartOfList.get(i);
						String isPartOfText = isPartOf.getTextTrim();
						
						if(isPartOfText != null && isPartOfText.indexOf("KNOVEL_CID")>0)
						{
							//System.out.println("PARENTID "+isPartOfText.replaceAll("urn:KNOVEL_CID:",""));
							record.put("PARENTID", isPartOfText.replaceAll("urn:KNOVEL_CID:",""));
						}
						
						if (isPartOfText != null && isPartOfText.indexOf(":EISBN:")>0)
						{
							//System.out.println("PARENTEISBN "+isPartOfText.replaceAll("urn:EISBN:", ""));
							record.put("PARENTEISBN", isPartOfText.replaceAll("urn:EISBN:", ""));
						}
						
						if (isPartOfText != null && isPartOfText.indexOf(":ISBN:")>0)
						{
							//System.out.println("PARENTISBN "+isPartOfText.replaceAll("urn:ISBN:", ""));
							record.put("PARENTISBN", isPartOfText.replaceAll("urn:ISBN:", ""));
						}
					}
					
				}
				
				if(rec.getChildren("bibliographicCitation",dctermsNamespace) != null)
				{
					Element bibliographicCitations = rec.getChild("bibliographicCitation",dctermsNamespace);
					getBibliographicCitation(bibliographicCitations,record);
				}
				
				/*
				if(rec.getChild("content",noNamespace) != null)
				{
					
					String fullText = rec.getChildText("content",noNamespace);
					if(fullText!=null)
					{
						fullText = fullText.replaceAll("\n","<br/>");
					}
					//System.out.println("FULLTEXT="+fullText);
					record.put("FULLTEXT", DataLoadDictionary.mapEntity(fullText));
				}
				*/
				

				if(rec.getChild("sourceFileName",knovelNamespace) != null)
				{
					Element sourceFileName = rec.getChild("sourceFileName",knovelNamespace);
					String sourceFileNameText = rec.getChildText("sourceFileName",knovelNamespace);
					String pageRange = sourceFileName.getAttributeValue("pagerange");
					if(sourceFileNameText!=null)
					{
						record.put("SOURCENAME", DataLoadDictionary.mapEntity(sourceFileNameText));
					}
					
					if(pageRange!=null)
					{
						record.put("PAGERANGE", pageRange);
					}
				}
				
				if(rec.getChild("coverage",dcNamespace) != null)
				{
					List coverage = rec.getChildren("coverage",dcNamespace);
					record.put("COVERAGE",getCoverage(coverage));
				}
			}
					
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+accessnumber);
			e.printStackTrace();

		}
		return record;
	}
	
	private String getCoverage(List coverages) throws Exception
	{
		StringBuffer coveragebuffer = new StringBuffer();
	
		for(int i=0;i<coverages.size();i++)
		{
			Element coverage = (Element)coverages.get(i);
			String coverageText = coverage.getTextTrim();
			if(coverageText!=null)
			{
				coveragebuffer.append(coverageText);
				if(i<coverages.size()-1)
				{
					coveragebuffer.append(AUDELIMITER);
				}
			}
		}
		return coveragebuffer.toString();
	}
	
	private void getBibliographicCitation(Element bibliographicCitations,HashMap record) throws Exception
	{
		if(bibliographicCitations!=null)
		{
			String bibliographicCitationText = bibliographicCitations.getTextTrim();
			if(bibliographicCitationText!=null)
			{
				//System.out.println("bibliographicCitationText1="+bibliographicCitationText);
				bibliographicCitationText = java.net.URLDecoder.decode(bibliographicCitationText);
				//System.out.println("bibliographicCitationText2="+bibliographicCitationText);
				String[] bibs = bibliographicCitationText.split("&rft.");
				for(int i=0;i<bibs.length;i++)
				{
					String bibsText = bibs[i];
					if(bibsText!=null && bibsText.indexOf("=")>0)
					{
						//System.out.println("bibsText="+bibsText);
						String[] bibsArray=bibsText.split("=");
						if(bibsArray.length>=2)
						{
							if(bibsArray[0].indexOf("jtitle")>-1)
							{
								//System.out.println("TITLE="+bibsArray[1]);
								record.put("JOURNAL_NAME",DataLoadDictionary.mapEntity(bibsArray[1]));
							}
							else if(bibsArray[0].indexOf("atitle")>-1)
							{
								//System.out.println("SUBTITLE="+bibsArray[1]);
								record.put("JOURNAL_SUBNAME",DataLoadDictionary.mapEntity(bibsArray[1]));
							}
							else if(bibsArray[0].indexOf("volume")>-1)
							{
								//System.out.println("VOLUME="+bibsArray[1]);
								record.put("VOLUME",(bibsArray[1]));
							}
							else if(bibsArray[0].indexOf("issue")>-1)
							{
								//System.out.println("ISSUE="+bibsArray[1]);
								record.put("ISSUE",(bibsArray[1]));
							}
							else if(bibsArray[0].indexOf("date")>0)
							{
								//System.out.println("DATE="+bibsArray[1]);
								record.put("DATE",(bibsArray[1]));
							}
						}
					}
				}
			}
		}
	}
			
	
		
	private void getSubjects(List subjects,HashMap record) throws Exception
	{
		StringBuffer subjectbuffer = new StringBuffer();
		Set subjectSet = new LinkedHashSet();
		for(int i=0;i<subjects.size();i++)
		{
			Element subject = (Element)subjects.get(i);
			String subjectText = subject.getTextTrim();
			if(subjectText!=null)
			{
				subjectText = subjectText.replace("knovel.com/","");
				String[] subjectArray = subjectText.split("/");
				for(int j=0;j<subjectArray.length;j++)
				{
					//only use the first term
					//String singleSubject = subjectArray[j];
					String singleSubject = subjectArray[0];
					if(!subjectSet.contains(singleSubject))
					{
						if(!this.excludedSubjectList.contains(singleSubject))
						{
							//System.out.println(singleSubject);
							subjectSet.add(singleSubject);	
						}
					}
					break;
				}
				
			}
		}
		Object [] y = subjectSet.toArray(new String[0]);
		for(int i=0;i<y.length;i++)
		{
			subjectbuffer.append((String)y[i]);
			if(i<y.length-1)
			{
				subjectbuffer.append(AUDELIMITER);
			}
		}
		record.put("SUBJECT",subjectbuffer.toString());
	}
		
	private void getFormats(List formats, HashMap record) throws Exception
	{
		StringBuffer formatbuffer = new StringBuffer();
		for(int i=0;i<formats.size();i++)
		{
			Element format = (Element)formats.get(i);
			String formatText = format.getTextTrim();
			if(formatText!=null)
			{
				formatbuffer.append(formatText+AUDELIMITER);
			}
		}
		//System.out.println("FORMAT= "+ formatbuffer.toString());
		record.put("FORMAT",formatbuffer.toString());
	}
		
	private void getAuthors(List authors,HashMap record) throws Exception
	{
		
	    StringBuffer authorbuffer = new StringBuffer();
		for(int i=0;i<authors.size();i++)
		{
			Element author = (Element)authors.get(i);
			String authorText = author.getTextTrim();
			if(authorText!=null)
			{
				//System.out.println("AUHTOR1="+authorText);
				//System.out.println("AUHTOR2="+DataLoadDictionary.mapEntity(authorText));
				authorbuffer.append(DataLoadDictionary.mapEntity(authorText)+AUDELIMITER);
			}
		}
		//System.out.println("AUTHOR= "+ authorbuffer.toString());
		record.put("AUTHOR",authorbuffer.toString());
	}
	
	private void getAffiliations(List affiliations,HashMap record) throws Exception
	{
		
	    StringBuffer affiliationBuffer = new StringBuffer();
		for(int i=0;i<affiliations.size();i++)
		{
			Element affiliation = (Element)affiliations.get(i);
			String affiliationText = affiliation.getTextTrim();
			if(affiliationText!=null)
			{
				affiliationBuffer.append(DataLoadDictionary.mapEntity(affiliationText)+AUDELIMITER);
			}
		}
		//System.out.println("AFFILIATION= "+ affiliationBuffer.toString());
		record.put("AFFILIATION",affiliationBuffer.toString());
	}
		
	private void getIdentifiers(List identifiers,HashMap record) throws Exception
	{
		for(int i=0;i<identifiers.size();i++)
		{
			Element identifier = (Element)identifiers.get(i);
			String identifierText = identifier.getTextTrim();
			if(identifierText!=null && identifierText.indexOf("KNOVEL_CID")>-1)
			{
				record.put("KNOVEL_CID",identifierText);
			}
			
			if(identifierText!=null && identifierText.indexOf(":ISBN")>-1)
			{
				record.put("ISBN",identifierText);
			}
			
			if(identifierText!=null && identifierText.indexOf(":EISBN")>-1)
			{
				record.put("EISBN",identifierText);
			}
			
			if(identifierText!=null && identifierText.indexOf("oai")>-1)
			{
				record.put("OAI",identifierText);
			}
			
			if(identifierText!=null && identifierText.indexOf("PII")>-1)
			{
				record.put("PII",identifierText);
			}
			
			if(identifierText!=null && identifierText.indexOf("info")>-1)
			{
				//System.out.println("DOI="+identifierText);
				record.put("DOI",identifierText);
			}
		}

	}
		
	private void getLinkElements(List links,HashMap record) throws Exception
	{
				
		for(int i=0;i<links.size();i++)
		{
			Element link = (Element)links.get(i);
			String href = link.getAttributeValue("href");
			String rel = link.getAttributeValue("rel");
			
			if(rel==null)
			{
				if(href!=null)
				{
					record.put("FULLTEXTLINK",href);
				}
			}
			else if(rel !=null && rel.equals("fulltoc"))
			{
				//getFullTextToc(href,record);
				if(bucketName !=null && bucketName.length() >0)
				{
					//HH 03/21/2016 get fulltoc from S3 bucket
					getFullTextTocFromS3(href,record);
				}
				else
				getFullTextToc(href,record);
			}	
			else if(rel !=null && rel.equals("parent"))
			{
				if(href!=null)
				{
					record.put("PARENTFILENAME",href);
				}
				if(link.getTextTrim()!=null)
				{
					record.put("PARENTTITLENAME",link.getTextTrim());
				}
			}	
		}	
	}
	
	private void getChapterLinkElement(Element root,HashMap record,StringBuffer tocBuffer) throws Exception
	{
		List links = root.getChildren("link");		
		for(int i=0;i<links.size();i++)
		{
			Element link = (Element)links.get(i);
			String href = link.getAttributeValue("href");
			System.out.println("href= "+href);
			String rel = link.getAttributeValue("rel");
			System.out.println("rel= "+rel);
			String title = link.getAttributeValue("title");
			System.out.println("title= "+title);
			String text = link.getTextTrim();
			System.out.println("text= "+text);
			
			if(rel==null)
			{
				if(href!=null)
				{
					record.put("FULLTEXTLINK",href);
				}
			}
			else if(rel !=null && rel.equals("subsection"))
			{
				//getFullTextToc(href,record);
				if(bucketName !=null && bucketName.length() >0)
				{
					//HH 03/21/2016 get fulltoc from S3 bucket
					getFullTextTocFromS3(href,record);
				}
				else
					getFullTextToc(href,record);
			}	
			else if(rel !=null && rel.equals("parent"))
			{
				if(href!=null)
				{
					record.put("PARENTFILENAME",href);
				}
				if(link.getTextTrim()!=null)
				{
					record.put("PARENTTITLENAME",link.getTextTrim());
				}
			}
			
		}	
	}
	
	private String getContent(String input)
	{
		String output = "";
		if(input != null)
		{
			output=input.substring(input.lastIndexOf(":")+1);
		}
		return output;
	}
	
	private void getTOC(Element root,StringBuffer tocBuffer) throws Exception
	{
		if(root!=null)
		{
			List links=root.getChildren("link",noNamespace);
			for(int i=0;i<links.size();i++)
			{
				Element link = (Element)links.get(i);
				String tocFilename = link.getAttributeValue("href");
				//System.out.println("SUBSECTIONFILENAME="+tocFilename);
				String rel = link.getAttributeValue("rel");
				//System.out.println("TOCREL="+rel);
				String title = link.getAttributeValue("title");
				//System.out.println("TOCTITLE="+title);
				String sourceFileName = link.getAttributeValue("sourceFileName",knovelNamespace);
				//System.out.println("TOCSOURCEFILENAME="+sourceFileName);
				String pageRange = link.getAttributeValue("sourceFilePageRange",knovelNamespace);
				//System.out.println("PAGERANGE="+pageRange);
				String text = link.getTextTrim();
				if(rel!=null && rel.equals("subsection"))
				{
					//toc title        				
					if(title!=null)
					{
						tocBuffer.append(title);
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection filename
					if(tocFilename!=null)
					{
						tocBuffer.append(tocFilename);
						//************ added for chapter infomation at 03/16/2018 ***********
						//processChapter(tocFilename);
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection pdf filename
					if(sourceFileName!=null)
					{
						tocBuffer.append(sourceFileName);
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection pagerange
					if(pageRange!=null)
					{
						tocBuffer.append(pageRange);
					}
					tocBuffer.append(IDDELIMITER);    					
				}
				tocBuffer.append(AUDELIMITER); 
				
				if(link.getChild("link")!=null && link.getChild("link").getAttributeValue("title")==null)
				{
					System.out.println("TEXT="+text);
					getTOC(link,tocBuffer);
				}
				
			}
		}
	}
	
	private void getChapterTOC(Element root,StringBuffer tocBuffer) throws Exception
	{
		if(root!=null)
		{
			List links=root.getChildren("link",noNamespace);
			for(int i=0;i<links.size();i++)
			{
				Element link = (Element)links.get(i);
				String tocFilename = link.getAttributeValue("href");
				//System.out.println("SUBSECTIONFILENAME="+tocFilename);
				String rel = link.getAttributeValue("rel");
				//System.out.println("TOCREL="+rel);
				String title = link.getAttributeValue("title");
				//System.out.println("TOCTITLE="+title);
				String sourceFileName = link.getAttributeValue("sourceFileName",knovelNamespace);
				//System.out.println("TOCSOURCEFILENAME="+sourceFileName);
				String pageRange = link.getAttributeValue("sourceFilePageRange",knovelNamespace);
				//System.out.println("PAGERANGE="+pageRange);
				String text = link.getTextTrim();
				if(rel!=null && rel.equals("subsection"))
				{
					//toc title        				
					if(title!=null)
					{
						tocBuffer.append(title);
					}
					else if(text!=null)
					{
						tocBuffer.append(text);
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection filename
					if(tocFilename!=null)
					{
						BufferedReader in = null;
															
						if(this.path!=null && this.path.length()>0)
						{
							
							in = new BufferedReader(new InputStreamReader(new FileInputStream(this.path+"/"+tocFilename)));
						}
						else
						{
							
							in = new BufferedReader(new InputStreamReader(new FileInputStream(tocFilename)));
						}
						
						tocBuffer.append(tocFilename);
						SAXBuilder builder = new SAXBuilder();
		        		builder.setExpandEntities(false);
		        		Document chapterDoc = builder.build(in);
		        		Element croot = chapterDoc.getRootElement();
		        		if(croot!=null)
		        		{
		        			getChapterTOC(croot,tocBuffer);
		        		}
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection pdf filename
					if(sourceFileName!=null)
					{
						tocBuffer.append(sourceFileName);
					}
					tocBuffer.append(IDDELIMITER);
					
					//subsection pagerange
					if(pageRange!=null)
					{
						tocBuffer.append(pageRange);
					}
					tocBuffer.append(IDDELIMITER);    					
				}
				tocBuffer.append(AUDELIMITER); 
				
				if(link.getChild("link")!=null && link.getChild("link").getAttributeValue("title")==null)
				{
					System.out.println("TEXT="+text);
					getChapterTOC(link,tocBuffer);
				}
				
			}
		}
	}
	
	private void processChapter(String fileName) throws Exception
	{
		BufferedReader in = null;
	    Document chapterDoc=null;
	    StringBuffer chapterBuffer = new StringBuffer();
        try
        {
           
            if(fileName!=null)
            {
            	
            	if(this.path!=null && this.path.length()>0)
            	{
            		in = new BufferedReader(new FileReader(this.path+"/"+fileName));
            	}
            	else
            	{
            		in = new BufferedReader(new FileReader(fileName));
            	}
                //System.out.println(bookCount+" - "+ ++chapterCount + " - "+(++totalChapterCount)+": FILENAME="+fileName);
                SAXBuilder builder = new SAXBuilder();
        		builder.setExpandEntities(false);
        		chapterDoc = builder.build(in);
        		Element root = chapterDoc.getRootElement();
        		if(root!=null)
        		{
        			HashMap chapter_output_record = getRecord(root,fileName);
        			StringBuffer chapterTocs = new StringBuffer();         			
        			//getChapterLinkElement(root,chapter_output_record,chapterTocs);
        			getChapterTOC(root,chapterTocs);
        			System.out.println("Chapter=== "+chapterTocs.toString());
        			chapter_output_record.put("FULLTEXTTOC", chapterTocs.toString());
        			chapter_output_record.put("documentType", "Chapter");
        			if(chapter_output_record != null)
        			{
        				if(chapter_output_record.get("KNC_DATABASE")!=null && ((String)chapter_output_record.get("KNC_DATABASE")).equalsIgnoreCase("knc"))
        				{
        					outputKnovelRecord(chapter_output_record,this.out_chapter,null,"knc");
        				}
        				
        				if(chapter_output_record.get("KNA_DATABASE")!=null && ((String)chapter_output_record.get("KNA_DATABASE")).equalsIgnoreCase("kna"))
        				{
        					outputKnovelRecord(chapter_output_record,this.out_chapter,null,"kna");
        				}
        			}     			
        		}
            }
        }
        catch(Exception e)
        {
        	System.out.println("Exception: "+bookCount+" - "+chapterCount + "-" + (++totalExceptionCount) + ": FILENAME="+fileName);
        	e.printStackTrace();
        }
        finally
        {
        	System.out.println("================================");
        }
	}
	
	private void getFullTextToc(String href,HashMap record) throws Exception
	{
		BufferedReader in = null;
	    String filename = href;
	    Document fullDocToc=null;
	    StringBuffer tocBuffer = new StringBuffer();
        try
        {
           
            if(filename!=null)
            {
            	
            	if(this.path!=null && this.path.length()>0)
            	{
            		in = new BufferedReader(new FileReader(this.path+"/"+filename));
            	}
            	else
            	{
            		in = new BufferedReader(new FileReader(filename));
            	}
                
                SAXBuilder builder = new SAXBuilder();
        		builder.setExpandEntities(false);
        		fullDocToc = builder.build(in);
        		Element root = fullDocToc.getRootElement();
        		//Element entry = root.getChild("entry");
        		getTOC(root,tocBuffer);
        			
            }
            else
            {
                System.out.println("fulltexttoc file "+filename+" is not available");
            }
        }        
        catch(Exception e)
        {
        	System.out.println("Exception on file "+filename);
        	e.printStackTrace();
        }
        finally
        {
        	//System.out.println("FULLTEXTTOC= "+ tocBuffer.toString());
        	if(tocBuffer.length()>0)
        	{
        		//System.out.println("FULLTEXTTOC"+tocBuffer.toString());
        		record.put("FULLTEXTTOC",tocBuffer.toString());
        	}
        	try
        	{
        		in.close();
        	}
        	catch(Exception e)
            {
            	e.printStackTrace();
            }
        }
	}
	
	//HH 03/21/2016 get fulltextTOC from S3 bucket

	private void getFullTextTocFromS3(String href,HashMap record) throws Exception
	{
		BufferedReader in = null;
		String filename = href;
		Document fullDocToc=null;
		StringBuffer tocBuffer = new StringBuffer();
		try
		{

			if(filename!=null)
			{
				if(this.path!=null && this.path.length()>0)
				{
					knovelS3.getFileContentFromS3(this.path+"/"+filename);
					in = new BufferedReader(new InputStreamReader(knovelS3.objectData));
				}
				else
				{
					knovelS3.getFileContentFromS3(filename);
					in = new BufferedReader(new InputStreamReader(knovelS3.objectData));
				}
				//System.out.println("FULLTEXTFILENAME="+this.path+"/"+filename);
				SAXBuilder builder = new SAXBuilder();
				builder.setExpandEntities(false);
				fullDocToc = builder.build(in);
				Element root = fullDocToc.getRootElement();
				//Element entry = root.getChild("entry");
				getTOC(root,tocBuffer);

			}
			else
			{
				System.out.println("fulltexttoc file "+filename+" is not available");
			}
		}        
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//System.out.println("FULLTEXTTOC= "+ tocBuffer.toString());
			if(tocBuffer.length()>0)
			{
				//System.out.println("FULLTEXTTOC"+tocBuffer.toString());
				record.put("FULLTEXTTOC",tocBuffer.toString());
			}
			try
			{
				in.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	 public void setExcludedSubjectList(Connection con)
	 {
	    	Statement stmt = null;
	        ResultSet rs = null;
	        List<String> subjectList = new ArrayList<>();
	        try
	        {
	            stmt = con.createStatement();

	            rs = stmt.executeQuery("select subject from SUBJECT_EXCLUDED");
	            while (rs.next())
	            {
	                String subject = rs.getString("subject");
	                if(subject != null)
	                {
	                	subject = subject.toLowerCase().replace(" ", "-");
	                	//System.out.println("SUBJECT="+subject);
	                	subjectList.add(subject);
	                }
	            }

	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {

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
	        this.excludedSubjectList=subjectList;
	       
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

