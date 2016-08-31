package org.ei.dataloading.upt.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.zip.ZipEntry;


/*import java.util.zip.ZipFile;*/    //original
import org.apache.commons.compress.archivers.zip.*;   //HH 08/04/2015 to fix issue of Patent zip file's headers 
import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.GUID;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.ei.common.Constants;

public class PatentXmlReader
{
	private HashMap output_record = null;
	private Document doc = null;
	private Iterator rec =null;
	private Perl5Util perl = new Perl5Util();
	private HashMap dupMap = new HashMap();
	private InventorComp INVENTORS_COMP = new InventorComp();
	//public static final char IDDELIMITER = (char)31;
	//public static final char Constants.GROUPDELIMITER = (char) 30;
	//public static final char IDDELIMITER = Constants.IDDELIMITER;
	//public static final char AUDELIMITER = Constants.AUDELIMITER;
	//public static final char GROUPDELIMITER = Constants.GROUPDELIMITER;
	public static final char DELIM = '\t';
	

	/*
	 * If there is field delimiter that is 2 or more values for one field eg, A;B;C,
	 * use 'AUDELIMITER' between A, B and C
	 * If there is subfields in one value of the field use 'IDDELIMITER' ,
	 * for eg. A:1;B:2;C:3 USE AUDELIMITER between A, B and C
	 * To separate fields use 'IDDELIMITER between A and 1, B and 2 and C and 3.
	 */

    public Element bibliographic_data;
    public Element abstractData;
    PrintWriter patentsOut;
	PrintWriter patentsRefOut;
    PrintWriter nonPatentsRefOut;
    PrintWriter updatePatentsOut;
	PrintWriter updatePatentsRefOut;
    PrintWriter updateNonPatentsRefOut;
	static OracleMap dbMap;
	static String loadNumber;
	static String filename;
	static String xmlFileName;
	static String patentNumber;
	static String patentCountry;
	static HashMap langMap = new HashMap();
	static HashMap kindMap = new HashMap();
	{
		langMap.put("eng","English");
		langMap.put("fre","French");
		langMap.put("ger","German");
		langMap.put("spa","Spanish");
		langMap.put("jap","Japanese");
		langMap.put("chi","Chinese");
		langMap.put("rus","Russian");

		kindMap.put("A1","Patent Application Publication");
		kindMap.put("A2","Patent Application Publication(Republication)");
		kindMap.put("A3","PUBL. OF SEARCH REPORT");
		kindMap.put("A8","CORRECTED TITLE PAGE OF AN EP-A DOCUMENT");//may not use
		kindMap.put("A9","Patent Application Publication(Corrected Publication)");
		kindMap.put("B1","Patent");
		kindMap.put("B2","Patent");
		kindMap.put("B8","CORRECTED FRONT PAGE OF AN EP-B DOCUMENT");
		kindMap.put("B9","COMPLETE REPRINT OF AN EP-B DOCUMENT");
		kindMap.put("C1","Reexamination Certificate");
		kindMap.put("C2","Reexamination Certificate");
		kindMap.put("C3","Reexamination Certificate");
		kindMap.put("E","Reissue Patent");
		kindMap.put("E1","Reissue Patent");//may not use
		kindMap.put("H","Statutory Invention Registration (SIR)");
		kindMap.put("H1","Statutory Invention Registration (SIR)");//may not use
		kindMap.put("P1","Plant Patent Application Publication");
		kindMap.put("P2","Plant Patent");
		kindMap.put("P3","Plant Patent");
		kindMap.put("P4","Plant Patent Application Publication(Republication)");
		kindMap.put("P9","Plant Patent Application Publication(Corrected Publication)");
		kindMap.put("S","Design Patent");
		kindMap.put("S1","Design Patent");//may not use

	}

	 public static void main(String[] args)
	        throws Exception
	 {
		filename = args[0];
		String outputFile = args[1];
		loadNumber = args[2];
		String url = "jdbc:oracle:thin:@206.137.75.53:1521:EIDB1";
		String username = "db_patent";
		String password = "nynoru6j";

		if(args.length>6)
		{
			url=args[3];
			username = args[4];
			password = args[5];
		}

		PatentXmlReader r = new PatentXmlReader();

		try
		{
			r.init(outputFile, loadNumber, url, username, password);
			File inputFiles = new File(filename);
			if(inputFiles.exists())
			{
				r.fileReader(inputFiles);
			}
			else
			{
				System.out.println("File does not exist");
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			r.close();
			System.exit(1);
		}
	}

	private void fileReader(File root) throws Exception
	{
		String zipfiles[] = root.list();
		Arrays.sort(zipfiles, new DataFileComp());

		for (int i = 0; i < zipfiles.length; i++)
		{
			String path = root.getPath() + File.separator + zipfiles[i];
			File current_file = new File(path);
			System.out.println("filename= "+current_file.getName());
			ZipFile zipFile = new ZipFile(path);
			/*Enumeration entries = zipFile.entries();*/   // original
			Enumeration entries = zipFile.getEntries();    
			while (entries.hasMoreElements())
			{
				BufferedReader xmlReader = null;
				/*ZipEntry entry = (ZipEntry)entries.nextElement();*/   //original
				ZipArchiveEntry entry = (ZipArchiveEntry)entries.nextElement();
				try
				{
					xmlReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
					patentXmlParser(xmlReader);
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
		}
	}

	class DataFileComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			String file1 = (String)o1;
			String file2 = (String)o2;
			String[] file1Parts = file1.split(".zip");
			String[] file2Parts = file2.split(".zip");

			int date1 = Integer.parseInt(file1Parts[0]);
			int date2 = Integer.parseInt(file2Parts[0]);

			if(date1 < date2)
			{
				return -1;
			}
			else if(date1 > date2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}


	private void close()
	{
		System.out.println("Close..............");

		if (patentsOut != null)
			patentsOut.close();

		if (patentsRefOut != null)
			patentsRefOut.close();

		if (nonPatentsRefOut != null)
			nonPatentsRefOut.close();

		if (updatePatentsOut != null)
			updatePatentsOut.close();

		if (updatePatentsRefOut != null)
			updatePatentsRefOut.close();

		if (updateNonPatentsRefOut != null)
			updateNonPatentsRefOut.close();

		if (dbMap != null)
		{
			try
			{
				dbMap.close();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

	private void init(String outFile, String loadNumber,String URL,String User,String Password)
	{
		try
		{
			System.out.println("init..............");

			dbMap = new OracleMap(URL,User,Password);
			dbMap.open();
			if(patentsOut==null)
				patentsOut 	= new PrintWriter(new FileWriter("out"+File.separator+outFile+"_master_"+loadNumber+".out"), true);
			if(patentsRefOut == null)
				patentsRefOut = new PrintWriter(new FileWriter("out"+File.separator+outFile+"_patref_"+loadNumber+".out"), true);
			if(nonPatentsRefOut == null)
				nonPatentsRefOut = new PrintWriter(new FileWriter("out"+File.separator+outFile+"_nonpatref_"+loadNumber+".out"), true);
			if(updatePatentsOut==null)
				updatePatentsOut 	= new PrintWriter(new FileWriter("out"+File.separator+"corr_"+outFile+"_master_"+loadNumber+".out"), true);
			if(updatePatentsRefOut == null)
				updatePatentsRefOut = new PrintWriter(new FileWriter("out"+File.separator+"corr_"+outFile+"_patref_"+loadNumber+".out"), true);
			if(updateNonPatentsRefOut == null)
				updateNonPatentsRefOut = new PrintWriter(new FileWriter("out"+File.separator+"corr_"+outFile+"_nonpatref_"+loadNumber+".out"), true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	public void patentXmlParser(Reader r) throws Exception
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		this.doc = builder.build(r);
		Element patentRoot = doc.getRootElement();
		this.bibliographic_data = patentRoot.getChild("bibliographic-data");
		this.abstractData = patentRoot.getChild("abstract");
		output_record = getRecord(patentRoot);
		patentNumber = (String)output_record.get("PR_DOCID_DOC_NUMBER");
		System.out.println("Patent Number:"+ patentNumber);
		patentCountry = (String)output_record.get("PR_DOCID_COUNTRY");
		String patentKind = (String)output_record.get("PR_DOCID_KIND");
		if(!checkAvailable(patentNumber,patentCountry,patentKind))
		{
			outputUPTRecord(output_record,patentsOut);
			outputPatentRef(output_record,patentsRefOut);
			outputNonPatentRef(output_record,nonPatentsRefOut);
		}
		else
		{
			outputUPTRecord(output_record,updatePatentsOut);
			outputPatentRef(output_record,updatePatentsRefOut);
			outputNonPatentRef(output_record,updateNonPatentsRefOut);
		}
	}

	private boolean enoughData(HashMap singleRecord)
	{
		List titleList = (List)singleRecord.get("TITLE");
		if(titleList != null && titleList.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void outputUPTRecord(HashMap singleRecord, PrintWriter out)
	{
		//String patentNumber = "0";
		if(!enoughData(singleRecord))
		{
			System.out.println("Patent not included due to lack of data:"+(String)singleRecord.get("PR_DOCID_DOC_NUMBER"));
			return;
		}

		try
		{
			// M_ID
			out.print(singleRecord.get("MID"));
			out.print(DELIM);

			// PN
			if(singleRecord.get("PR_DOCID_DOC_NUMBER")!=null)
			{
				patentNumber = (String)singleRecord.get("PR_DOCID_DOC_NUMBER");
				out.print(patentNumber);
			}
			//System.out.println("PN "+patentNumber);
			out.print(DELIM);

			// AC
			String ac =  (String)singleRecord.get("PR_DOCID_COUNTRY");
			if(ac!=null)
			{
				out.print(ac);
			}
			else
			{
				System.out.println("no authCode for record "+ patentNumber);
			}
			out.print(DELIM);

			// KC
			String kind = (String)singleRecord.get("PR_DOCID_KIND");
			if(kind !=null)
			{
				out.print(kind);
			}
			else
			{
				System.out.println("no kindCode for record "+ patentNumber);
			}
			out.print(DELIM);

			// KD
			if(singleRecord.get("PR_DOCID_NAME")!=null)
			{
				out.print((String)singleRecord.get("PR_DOCID_NAME"));
			}
			else if(kind!= null)
			{
				if(kindMap.containsKey(kind))
				{
					out.print((String)kindMap.get(kind));
				}
			}
			out.print(DELIM);

			// TI * PRE_TI * GER_TI * LTN_TI
			if(singleRecord.get("TITLE")!=null)
			{
				List titleList = (List)singleRecord.get("TITLE");
				StringBuffer eng_title = new StringBuffer();
				StringBuffer fre_title = new StringBuffer();
				StringBuffer ger_title = new StringBuffer();
				StringBuffer ltn_title = new StringBuffer();
				for(int i=0; i<titleList.size(); i++)
				{
					HashMap title = (HashMap)titleList.get(i);
					if(title.get("IT_LANG")!=null)
					{
						if(((String)title.get("IT_LANG")).equalsIgnoreCase("ENG"))
						{
							if(eng_title.length()>0)
							{
								eng_title.append(Constants.AUDELIMITER);
							}
							eng_title.append(title.get("IT_CONTENT"));
						}
						else if(((String)title.get("IT_LANG")).equalsIgnoreCase("FRE"))
						{
							if(fre_title.length()>0)
							{
								fre_title.append(Constants.AUDELIMITER);
							}
							fre_title.append(title.get("IT_CONTENT"));
						}
						else if(((String)title.get("IT_LANG")).equalsIgnoreCase("GER"))
						{
							if(ger_title.length()>0)
							{
								ger_title.append(Constants.AUDELIMITER);
							}
							ger_title.append(title.get("IT_CONTENT"));
						}
						else if(((String)title.get("IT_LANG")).equalsIgnoreCase("LTN"))
						{
							if(ltn_title.length()>0)
							{
								ltn_title.append(Constants.AUDELIMITER);
							}
							ltn_title.append(title.get("IT_CONTENT"));
						}
					}
				}
				if(eng_title.length()>0)
				{
					out.print(substituteChars(eng_title.toString()));
				}
				out.print(DELIM);
				if(fre_title.length()>0)
				{
					out.print(substituteChars(fre_title.toString()));
				}
				out.print(DELIM);
				if(ger_title.length()>0)
				{
					out.print(substituteChars(ger_title.toString()));
				}
				out.print(DELIM);
				if(ltn_title.length()>0)
				{
					out.print(substituteChars(ltn_title.toString()));
				}
				out.print(DELIM);
			}
			else
			{
				out.print(DELIM);
				out.print(DELIM);
				out.print(DELIM);
				out.print(DELIM);
			}

			// FD

			String filing_date = (String)singleRecord.get("AP_DOCID_DATE");
			if(filing_date!=null)
			{
				out.print(filing_date);
			}
			out.print(DELIM);

			// FY

			if(filing_date!=null && filing_date.length()>4)
			{
				out.print(filing_date.substring(0,4));
			}
			out.print(DELIM);

			// PD

			String publication_date = (String)singleRecord.get("PR_DOCID_DATE");
			if(publication_date!=null)
			{
				out.print(publication_date);
			}
			out.print(DELIM);

			// PY

			if(publication_date!=null && publication_date.length()>4)
			{
				out.print(publication_date.substring(0,4));
			}
			out.print(DELIM);

			// XPB_DT
			if(singleRecord.get("DATE_OF_PUBLIC")!=null)
			{
				//System.out.println("DATE_OF_PUBLIC "+(String)singleRecord.get("DATE_OF_PUBLIC"));
				out.print((String)singleRecord.get("DATE_OF_PUBLIC"));
			}

			out.print(DELIM);

			// DAN

			if(singleRecord.get("AP_DOCID_DOC_NUMBER")!=null)
			{
				out.print(singleRecord.get("AP_DOCID_DOC_NUMBER"));
			}
			out.print(DELIM);

			// DUN

			out.print(DELIM);

			// AIN

			HashMap related_documents = (HashMap)singleRecord.get("RELATED-DOCUMENT");
			if(related_documents!=null && related_documents.get("DOCNUMBER") != null)
			{
				String ainString = (String)related_documents.get("DOCNUMBER");
				if(ainString.length()>499)
				{
					ainString = ainString.substring(0,ainString.lastIndexOf(Constants.AUDELIMITER,999));
					System.out.println("AIN Field too long for record "+ac+" "+patentNumber);
				}

				out.print(ainString);
			}
			out.print(DELIM);

			// AID

			if(related_documents!=null && related_documents.get("DATE")!= null)
			{

				String aidString = (String)related_documents.get("DATE");
				if(aidString.length()>3999)
				{
					aidString = aidString.substring(0,aidString.lastIndexOf(Constants.AUDELIMITER,3999));
					System.out.println("AID Field too long for record "+ac+" "+patentNumber);
				}

				out.print(aidString);
			}
			out.print(DELIM);

			// AIC

			if(related_documents!=null && related_documents.get("COUNTRY")!= null)
			{
				String aicString =	(String)related_documents.get("COUNTRY");
				if(aicString.length()>3999)
				{
					aicString = aicString.substring(0,aicString.lastIndexOf(Constants.AUDELIMITER,3999));
					System.out.println("AIC Field too long for record "+ac+" "+patentNumber);
				}
				out.print(aicString);
			}
			out.print(DELIM);

			// AIK

			if(related_documents!=null && related_documents.get("KIND")!= null)
			{
				out.print((String)related_documents.get("KIND"));
			}
			out.print(DELIM);

			// IPC

			//System.out.println("IPC "+singleRecord.get("IPC"));
			if(singleRecord.get("IPC")!=null)
			{
				out.print(singleRecord.get("IPC"));
			}
			out.print(DELIM);

			// FIC
			if(singleRecord.get("FIC")!=null)
			{
				out.print(singleRecord.get("FIC"));
			}
			out.print(DELIM);

			// ICC
			if(singleRecord.get("ICC")!=null)
			{
				out.print(singleRecord.get("ICC"));
			}
			out.print(DELIM);

			// ISC
			if(singleRecord.get("ISC")!=null)
			{
				out.print(singleRecord.get("ISC"));
			}
			out.print(DELIM);

			// ECL
			StringBuffer main_ecla_text = new StringBuffer();
			StringBuffer main_ecla = new StringBuffer();
			StringBuffer main_ecla_class = new StringBuffer();
			StringBuffer main_ecla_subclass = new StringBuffer();
			StringBuffer ltn_title = new StringBuffer();

			if(singleRecord.get("CLASSIFICATION_ECLA")!=null)
			{
				List eclaList = (List)singleRecord.get("CLASSIFICATION_ECLA");
				HashMap eccTable = new HashMap();
				HashMap escTable = new HashMap();
				for(int i=0; i<eclaList.size(); i++)
				{
					HashMap ecla = (HashMap)eclaList.get(i);
					String ecla_text = (String)ecla.get("TEXT");
					if(main_ecla.length()>0)
					{
						main_ecla.append(Constants.AUDELIMITER);
					}
					if(ecla_text!=null)
					{
						if(main_ecla_text.length()>0)
						{
							main_ecla_text.append(Constants.AUDELIMITER);
						}
						main_ecla_text.append(ecla_text);
					}

					String eclaMainclass = (String)ecla.get("MAINCLASS");

					if(eclaMainclass!=null)
					{

						if(!eccTable.containsKey(eclaMainclass))   // get rid of duplicate term
						{
							eccTable.put(eclaMainclass,eclaMainclass);
							if(main_ecla_class.length()>0)
							{
								main_ecla_class.append(Constants.AUDELIMITER);
							}
							main_ecla_class.append((String)ecla.get("MAINCLASS"));
						}
					}

					String eclaSubclass = (String)ecla.get("SUBCLASS");
					if(eclaSubclass!=null)
					{

						if(!escTable.containsKey(eclaSubclass)) // get rid of duplicate term
						{
							escTable.put(eclaSubclass,eclaSubclass);

							if(main_ecla_subclass.length()>0)
							{
								main_ecla_subclass.append(Constants.AUDELIMITER);
							}
							main_ecla_subclass.append((String)ecla.get("SUBCLASS"));
						}
					}

					if((eclaMainclass!=null) && (eclaSubclass!=null))
					{
						main_ecla.append(eclaMainclass+eclaSubclass);
					}
					else if(ecla_text!=null)
					{
						main_ecla.append(ecla_text);
					}

					//System.out.println("MAINCLASS "+eclaMainclass);
					//System.out.println("SUBCLASS "+eclaSubclass);
					//System.out.println("ecla_text "+ecla_text);


				}
			}
			//System.out.println("main_ecla "+main_ecla.toString());
			//System.out.println("PN "+patentNumber);
			if(main_ecla.length()>0)
			{
				out.print(main_ecla.toString());
			}
			else
			{
				out.print(main_ecla_text.toString());
			}
			//System.out.println("ECLA "+main_ecla_subclass.toString());
			out.print(DELIM);

			// FEC
			out.print(DELIM);

			// ECC
			out.print(main_ecla_class.toString());
			out.print(DELIM);

			// ESC
			out.print(main_ecla_subclass.toString());
			out.print(DELIM);

			// UCL
			if(singleRecord.get("MC_TEXT")!=null)
			{
				out.print((String)singleRecord.get("MC_TEXT"));
			}
			out.print(DELIM);

			// UCC
			if(singleRecord.get("MC_CLASS")!=null)
			{
				out.print((String)singleRecord.get("MC_CLASS"));
			}
			out.print(DELIM);

			// USC
			if(singleRecord.get("MC_SUBCLASS")!=null)
			{
				out.print((String)singleRecord.get("MC_SUBCLASS"));
			}
			out.print(DELIM);

			// FS
			if(singleRecord.get("FIELD_OF_SEARCH")!=null)
			{
				StringBuffer fosBuffer = new StringBuffer();
				List field_of_search = (List)singleRecord.get("FIELD_OF_SEARCH");
				if(field_of_search != null)
				{
					for(int i=0;i<field_of_search.size();i++)
					{
						HashMap fosMap = (HashMap)field_of_search.get(i);

						if(fosMap != null && fosMap.get("CN_TEXT")!=null)
						{
							if(i>0)
							{
								fosBuffer.append(Constants.AUDELIMITER);
							}

							fosBuffer.append((String)fosMap.get("CN_TEXT"));
						}
					}
				}
				out.print(fosBuffer.toString());

			}
			out.print(DELIM);


			// INV
			if(singleRecord.get("INVENTOR")!=null)
			{
				StringBuffer name = new StringBuffer();
				StringBuffer address = new StringBuffer();
				StringBuffer country = new StringBuffer();

				List inventors = (List)singleRecord.get("INVENTOR");
				Collections.sort(inventors, INVENTORS_COMP);
				/*
				* Possibly need to sort inventors basked on sequence here.
				*/

				for(int i=0;i<inventors.size();i++)
				{
					HashMap inventor = (HashMap)inventors.get(i);
					if(inventor.get("NAME")!=null ||inventor.get("LAST_NAME")!=null || inventor.get("FIRST_NAME")!=null)
					{
						if(inventor.get("NAME")!=null)
						{
							name.append((String)inventor.get("NAME"));
						}
						else
						{
							if(inventor.get("LAST_NAME")!=null)
							{
								name.append((String)inventor.get("LAST_NAME"));
								if(inventor.get("FIRST_NAME")!=null)
								{
									name.append(", ");
								}
							}
							if(inventor.get("FIRST_NAME")!=null)
							{
								name.append((String)inventor.get("FIRST_NAME"));
							}
						}
					}
					else
					{
						continue;
					}

					if(i+1 < inventors.size())
					{
						name.append(Constants.AUDELIMITER);
					}

					if(inventor.get("ADDRESS1")!=null || inventor.get("CITY")!=null || inventor.get("STATE")!=null || inventor.get("POSTCODE")!=null)
					{
						if(inventor.get("ADDRESS1")!=null)
						{
							address.append((String)inventor.get("ADDRESS1"));
							if(inventor.get("CITY")!=null || inventor.get("STATE")!=null || inventor.get("POSTCODE")!=null)
							{
								address.append(", ");
							}
						}
						if(inventor.get("CITY")!=null)
						{
							address.append((String)inventor.get("CITY"));
							if(inventor.get("STATE")!=null || inventor.get("POSTCODE")!=null)
							{
								address.append(", ");
							}
						}
						if(inventor.get("STATE")!=null)
						{
							address.append((String)inventor.get("STATE"));
							if(inventor.get("POSTCODE")!=null)
							{
								address.append(", ");
							}
						}
						if(inventor.get("POSTCODE")!=null)
						{
							address.append((String)inventor.get("POSTCODE"));
						}
					}

					if(i+1 < inventors.size())
					{
						address.append(Constants.AUDELIMITER);
					}

					if(inventor.get("COUNTRY")!=null)
					{
						country.append((String)inventor.get("COUNTRY"));
					}

					if(i+1 < inventors.size())
					{
						country.append(Constants.AUDELIMITER);
					}
				}

				if(name.length()>0)
				{
					out.print(substituteChars(name.toString()));
				}
				out.print(DELIM);

			// INV_ADDR
				if(address.length()>0)
				{
					String addressString = address.toString();
					if(addressString.length()>3999)
					{
						addressString = addressString.substring(0,addressString.lastIndexOf(Constants.AUDELIMITER,3999));
						System.out.println("INV_ADDR Field too long for record "+ac+" "+patentNumber);

					}
					out.print(substituteChars(addressString));
				}
				out.print(DELIM);

			// INV_CTRY
				if(country.length()>0)
				{
					out.print(substituteChars(country.toString()));
				}
				out.print(DELIM);
			}
			else
			{
				out.print(DELIM);
				out.print(DELIM);
				out.print(DELIM);
			}


			// ASG

			if(singleRecord.get("APPLICANT")!=null)
			{
				StringBuffer ap_name = new StringBuffer();
				StringBuffer ap_address = new StringBuffer();
				StringBuffer ap_country = new StringBuffer();

				List applicants = (List)singleRecord.get("APPLICANT");
				for(int i=0;i<applicants.size();i++)
				{
					HashMap applicant = (HashMap)applicants.get(i);
					if(applicant != null && applicant.get("APP_TYPE") != null && ((String)applicant.get("APP_TYPE")).equals("applicant"))
					{
						if(applicant.get("LAST_NAME")!=null || applicant.get("FIRST_NAME")!=null || applicant.get("ORGNAME")!=null)
						{
							if(applicant.get("LAST_NAME")!=null)
							{
								ap_name.append((String)applicant.get("LAST_NAME"));
								if(applicant.get("FIRST_NAME")!=null || applicant.get("ORGNAME")!=null)
								{
									ap_name.append(", ");
								}
							}

							if(applicant.get("FIRST_NAME")!=null)
							{
								ap_name.append((String)applicant.get("FIRST_NAME"));
								if(applicant.get("ORGNAME")!=null)
								{
									ap_name.append(", ");
								}
							}

							if(applicant.get("ORGNAME")!=null)
							{
								ap_name.append((String)applicant.get("ORGNAME"));
							}
						}
						else
						{
							continue;
						}

						if(i+1 < applicants.size())
						{
							ap_name.append(Constants.AUDELIMITER);
						}

						if(applicant.get("ADDRESS1")!=null || applicant.get("CITY")!=null || applicant.get("STATE")!=null || applicant.get("POSTCODE")!=null)
						{
							if(applicant.get("ADDRESS1")!=null)
							{
								ap_address.append((String)applicant.get("ADDRESS1"));
								if(applicant.get("CITY")!=null || applicant.get("STATE")!=null || applicant.get("POSTCODE")!=null)
								{
									ap_address.append(", ");
								}
							}

							if(applicant.get("CITY")!=null)
							{
								ap_address.append((String)applicant.get("CITY"));
								if(applicant.get("STATE")!=null || applicant.get("POSTCODE")!=null)
								{
									ap_address.append(", ");
								}
							}
							if(applicant.get("STATE")!=null)
							{
								ap_address.append((String)applicant.get("STATE"));
								if(applicant.get("POSTCODE")!=null)
								{
									ap_address.append(", ");
								}
							}
							if(applicant.get("POSTCODE")!=null)
							{
								ap_address.append((String)applicant.get("POSTCODE"));
							}
						}

						if(i+1 < applicants.size())
						{
							ap_address.append(Constants.AUDELIMITER);
						}

						if(applicant.get("COUNTRY")!=null)
						{
							ap_country.append((String)applicant.get("COUNTRY"));
						}

						if(i+1 < applicants.size())
						{
							ap_country.append(Constants.AUDELIMITER);
						}
					}
				}

				if(ap_name.length()>0)
				{
					out.print(substituteChars(ap_name.toString()));
				}
				out.print(DELIM);

				// ASG_ADDR
				if(ap_address.length()>0)
				{
					out.print(substituteChars(ap_address.toString()));
				}
				out.print(DELIM);

				// ASG_CTRY
				if(ap_country.length()>0)
				{
					out.print(substituteChars(ap_country.toString()));
				}
				out.print(DELIM);
			}
			else
			{
				out.print(DELIM);
				out.print(DELIM);
				out.print(DELIM);
			}
			out.print(DELIM);

			// ATY

			if(singleRecord.get("AGENT")!=null)
			{
				StringBuffer aty_name = new StringBuffer();
				StringBuffer aty_address = new StringBuffer();
				StringBuffer aty_country = new StringBuffer();

				List agents = (List)singleRecord.get("AGENT");
				for(int i=0;i<agents.size();i++)
				{
					HashMap agent = (HashMap)agents.get(i);
					if(agent.get("REP_TYPE")!=null && ((String)agent.get("REP_TYPE")).equals("attorney"))
					{
						if(agent.get("LAST_NAME")!=null || agent.get("FIRST_NAME")!=null || agent.get("ORGNAME")!=null)
						{

							if(agent.get("LAST_NAME")!=null)
							{
								aty_name.append((String)agent.get("LAST_NAME"));
								if(agent.get("FIRST_NAME")!=null || agent.get("ORGNAME")!=null)
								{
									aty_name.append(", ");
								}
							}

							if(agent.get("FIRST_NAME")!=null)
							{
								aty_name.append((String)agent.get("FIRST_NAME"));
								if(agent.get("ORGNAME")!=null)
								{
									aty_name.append(", ");
								}
							}

							if(agent.get("ORGNAME")!=null)
							{
								aty_name.append((String)agent.get("ORGNAME"));
							}
						}
						else
						{
							continue;
						}

						if(i+1 < agents.size())
						{
							aty_name.append(Constants.AUDELIMITER);
						}

						if(agent.get("ADDRESS1")!=null || agent.get("CITY")!=null || agent.get("STATE")!=null || agent.get("POSTCODE")!=null)
						{
							if(aty_address.length()>0)
							{
								aty_address.append(Constants.AUDELIMITER);
							}

							if(agent.get("ADDRESS1")!=null)
							{
								aty_address.append((String)agent.get("ADDRESS1"));
								if(agent.get("CITY")!=null || agent.get("STATE")!=null || agent.get("POSTCODE")!=null)
								{
									aty_address.append(", ");
								}
							}

							if(agent.get("CITY")!=null)
							{
								aty_address.append((String)agent.get("CITY"));
								if(agent.get("STATE")!=null || agent.get("POSTCODE")!=null)
								{
									aty_address.append(", ");
								}
							}
							if(agent.get("STATE")!=null)
							{
								aty_address.append((String)agent.get("STATE"));
								if(agent.get("POSTCODE")!=null)
								{
									aty_address.append(", ");
								}
							}
							if(agent.get("POSTCODE")!=null)
							{
								aty_address.append((String)agent.get("POSTCODE"));
							}
						}

						if(i+1 < agents.size())
						{
							aty_address.append(Constants.AUDELIMITER);
						}

						if(agent.get("COUNTRY")!=null)
						{
							if(aty_country.length()>0)
							{
								aty_country.append(Constants.AUDELIMITER);
							}
							aty_country.append((String)agent.get("COUNTRY"));
						}

						if(i+1 < agents.size())
						{
							aty_country.append(Constants.AUDELIMITER);
						}
					}
				}

				if(aty_name.length()>0)
				{
					//System.out.println("ATY_NAME "+aty_name.toString());
					out.print(substituteChars(aty_name.toString()));
				}
				out.print(DELIM);


				// ATY_ADDR
				if(aty_address.length()>0)
				{
					//System.out.println("ADDRESS "+aty_address.toString());
					out.print(substituteChars(aty_address.toString()));
				}
				out.print(DELIM);

				// ATY_CTRY
				if(aty_country.length()>0)
				{
					out.print(substituteChars(aty_country.toString()));
				}
				out.print(DELIM);
			}
			else
			{
				out.print(DELIM);
				out.print(DELIM);
				out.print(DELIM);
			}


			// PE
			if(singleRecord.get("PE_NAME")!=null)
			{
				out.print(substituteChars((String)singleRecord.get("PE_NAME")));
			}
			out.print(DELIM);

			// AE
			if(singleRecord.get("AE_NAME")!= null)
			{
				out.print(substituteChars((String)singleRecord.get("AE_NAME")));
			}
			out.print(DELIM);

			// PI
			StringBuffer pcBuffer = new StringBuffer();
			if(singleRecord.get("PRIORITY_CLAIMS")!= null)
			{
				List priority_claims = (List)singleRecord.get("PRIORITY_CLAIMS");
				for(int i=0;i<priority_claims.size();i++)
				{
					HashMap pcMap = (HashMap)priority_claims.get(i);
					if(pcMap != null)
					{
						if(pcBuffer.length()>0)
						{
							pcBuffer.append(Constants.AUDELIMITER);
						}
						if(pcMap.get("COUNTRY") != null)
						{
							pcBuffer.append((String)pcMap.get("COUNTRY"));
						}
						pcBuffer.append(Constants.IDDELIMITER);
						if(pcMap.get("DOC_NUMBER") != null)
						{
							
							pcBuffer.append((String)pcMap.get("DOC_NUMBER"));
						}
						pcBuffer.append(Constants.IDDELIMITER);
						if(pcMap.get("DATE") != null)
						{
							
							pcBuffer.append((String)pcMap.get("DATE"));
						}

					}
				}
			}

			if(pcBuffer.length()>0)
			{
				String pcString = pcBuffer.toString();
				if(pcString.length()>3999)
				{
					pcString = pcString.substring(0,pcString.lastIndexOf(Constants.AUDELIMITER,3999));
					System.out.println("PI Field too long for record "+ac+" "+patentNumber);
				}
				out.print(pcString);
			}
			out.print(DELIM);

			// DT

			String docType = getDocType(ac,kind);
			if(docType != null)
			{
				out.print(docType);
			}
			out.print(DELIM);

			// LA
			String lang = null;
			if(singleRecord.get("BIB_LANG")!=null)
			{
				lang = (String)singleRecord.get("BIB_LANG");
			}
			else if(singleRecord.get("LANGUAGE_OF_FILING")!=null)
			{
				lang = (String)singleRecord.get("LANGUAGE_OF_FILING");
			}
			if(lang != null)
			{

				if(langMap.containsKey(lang))
				{
					lang = (String)langMap.get(lang);
				}
				else
				{
					System.out.println("missing language mapping "+lang);
				}

				out.print(lang);
			}
			out.print(DELIM);

			// PCT_PNM
			/******* no mapping *********/
			out.print(DELIM);

			// PCT_ANM
			/******* no mapping *********/
			out.print(DELIM);

			// DS
			StringBuffer dsBuffer = new StringBuffer();
			if(singleRecord.get("DESIGNATIONSTATE")!=null)
			{
				List dsList = (List)singleRecord.get("DESIGNATIONSTATE");
				for(int i=0;i<dsList.size();i++)
				{
					if(dsList.get(i)!=null)
					{
						if(dsBuffer.length()>0)
						{
							dsBuffer.append(Constants.AUDELIMITER);
						}
						dsBuffer.append((String)dsList.get(i));
					}
				}
				out.print(dsBuffer.toString());
			}
			out.print(DELIM);


			// AB
			if(singleRecord.get("ABSTRACT_DATA")!=null)
			{
				out.print(substituteChars((String)singleRecord.get("ABSTRACT_DATA")));
			}
			else
			{
				out.print("QQ");
			}
			out.print(DELIM);

			// OAB

			/********* not available **************/
			out.print("QQ");
			out.print(DELIM);

			// REF_CNT
			int patentReferenceCount = 0;
			int nonpatentReferenceCount = 0;
			if(singleRecord.get("CITATION")!=null)
			{
				List ref = (List)singleRecord.get("CITATION");
				for(int i=0;i<ref.size();i++)
				{
					HashMap citationMap = (HashMap)ref.get(i);
					if(citationMap != null)
					{
						if(citationMap.get("DOC_NUMBER")!=null)
						{
							patentReferenceCount++;
						}
						if(citationMap.get("NON_PATENT_CITATION")!=null)
						{
							nonpatentReferenceCount++;
						}

					}

				}
				out.print(patentReferenceCount);
			}
			out.print(DELIM);

			// CIT_CNT
			out.print("0");
			out.print(DELIM);

			// MD
			//out.print(DELIM);



			// NP_CNT

			out.print(nonpatentReferenceCount);
			out.print(DELIM);

			// UPDATE_NUMBER
			//out.print(loadNumber);
			//out.print(DELIM);

			// IPC8
			StringBuffer ipcBuffer = new StringBuffer();
			if(singleRecord.get("CLASSIFICATION_IPCR")!=null)
			{
				List classification_ipcr_list = (List)singleRecord.get("CLASSIFICATION_IPCR");
				for(int i=0;i<classification_ipcr_list.size();i++)
				{
					HashMap ipcMap = (HashMap)classification_ipcr_list.get(i);
					if(ipcMap != null && ipcMap.get("TEXT")!=null)
					{
						ipcBuffer.append((String)ipcMap.get("TEXT"));
					}
				}


				if(ipcBuffer.length()<4000)
				{
					out.print(ipcBuffer.toString());
				}
				else
				{
					out.print(ipcBuffer.substring(0,4000));
				}
			}
			//System.out.println("IPC= "+ipcBuffer.toString());
			out.print(DELIM);

			// IPC8_2
			if(ipcBuffer.length()>4000)
			{
				if(ipcBuffer.length()>8000)
				{
					out.print(ipcBuffer.substring(4000,8000));
				}
				else
				{
					out.print(ipcBuffer.substring(4000));
				}
			}

			out.print(DELIM);

			// LOAD_NUMBER
			out.print(loadNumber);					
			
			out.print(DELIM);
			
			//*************************************************************************//
			//*************                                           *****************//
			//************* Following items are new from VTW 1/7/2016 *****************//
			//*************                                           *****************//
			//*************************************************************************//
			
			///*
			//CLASSIFICATION_CPC
			
			if(singleRecord.get("CLASSIFICATION_CPC")!=null)
			{
				//out.print((String)singleRecord.get("CLASSIFICATION_CPC"));
				
				List classification_cpc_list = (List)singleRecord.get("CLASSIFICATION_CPC");
				StringBuffer cpcBuffer = new StringBuffer();
				for(int i=0;i<classification_cpc_list.size();i++)
				{
					HashMap cpcMap = (HashMap)classification_cpc_list.get(i);
					StringBuffer singleCpcBuffer = new StringBuffer();
					if(cpcMap!=null)
					{
						String cpcSection = (String)cpcMap.get("SECTION");
						String cpcClass = (String)cpcMap.get("CLASS");
						String cpcSubclass = (String)cpcMap.get("SUBCLASS");
						String cpcMaingroup = (String)cpcMap.get("MAIN_GROUP");
						String cpcSubgroup = (String)cpcMap.get("SUBGROUP");
						String cpcText = (String)cpcMap.get("TEXT");
						
						if(cpcSection!=null)
						{
							singleCpcBuffer.append(cpcSection);
						}
						
						if(cpcClass!=null)
						{
							singleCpcBuffer.append(cpcClass);
						}
						
						if(cpcSubclass!=null)
						{
							singleCpcBuffer.append(cpcSubclass);
						}
						
						if(cpcMaingroup!=null)
						{
							singleCpcBuffer.append(cpcMaingroup);
						}
						
						if(cpcSubgroup!=null)
						{
							singleCpcBuffer.append("/"+cpcSubgroup);
						}
						
						if(singleCpcBuffer.length()<1 && cpcText!=null)
						{
							singleCpcBuffer.append(cpcText);							
						}
												
					}
					if(singleCpcBuffer.length()>0)
					{
						cpcBuffer.append(singleCpcBuffer);
						if(i<classification_cpc_list.size()-1)
						{
							cpcBuffer.append(Constants.IDDELIMITER);
						}
					}
					
				}


				if(cpcBuffer.length()<4000)
				{
					out.print(cpcBuffer.toString());
				}
				else
				{
					out.print(cpcBuffer.substring(0,4000));
				}
			}
			
			out.print(DELIM);
			
			//MAIN_FAMILY
			
			if(singleRecord.get("MAIN_FAMILY")!=null)
			{
				out.print((String)singleRecord.get("MAIN_FAMILY"));
			}
			
			out.print(DELIM);
			
			//COMPLETE_FAMILY
			
			if(singleRecord.get("COMPLETE_FAMILY")!=null)
			{
				out.print((String)singleRecord.get("COMPLETE_FAMILY"));
			}
			
			out.print(DELIM);
			
			//SAMPLY_FAMILY
			
			if(singleRecord.get("SIMPLE_FAMILY")!=null)
			{
				out.print((String)singleRecord.get("SIMPLE_FAMILY"));
			}
			
			out.print(DELIM);
			
			//CLAIMS
			
			if(singleRecord.get("CLAIMS")!=null)
			{
				out.print((String)singleRecord.get("CLAIMS"));
			}
			
			out.print(DELIM);
			
			//DRAWINGS
			
			if(singleRecord.get("DRAWINGS")!=null)
			{
				out.print((String)singleRecord.get("DRAWINGS"));
			}
			
			out.print(DELIM);
			
			//IMAGE
			
			if(singleRecord.get("IMAGE")!=null)
			{
				out.print((String)singleRecord.get("IMAGE"));
			}
			
			out.print(DELIM);
			
			//DESCRIPTION
			
			if(singleRecord.get("DESCRIPTION")!=null)
			{
				out.print((String)singleRecord.get("DESCRIPTION"));
			}
						
			out.print(DELIM);
			
			//MODIFIED_DATE_OF_PUBLIC
			
			if(singleRecord.get("MODIFIED_DATE_OF_PUBLIC")!=null)
			{
				out.print((String)singleRecord.get("MODIFIED_DATE_OF_PUBLIC"));
			}	
			//*/ -- end of VTW project					
			
			out.print("\n");
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+patentNumber);
			System.out.println("xmlFile Name is "+xmlFileName);
			System.out.println("zip file name is "+filename);
			e.printStackTrace();
		}
	}

	class InventorComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			Map map1 = (Map)o1;
			Map map2 = (Map)o2;

			int s1 = Integer.parseInt((String)map1.get("SEQUENCE"));
			int s2 = Integer.parseInt((String)map2.get("SEQUENCE"));
			if(s1 < s2)
			{
				return -1;
			}
			else if(s1 > s2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private void outputPatentRef(HashMap singleRecord, PrintWriter out)
	{
		String prt_pn = null;
		String prt_cy = null;
		try
		{
			if(singleRecord.get("CITATION")!=null)
			{
				List ref = (List)singleRecord.get("CITATION");
				for(int i=0;i<ref.size();i++)
				{
					HashMap citationMap = (HashMap)ref.get(i);
					if(citationMap != null && citationMap.get("DOC_NUMBER")!=null)
					{
						// REF_mid
						out.print("ref_" + new GUID().toString());
						out.print(DELIM);

						// PRT_MID
						out.print((String)singleRecord.get("MID"));
						out.print(DELIM);

						// PRT_CY
						prt_cy = (String)singleRecord.get("PR_DOCID_COUNTRY");
						// PRT_PN
						prt_pn = (String)singleRecord.get("PR_DOCID_DOC_NUMBER");

						out.print(prt_pn);
						out.print(DELIM);
						out.print(prt_cy);
						out.print(DELIM);


						// PRT_PK

						String prt_pk = (String)singleRecord.get("PR_DOCID_KIND");
						if(prt_pk != null)
						{
							out.print(prt_pk);
						}

						out.print(DELIM);
						String cit_cy = (String)citationMap.get("DI_COUNTRY");
						String cit_pn = (String)citationMap.get("DOC_NUMBER");
						String cit_ki = (String)citationMap.get("KIND");


						// CIT_PN
						if(cit_pn != null)
						{
							cit_pn = pnNormalization(cit_pn,cit_ki);
							out.print(cit_pn);
						}

						out.print(DELIM);

						// CIT_CY
						if(cit_cy != null)
						{
							out.print(cit_cy);
						}

						out.print(DELIM);
						String[] cit_mid = null;
						if(cit_pn != null &&
						   cit_cy != null)
						{
							cit_mid  = getCitMID(cit_pn,cit_cy);
							if(cit_mid != null)
							{
								out.print(cit_mid[1]);
							}
						}

						out.print(DELIM);

						// CIT_MID

						if(cit_mid != null)
						{
							out.print(cit_mid[0]);
						}

						out.print(DELIM);

						// UPT_LINK
						if(cit_mid != null)
						{
							out.print("Y");
						}
						else
						{
							out.print("N");
						}
						out.print(DELIM);

						// PRT_LINK

						out.print("Y");
						out.print(DELIM);

						// MD

						// LOAD_NUMBER

						out.print(loadNumber);
						out.print("\n");

					}

				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+prt_cy+prt_pn);
			e.printStackTrace();
		}

	}

	private void outputNonPatentRef(HashMap singleRecord,PrintWriter out)
	{
		String prt_pn = null;
		String prt_ac = null;
		try
		{
			if(singleRecord.get("CITATION")!=null)
			{
				List ref = (List)singleRecord.get("CITATION");
				for(int i=0;i<ref.size();i++)
				{
					HashMap citationMap = (HashMap)ref.get(i);
					if(citationMap != null && citationMap.get("NON_PATENT_CITATION")!=null)
					{
						// mid
						out.print("ref_" + new GUID().toString());
						out.print(DELIM);

						// PRT_MID
						out.print((String)singleRecord.get("MID"));
						out.print(DELIM);

						// PRT_PN
						prt_pn = (String)singleRecord.get("PR_DOCID_DOC_NUMBER");
						out.print(prt_pn);
						out.print(DELIM);

						// PRT_AC

						prt_ac = (String)singleRecord.get("PR_DOCID_COUNTRY");
						if(prt_ac != null)
						{
							out.print(prt_ac);
						}
						else
						{
							System.out.println("no ac for record "+prt_pn);
						}
						out.print(DELIM);

						// PRT_KC
						String prt_kc = (String)singleRecord.get("PR_DOCID_KIND");
						if(prt_kc != null)
						{
							out.print(prt_kc);
						}
						else
						{
							System.out.println("no kc for record "+prt_pn);
						}
						out.print(DELIM);

						// REF_RAW
						String non_patent_citation = (String)citationMap.get("NON_PATENT_CITATION");

						if(non_patent_citation != null)
						{


							out.print(substituteChars(non_patent_citation));
							//out.print(substituteChars("GB912066719910928GB920629519920323"));
						}
						out.print(DELIM);


						// DOI
						out.print(DELIM);

						// CPX
						out.print(DELIM);

						// INS
						out.print(DELIM);

						// C84
						out.print(DELIM);

						// IBF
						out.print(DELIM);
						out.print(DELIM);

						// LOAD_NUMBER

						out.print(loadNumber);
						out.print("\n");
					}

				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+(String)singleRecord.get("PR_DOCID_DOC_NUMBER"));
			e.printStackTrace();
		}
	}

	public HashMap getRecord(Element rec)
	{
		HashMap record = null;
		String patentNumber = "0";
		try
		{
			if(rec!=null)
			{
				record = new HashMap();

				record.put("MID","upt_" + new GUID().toString());
				if(rec.getAttributeValue("date-produced") != null)
				{
					//System.out.println("DATE_PRODUCTED"+rec.getAttributeValue("date-produced"));
					record.put("DATE_PRODUCTED", rec.getAttributeValue("date-produced"));
				}

				if(rec.getAttributeValue("schema-version") != null)
				{
					//System.out.println("SCHEMA-VERSION"+rec.getAttributeValue("schema-version"));
					record.put("SCHEMA-VERSION", rec.getAttributeValue("schema-version"));
				}

				if(rec.getAttributeValue("schema-version") != null)
				{
					//System.out.println("SCHEMA-VERSION"+rec.getAttributeValue("schema-version"));
					record.put("SCHEMA-VERSION", rec.getAttributeValue("schema-version"));
				}

				if(rec.getAttributeValue("produced-by") != null)
				{
					//System.out.println("PRODUCTED-BY"+rec.getAttributeValue("produced-by"));
					record.put("PRODUCTED-BY", rec.getAttributeValue("produced-by"));
				}

				if(rec.getAttributeValue("lang") != null)
				{
					//System.out.println(rec.getAttributeValue("lang"));
					record.put("LANG", rec.getAttributeValue("lang"));
				}

				//	abstract Group Elements


				Element abstractData  = rec.getChild("abstract");
				

				if(abstractData !=null)
				{
					// abstract data

					if(abstractData.getAttributeValue("lang") != null)
					{
						//System.out.println("ABSTRACT_LANG "+abstractData.getAttributeValue("lang"));
						record.put("ABSTRACT_LANG", abstractData.getAttributeValue("lang"));
					}

					if(abstractData.getAttributeValue("format") != null)
					{
						//System.out.println("ABSTRACT_FORMAT "+abstractData.getAttributeValue("format"));
						record.put("ABSTRACT_FORMAT", abstractData.getAttributeValue("format"));
					}

					if(abstractData.getAttributeValue("id") != null)
					{
						//System.out.println("ABSTRACT_ID "+abstractData.getAttributeValue("id"));
						record.put("ABSTRACT_ID", abstractData.getAttributeValue("id"));
					}

					if(abstractData.getChildTextTrim("p") != null)
					{
						StringBuffer abstractBuf = getMixData(abstractData.getContent(),new StringBuffer());
						record.put("ABSTRACT_DATA", abstractBuf.toString().trim());
					}
				}
				
				//description
				Element description = rec.getChild("description");
				if(description != null)
				{				
					setDescription(record,description); //OK
				}
				
				//claims
				Element claims = rec.getChild("claims");
				if(claims != null)
				{				
					setClaims(record,claims); //OK
				}
				
				//drawings
				Element drawings = rec.getChild("drawings");
				if(drawings != null)
				{				
					setDrawings(record,drawings); //OK
				}
				
				//image
				Element image = rec.getChild("image");
				if(image != null)
				{				
					setImages(record,image); //OK
				}

				//bibliographic-data
				Element bibliographic_data = rec.getChild("bibliographic-data");
				
				if(bibliographic_data != null)
				{

					//	bibliographic Group Elements
					// publication-reference
					String bib_lang = bibliographic_data.getAttributeValue("lang");
					//System.out.println("BIB_LANG "+bib_lang);
					if(bib_lang != null)
					{
						record.put("BIB_LANG",bib_lang);
					}
					Element publication_reference = bibliographic_data.getChild("publication-reference");
					if(publication_reference != null)
					{
						Element pr_document_id = publication_reference.getChild("document-id");
						if(pr_document_id != null)
						{

							//System.out.println("pr_kind= "+pr_document_id.getChildTextTrim("kind")); //OK
							String kind = pr_document_id.getChildTextTrim("kind");
							record.put("PR_DOCID_KIND", kind);

							String pr_country = pr_document_id.getChildTextTrim("country"); //OK
							//System.out.println("pr_country= "+pr_country);
							record.put("PR_DOCID_COUNTRY",pr_country);

							String pr_doc_number = pr_document_id.getChildTextTrim("doc-number"); //OK
							pr_doc_number = pnNormalization(pr_doc_number, kind);
							//System.out.println("pr_doc-number= "+pr_doc_number);
							patentNumber = pr_doc_number;
							record.put("PR_DOCID_DOC_NUMBER",pr_doc_number);



							//System.out.println("pr_date= "+pr_document_id.getChildTextTrim("date")); //OK
							record.put("PR_DOCID_DATE",pr_document_id.getChildTextTrim("date"));

							//System.out.println("pr_desc= "+pr_document_id.getChildTextTrim("name"));

							if(pr_document_id.getChild("name") != null)
							{
								record.put("PR_DOCID_NAME",getMixData(pr_document_id.getChild("name").getContent(),new StringBuffer()));
							}
						}
					}
					// application-reference

					Element application_reference = bibliographic_data.getChild("application-reference");
					if(application_reference != null)
					{
						Element ap_document_id = application_reference.getChild("document-id");
						if(ap_document_id != null)
						{
							//System.out.println("ap_country= "+ap_document_id.getChildTextTrim("country"));
							record.put("AP_DOCID_COUNTRY",ap_document_id.getChildTextTrim("country")); //OK

							//System.out.println("ap_doc-number= "+ap_document_id.getChildTextTrim("doc-number"));
							record.put("AP_DOCID_DOC_NUMBER",ap_document_id.getChildTextTrim("doc-number")); //OK

							//System.out.println("ap_kind= "+ap_document_id.getChildTextTrim("kind"));
							record.put("AP_DOCID_KIND",ap_document_id.getChildTextTrim("kind")); //OK

							//System.out.println("ap_date= "+ap_document_id.getChildTextTrim("date"));
							record.put("AP_DOCID_DATE",ap_document_id.getChildTextTrim("date")); //OK
						}
					}
					// language-of-filing

					//System.out.println("language_of_filing= "+bibliographic_data.getChildTextTrim("language-of-filing"));
					record.put("LANGUAGE_OF_FILING",bibliographic_data.getChildTextTrim("language-of-filing")); //OK

					// language-of-publication

					//System.out.println("language-of-publication= "+bibliographic_data.getChildTextTrim("language-of-publication"));
					record.put("LANGUAGE_OF_PUBLICATION",bibliographic_data.getChildTextTrim("language-of-publication")); //OK

					// priority-claims

					Element priority_claims = bibliographic_data.getChild("priority-claims");
					if(priority_claims !=null)
					{
						List priority_claim = priority_claims.getChildren("priority-claim");
						setPriority_Claims(record,priority_claim); // OK
					}

					// dates-of-public-availability
					Element dates_of_public_availability = bibliographic_data.getChild("dates-of-public-availability");
					if(dates_of_public_availability!=null)
					{
						Element unexamined_printed_without_grant = dates_of_public_availability.getChild("unexamined-printed-without-grant");
						Element modified_first_page_pub = dates_of_public_availability.getChild("modified-first-page-pub");
						if(unexamined_printed_without_grant != null)
						{
							record.put("DATE_OF_PUBLIC",unexamined_printed_without_grant.getChildTextTrim("date")); //OK
						}
						
						if(modified_first_page_pub!=null)
						{
							record.put("MODIFIED_DATE_OF_PUBLIC",modified_first_page_pub.getChildTextTrim("date")); //OK
						}
						
						
					}

					// classification-ipc
					Element c_ipc = bibliographic_data.getChild("classification-ipc");
					setClassification_ipc(c_ipc,record); //OK


					// classifications-ipcr

					Element classifications_ipcr = bibliographic_data.getChild("classifications-ipcr");
					if(classifications_ipcr != null)
					{
						List classification_ipcr = classifications_ipcr.getChildren("classification-ipcr");
						setClassifications_ipcr(record,classification_ipcr,"ipc"); //OK
					}
					// classification-national

					Element classification_national = bibliographic_data.getChild("classification-national");

					if(classification_national != null)
					{
						setClassification_national(classification_national,record); //OK
					}

					// classifications-ecla

					Element classifications_ecla = bibliographic_data.getChild("classifications-ecla");
					if(classifications_ecla != null)
					{
						List classification_ecla = classifications_ecla.getChildren("classification-ecla");
						setClassification_ecla(record,classification_ecla);//OK
					}

					// field-of-search

					Element field_of_search = bibliographic_data.getChild("field-of-search");
					if(field_of_search != null)
					{
						List fos_classification_national = field_of_search.getChildren("classification-national");
						setFos_classification_national(record,fos_classification_national); //OK
					}

					// invention-title

					List invention_title = bibliographic_data.getChildren("invention-title");
					setTitle(record,invention_title); //OK

					// references-cited
					Element references_cited = bibliographic_data.getChild("references-cited");
					if(references_cited != null)
					{
						List citation = references_cited.getChildren("citation");
						setCitation(record,citation); //OK
					}


					// related-documents

					//Seems to parse OK but what exactly is it parsing.
					Element related_documents = bibliographic_data.getChild("related-documents");
					HashMap docNumberMap = new HashMap();
					if(related_documents != null)
					{
						List related_publication = related_documents.getChildren("related-publication");
						List continuation = related_documents.getChildren("continuation");
						List continuation_in_part = related_documents.getChildren("continuation-in-part");
						List addition = related_documents.getChildren("addition");
						List division = related_documents.getChildren("division");
						List reissue = related_documents.getChildren("reissuet");
						List divisional_reissue = related_documents.getChildren("divisional-reissue");
						List reexamination = related_documents.getChildren("reexamination");
						List reexamination_reissue_merger = related_documents.getChildren("reexamination-reissue-merger");
						List substitution = related_documents.getChildren("substitution");
						List provisional_application = related_documents.getChildren("provisional-application");
						List utility_model_basis = related_documents.getChildren("utility-model-basis");
						List correction = related_documents.getChildren("correction");

						if(continuation != null)
						{
							setRelation(continuation,docNumberMap);
						}
						else
						{
							System.out.println("continuation is null");
						}
						if(continuation_in_part != null)
						{
							setRelation(continuation_in_part,docNumberMap);
						}
						if(addition != null)
						{
							setRelation(addition,docNumberMap);
						}
						if(division != null)
						{
							setRelation(division,docNumberMap);
						}
						if(reissue != null)
						{
							setRelation(reissue,docNumberMap);
						}
						if(divisional_reissue != null)
						{
							setRelation(divisional_reissue,docNumberMap);
						}
						if(reexamination != null)
						{
							setRelation(reexamination,docNumberMap);
						}
						if(reexamination_reissue_merger != null)
						{
							setRelation(reexamination_reissue_merger,docNumberMap);
						}
						if(substitution != null)
						{
							setRelation(substitution,docNumberMap);
						}
						if(provisional_application != null)
						{
							for(int i=0;i<provisional_application.size();i++)
							{
								Element provisional_application_element = (Element)provisional_application.get(i);
								Element document_id = provisional_application_element.getChild("document-id");
								if(document_id != null)
								{
									setDocID(document_id,docNumberMap);
								}
							}
						}

						if(correction != null)
						{
							for(int i=0;i<correction.size();i++)
							{
								Element correctionElement = (Element)correction.get(i);
								if(correctionElement != null)
								{
									Element document_id = (Element)correctionElement.getChild("document-id");
									setDocID(document_id,docNumberMap);
								}
							}
						}

						if(related_publication!=null)
						{
							for(int i=0;i<related_publication.size();i++)
							{
								Element related_publication_element = (Element)related_publication.get(i);

								Element document_id = related_publication_element.getChild("document-id");
								if(document_id != null)
								{
									setDocID(document_id,docNumberMap);
								}
							}
						}
						record.put("RELATED-DOCUMENT",docNumberMap);
					}

					// parties

					Element parties = bibliographic_data.getChild("parties");
					if(parties != null)
					{
						Element applicants = parties.getChild("applicants");
						if(applicants != null)
						{
							List applicant = applicants.getChildren("applicant");
							setParties(record,applicant,"APPLICANT");
						}
						Element inventors = parties.getChild("inventors");
						if(inventors != null)
						{
							List inventor = inventors.getChildren("inventor");
							setParties(record,inventor,"INVENTOR");
						}

						Element agents = parties.getChild("agents");
						if(agents != null)
						{
							List agent = agents.getChildren("agent");
							setParties(record,agent,"AGENT");
						}
					} //OK
					
					// patent-family

					Element patent_family = bibliographic_data.getChild("patent-family");
					if(patent_family != null)
					{
						setPatentFamily(record,patent_family);
					}

					// examiners
					Element examiners = bibliographic_data.getChild("examiners");
					if(examiners != null)
					{
						Element primary_examiners = examiners.getChild("primary-examiner");
						if(primary_examiners != null)
						{
							Element name = primary_examiners.getChild("name");
							if(name != null)
							{
								StringBuffer pe_name = getMixData(name.getContent(), new StringBuffer());
								record.put("PE_NAME",pe_name.toString());
							}
						}

						Element assistant_examiners = examiners.getChild("assistant-examiner");
						if(assistant_examiners != null)
						{
							Element name = assistant_examiners.getChild("name");
							if(name != null)
							{
								StringBuffer ae_name = getMixData(name.getContent(), new StringBuffer());
								record.put("AE_NAME",ae_name.toString());
							}
						}

					}//OK

					//designation-of-states
					Element designation_of_states = bibliographic_data.getChild("designation-of-states");
					List dsList = new ArrayList();
					if(designation_of_states != null)
					{
						Element designation_epc = designation_of_states.getChild("designation-epc");
						if(designation_epc != null)
						{
							Element contracting_states = designation_epc.getChild("contracting-states");
							if(contracting_states != null)
							{
								List countryList = contracting_states.getChildren("country");
								for(int i=0;i<countryList.size();i++)
								{
									if(countryList.get(i)!=null)
									{
										dsList.add(((Element)countryList.get(i)).getText());
									}
								}
								if(dsList.size()>0)
								{
									record.put("DESIGNATIONSTATE",dsList);
								}
							}
						}
					} //OK
					
					//Following fields added on 1/5/2016
					//classifications-cpc
					Element classifications_cpc = bibliographic_data.getChild("classifications-cpc");
					if(classifications_cpc != null)
					{
						List classification_cpc = classifications_cpc.getChildren("classification-cpc");
						setClassifications_ipcr(record,classification_cpc,"cpc"); //OK
					}
					
					
				}

			}
		}
		catch(Exception e)
		{
			System.out.println("Error on record "+patentNumber);
			e.printStackTrace();

		}
		return record;
	}
	
	
	private void setPatentFamily(HashMap record,Element patent_family) throws Exception
	{
		Element main_family = patent_family.getChild("main-family");
		Element complete_family = patent_family.getChild("complete-family");
		Element simple_family = patent_family.getChild("simple-family");
		if(main_family!=null)
		{
			setFamilyMember(record,main_family,"main");
		}
		
		if(complete_family!=null)
		{
			setFamilyMember(record,complete_family,"complete");
		}
		
		if(simple_family!=null)
		{
			setFamilyMember(record,simple_family,"simple");
		}
	}
	
	private void setFamilyMember(HashMap record,Element family, String type) throws Exception
	{
		List familyMemberList = family.getChildren("family-member");
		StringBuffer familyBuffer = new StringBuffer();
		if(familyMemberList!=null)
		{
			for(int i=0;i<familyMemberList.size();i++)
			{
				Element familyMember = (Element)familyMemberList.get(i);
				if(familyMember!=null && familyMember.getChild("document-id")!=null)
				{
					Element document_id = familyMember.getChild("document-id");
					String country = document_id.getChildText("country");
					String doc_number = document_id.getChildText("doc-number");
					String kind = document_id.getChildText("kind");
					String date = document_id.getChildText("date");
					if(country!=null)
					{
						familyBuffer.append(country);
					}
					familyBuffer.append(Constants.IDDELIMITER);
					if(doc_number!=null)
					{
						familyBuffer.append(doc_number);
					}
					familyBuffer.append(Constants.IDDELIMITER);
					if(kind!=null)
					{
						familyBuffer.append(kind);
					}
					familyBuffer.append(Constants.IDDELIMITER);
					if(date!=null)
					{
						familyBuffer.append(date);
					}
					familyBuffer.append(Constants.IDDELIMITER);
				}
				
				if(familyMember!=null && familyMember.getChild("application-date")!=null)
				{
					Element application_date = familyMember.getChild("application-date");
					String date = application_date.getChildText("date");
					if(date!=null)
					{
						familyBuffer.append(date);
					}
				}
				familyBuffer.append(Constants.AUDELIMITER);
			}
			if(type.equals("main"))
			{
				record.put("MAIN_FAMILY", familyBuffer.toString());
			}
			else if(type.equals("complete"))
			{
				record.put("COMPLETE_FAMILY", familyBuffer.toString());
			}
			else if(type.equals("simple"))
			{
				record.put("SIMPLE_FAMILY", familyBuffer.toString());
			}
		}
	}
	
	private void setImages(HashMap record,Element image) throws Exception
	{

		
		StringBuffer imageBuffer = new StringBuffer();
		
		if(image != null)
		{
			String file = image.getAttributeValue("file");
			String type = image.getAttributeValue("type");
			String size = image.getAttributeValue("size");
			String pages = image.getAttributeValue("page");
			if(file!=null)
			{
				imageBuffer.append(file);
			}
			imageBuffer.append(Constants.AUDELIMITER);
			if(type!=null)
			{
				imageBuffer.append(type);
			}
			imageBuffer.append(Constants.AUDELIMITER);
			if(size!=null)
			{
				imageBuffer.append(size);
			}
			imageBuffer.append(Constants.AUDELIMITER);
			if(pages!=null)
			{
				imageBuffer.append(pages);
			}
			record.put("IMAGE", imageBuffer.toString());
			
		}
	}
	
	private void setDrawings(HashMap record,Element drawings) throws Exception
	{

		StringBuffer drawingsbuffer = new StringBuffer();
		
		if(drawings != null)
		{
			List figureList = drawings.getChildren("figure");
			
			String format = drawings.getAttributeValue("format");
			String generated = drawings.getAttributeValue("generated");
			String country = drawings.getAttributeValue("country");
			String doc_number = drawings.getAttributeValue("doc-number");
			String kind = drawings.getAttributeValue("kind");
			
			if(format!=null)
			{
				drawingsbuffer.append(format);
			}
			drawingsbuffer.append(Constants.AUDELIMITER);
			if(drawingsbuffer!=null)
			{
				drawingsbuffer.append(generated);
			}
			drawingsbuffer.append(Constants.AUDELIMITER);
			if(country!=null)
			{
				drawingsbuffer.append(country);
			}
			drawingsbuffer.append(Constants.AUDELIMITER);
			if(doc_number!=null)
			{
				drawingsbuffer.append(doc_number);
			}
			drawingsbuffer.append(Constants.AUDELIMITER);
			if(kind!=null)
			{
				drawingsbuffer.append(kind);
			}
			drawingsbuffer.append(Constants.AUDELIMITER);		
		
			for(int i=0;i<figureList.size();i++)
			{
				Element figure = (Element)figureList.get(i);
				if(figure != null && figure.getChild("img")!=null)
				{
					Element img = figure.getChild("img");
				
					String he = img.getAttributeValue("he");
					String wi = img.getAttributeValue("wi");
					String file = img.getAttributeValue("file");
					String alt = img.getAttributeValue("alt");
					String img_content = img.getAttributeValue("img-content");
					String img_Format = img.getAttributeValue("img-format");
					if(he!=null && !he.equals("N/A"))
					{
						drawingsbuffer.append(he);
					}
					drawingsbuffer.append(Constants.GROUPDELIMITER);
					
					if(wi!=null && !wi.equals("N/A"))
					{
						drawingsbuffer.append(wi);
					}
					drawingsbuffer.append(Constants.GROUPDELIMITER);
					
					if(file!=null)
					{
						drawingsbuffer.append(file);
					}
					drawingsbuffer.append(Constants.GROUPDELIMITER);
					
					if(alt!=null)
					{
						drawingsbuffer.append(alt);
					}
					drawingsbuffer.append(Constants.GROUPDELIMITER);
					
					if(img_content!=null)
					{
						drawingsbuffer.append(img_content);
					}
					drawingsbuffer.append(Constants.GROUPDELIMITER);
					
					if(img_Format!=null)
					{
						drawingsbuffer.append(img_Format);
					}								
				}
				drawingsbuffer.append(Constants.IDDELIMITER);		
			}
			
			record.put("DRAWINGS", drawingsbuffer.toString());
		}
		
	}
	
	private void setClaims(HashMap record,Element claims) throws Exception
	{

		StringBuffer claimbuffer = new StringBuffer();
		
		if(claims != null)
		{
			List claimList = claims.getChildren("claim");
			String id = claims.getAttributeValue("id");
			String lang = claims.getAttributeValue("lang");
			String format = claims.getAttributeValue("format");
			String generated = claims.getAttributeValue("generated");
			String country = claims.getAttributeValue("country");
			String doc_number = claims.getAttributeValue("doc-number");
			String kind = claims.getAttributeValue("kind");
			if(id!=null)
			{
				claimbuffer.append(id);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(lang!=null)
			{
				claimbuffer.append(lang);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(format!=null)
			{
				claimbuffer.append(format);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(claimbuffer!=null)
			{
				claimbuffer.append(generated);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(country!=null)
			{
				claimbuffer.append(country);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(doc_number!=null)
			{
				claimbuffer.append(doc_number);
			}
			claimbuffer.append(Constants.AUDELIMITER);
			if(kind!=null)
			{
				claimbuffer.append(kind);
			}
			claimbuffer.append(Constants.AUDELIMITER);		
		
			for(int i=0;i<claimList.size();i++)
			{
				Element claim = (Element)claimList.get(i);
				if(claim != null && claim.getChildText("claim-text")!=null)
				{					
					claimbuffer.append(claim.getChildText("claim-text"));					
				}
				claimbuffer.append(Constants.IDDELIMITER);		
			}
			
			record.put("CLAIMS", claimbuffer.toString());
		}
		
	}

	
	private void setDescription(HashMap record,Element descriptions) throws Exception
	{
		HashMap descTable = new HashMap();
		List pList = new ArrayList();
		StringBuffer descbuffer = new StringBuffer();
		
		if(descriptions != null)
		{
			List descriptions_p = descriptions.getChildren("p");
			String id = descriptions.getAttributeValue("id");
			String lang = descriptions.getAttributeValue("lang");
			String format = descriptions.getAttributeValue("format");
			String generated = descriptions.getAttributeValue("generated");
			String country = descriptions.getAttributeValue("country");
			String doc_number = descriptions.getAttributeValue("doc-number");
			String kind = descriptions.getAttributeValue("kind");
			if(id!=null)
			{
				descbuffer.append(id);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(lang!=null)
			{
				descbuffer.append(lang);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(format!=null)
			{
				descbuffer.append(format);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(generated!=null)
			{
				descbuffer.append(generated);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(country!=null)
			{
				descbuffer.append(country);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(doc_number!=null)
			{
				descbuffer.append(doc_number);
			}
			descbuffer.append(Constants.AUDELIMITER);
			if(kind!=null)
			{
				descbuffer.append(kind);
			}
			descbuffer.append(Constants.AUDELIMITER);		
		
			for(int i=0;i<descriptions_p.size();i++)
			{
				Element description_p = (Element)descriptions_p.get(i);
				if(description_p != null && description_p.getText()!=null)
				{					
					descbuffer.append(description_p.getText());					
				}
				descbuffer.append(Constants.IDDELIMITER);		
			}
			
			record.put("DESCRIPTION", descbuffer.toString());
		}
		
	}

	private void setClassification_ipc(Element c_ipc,HashMap record) throws Exception
	{
		if(c_ipc != null)
		{
			Element main_classification = (Element)c_ipc.getChild("main-classification");
			List further_classifications = (List)c_ipc.getChildren("further-classification");
			StringBuffer ipcBuffer = new StringBuffer();
			StringBuffer iccBuffer = new StringBuffer();
			StringBuffer iscBuffer = new StringBuffer();
			StringBuffer ficBuffer = new StringBuffer();


			if(main_classification != null)
			{
				if(main_classification.getChildTextTrim("section")!=null)
				{
					ipcBuffer.append(main_classification.getChildTextTrim("section"));
					iccBuffer.append(main_classification.getChildTextTrim("section"));
				}
				if(main_classification.getChildTextTrim("class")!=null)
				{
					ipcBuffer.append(main_classification.getChildTextTrim("class"));
					iccBuffer.append(main_classification.getChildTextTrim("class"));
				}
				if(main_classification.getChildTextTrim("subclass")!=null)
				{
					ipcBuffer.append(main_classification.getChildTextTrim("subclass"));
					iccBuffer.append(main_classification.getChildTextTrim("subclass"));
				}
				if(main_classification.getChildTextTrim("main-group")!=null)
				{
					ipcBuffer.append(main_classification.getChildTextTrim("main-group"));
					iscBuffer.append(main_classification.getChildTextTrim("main-group"));
				}
				if(main_classification.getChildTextTrim("subgroup")!=null)
				{
					ipcBuffer.append("/"+padSubGroup(main_classification.getChildTextTrim("subgroup")));
					iscBuffer.append("/"+padSubGroup(main_classification.getChildTextTrim("subgroup")));
				}

			}
			if(further_classifications != null)
			{
				StringBuffer tempFicBuffer = new StringBuffer();
				StringBuffer tempIccBuffer = new StringBuffer();
				StringBuffer tempIscBuffer = new StringBuffer();
				for(int i=0;i<further_classifications.size();i++)
				{
					if(ficBuffer.length()>0)
					{
						ficBuffer.append(Constants.AUDELIMITER);
					}

					Element further_classification = (Element)further_classifications.get(i);
					if(further_classification.getChildTextTrim("section")!=null)
					{
						tempFicBuffer.append(further_classification.getChildTextTrim("section"));
						tempIccBuffer.append(further_classification.getChildTextTrim("section"));
					}
					if(further_classification.getChildTextTrim("class")!=null)
					{
						tempFicBuffer.append(further_classification.getChildTextTrim("class"));
						tempIccBuffer.append(further_classification.getChildTextTrim("class"));
					}
					if(further_classification.getChildTextTrim("subclass")!=null)
					{
						tempFicBuffer.append(further_classification.getChildTextTrim("subclass"));
						tempIccBuffer.append(further_classification.getChildTextTrim("subclass"));
					}
					if(further_classification.getChildTextTrim("main-group")!=null)
					{
						tempFicBuffer.append(further_classification.getChildTextTrim("main-group"));
						tempIscBuffer.append(further_classification.getChildTextTrim("main-group"));
					}
					if(further_classification.getChildTextTrim("subgroup")!=null)
					{
						tempFicBuffer.append("/"+padSubGroup(further_classification.getChildTextTrim("subgroup")));
						tempIscBuffer.append("/"+padSubGroup(further_classification.getChildTextTrim("subgroup")));
					}
					if(ficBuffer.indexOf(tempFicBuffer.toString())<0)
					{
						ficBuffer.append(tempFicBuffer.toString());
						tempFicBuffer.setLength(0);
					}
					//System.out.println("icc "+i+" "+tempIccBuffer.toString());
					if(iccBuffer.indexOf(tempIccBuffer.toString())<0)
					{
						//System.out.println("icc2 "+tempIccBuffer.toString());
						if(iccBuffer.length()>0)
						{
							iccBuffer.append(Constants.AUDELIMITER);
						}
						iccBuffer.append(tempIccBuffer.toString());

					}
					if(iscBuffer.indexOf(tempIscBuffer.toString())<0)
					{
						if(iscBuffer.length()>0)
						{
							iscBuffer.append(Constants.AUDELIMITER);
						}
						iscBuffer.append(tempIscBuffer.toString());

					}
					tempIccBuffer.setLength(0);
					tempIscBuffer.setLength(0);
				}
			}
			record.put("IPC",ipcBuffer.toString());
			record.put("ICC",iccBuffer.toString());
			record.put("ISC",iscBuffer.toString());
			record.put("FIC",ficBuffer.toString());
		}
	}

	private String padSubGroup(String s)
	{
		if(s.length() == 1)
		{
			s = "0"+s;
		}

		return s;
	}


	private String getDocType(String authCode, String kindCode)throws Exception
	{
		String docType = null;
		if (authCode.equalsIgnoreCase("US"))
		{
			if (kindCode.equalsIgnoreCase("A1")
				|| kindCode.equalsIgnoreCase("A2")
				|| kindCode.equalsIgnoreCase("A9")
				|| kindCode.equalsIgnoreCase("P1"))
			{
				docType="UA";
			}
			else if(kindCode.equalsIgnoreCase("A")
					|| kindCode.equalsIgnoreCase("B1")
					|| kindCode.equalsIgnoreCase("B2"))
			{
				docType="UG";
			}
			else if
			(	kindCode.equalsIgnoreCase("S")
				|| kindCode.equalsIgnoreCase("S1")
				|| kindCode.equalsIgnoreCase("H")
				|| kindCode.equalsIgnoreCase("H1")
				|| kindCode.equalsIgnoreCase("P")
				|| kindCode.equalsIgnoreCase("P2")
				|| kindCode.equalsIgnoreCase("P3")
				|| kindCode.equalsIgnoreCase("E")
				|| kindCode.equalsIgnoreCase("E1"))
			{
				docType="UG";
			}
			else
			{
				System.out.println("found new kindCode "+kindCode+" for record "+patentCountry+patentNumber);
			}

		}
		else if (authCode.equalsIgnoreCase("EP"))
		{
			//Applications
			if (kindCode.equalsIgnoreCase("A1")
				|| kindCode.equalsIgnoreCase("A2")
				|| kindCode.equalsIgnoreCase("A3")
				|| kindCode.equalsIgnoreCase("A4")
				|| kindCode.equalsIgnoreCase("A8")
				|| kindCode.equalsIgnoreCase("A9"))
			{
				docType="EA";
			}
			//Grants
			else if(kindCode.equalsIgnoreCase("B1")
					|| kindCode.equalsIgnoreCase("B2")
					|| kindCode.equalsIgnoreCase("B8")
					|| kindCode.equalsIgnoreCase("B9"))
			{
				docType="EG";
			}
		}
		else
		{
			docType = authCode;
			System.out.println("found new authCode "+authCode+ " for record "+patentNumber);
		}
		return docType;
	}

	private void setClassification_national(Element classification_national,HashMap record) throws Exception
	{
		StringBuffer uclBuffer = new StringBuffer();
		StringBuffer uccBuffer = new StringBuffer();
		StringBuffer uscBuffer = new StringBuffer();
		HashMap uclMap = new HashMap();
		HashMap uccMap = new HashMap();
		HashMap uscMap = new HashMap();
		String ucl = null;
		String ucc = null;
		String usc = null;
		//System.out.println("cn_country= "+classification_national.getChildTextTrim("country"));
		record.put("CN_COUNTRY",classification_national.getChildTextTrim("country"));

		Element main_classification = classification_national.getChild("main-classification");
		if(main_classification != null)
		{
			//System.out.println("mc_text= "+main_classification.getChildTextTrim("text"));
			ucl = main_classification.getChildTextTrim("text");
			if(ucl != null)
			{
				if(!uclMap.containsKey(ucl))
				{
					uclMap.put(ucl,ucl);
					if(uclBuffer.length()>0)
					{
						uclBuffer.append(Constants.AUDELIMITER);
					}
					uclBuffer.append(ucl);
				}
			}

			ucc = main_classification.getChildTextTrim("class");
			if(ucc != null)
			{
				if(!uccMap.containsKey(ucc))
				{
					uccMap.put(ucc,ucc);
					if(uccBuffer.length()>0)
					{
						uccBuffer.append(Constants.AUDELIMITER);
					}
					uccBuffer.append(ucc);
				}
			}

			usc = main_classification.getChildTextTrim("subclass");
			if(usc != null)
			{
				if(!uscMap.containsKey(usc))
				{
					uscMap.put(usc,usc);
					if(uscBuffer.length()>0)
					{
						uscBuffer.append(Constants.AUDELIMITER);
					}
					uscBuffer.append(usc);
				}
			}

		}

		List further_classification_list = classification_national.getChildren("further-classification");
		if(further_classification_list != null)
		{
			for(int i=0; i<further_classification_list.size();i++)
			{
					Element further_classification = (Element)further_classification_list.get(i);
					if(further_classification != null)
					{
						//System.out.println("mc_text= "+main_classification.getChildTextTrim("text"));
						ucl = further_classification.getChildTextTrim("text");
						if(ucl != null)
						{
							if(!uclMap.containsKey(ucl))
							{
								uclMap.put(ucl,ucl);
								if(uclBuffer.length()>0)
								{
									uclBuffer.append(Constants.AUDELIMITER);
								}
								uclBuffer.append(ucl);
							}
						}


						ucc = further_classification.getChildTextTrim("class");
						if(ucc != null)
						{
							if(!uccMap.containsKey(ucc))
							{
								uccMap.put(ucc,ucc);
								if(uccBuffer.length()>0)
								{
									uccBuffer.append(Constants.AUDELIMITER);
								}
								uccBuffer.append(ucc);
							}
						}


						usc = further_classification.getChildTextTrim("subclass");
						if(usc != null)
						{
							if(!uscMap.containsKey(usc))
							{
								uscMap.put(usc,usc);
								if(uscBuffer.length()>0)
								{
									uscBuffer.append(Constants.AUDELIMITER);
								}
								uscBuffer.append(usc);
							}
						}


					}//if
				}//for
			}//if
		record.put("MC_TEXT",uclBuffer.toString());
		record.put("MC_CLASS",uccBuffer.toString());
		record.put("MC_SUBCLASS",uscBuffer.toString());
	}

	private void setRelation(List inputList,HashMap docNumberMap) throws Exception
	{
		if(inputList != null)
		{
			for(int i=0;i<inputList.size();i++)
			{
				Element inputElement = (Element)inputList.get(i);
				Element relation = inputElement.getChild("relation");
				if(relation == null)
				{
					//System.out.println("relation is null");
					relation = inputElement.getChild("us-relation");
				}
				if(relation != null)
				{
					Element parent_doc = relation.getChild("parent-doc");
					Element child_doc = relation.getChild("child-doc");
					if(parent_doc != null)
					{
						Element parentDocId = parent_doc.getChild("document-id");
						if(parentDocId != null)
						{
							setDocID(parentDocId,docNumberMap);
						}
						else
						{
							System.out.println("parent_doc document-id is null");
						}
					}
					else
					{
						System.out.println("parent_doc is null");
					}

					if(child_doc != null)
					{
						Element childDocId = child_doc.getChild("document-id");
						if(childDocId != null)
						{
							//setDocID(childDocId,docNumberMap);
						}
					}

				}
			}
		}
	}

	private void setDocID(Element docId,HashMap docNumberMap) throws Exception
	{
		String country 		= docId.getChildTextTrim("country");
		String docNumber 	= docId.getChildTextTrim("doc-number");
		String kind 		= docId.getChildTextTrim("kind");
		String date 		= docId.getChildTextTrim("date");
		String name 		= docId.getChildTextTrim("name");
		if(country != null)
		{
			if(docNumberMap.get("COUNTRY") != null)
			{
				docNumberMap.put("COUNTRY",(String)docNumberMap.get("COUNTRY")+Constants.AUDELIMITER+country);
			}
			else
			{
				docNumberMap.put("COUNTRY",country);
			}
		}

		if(docNumber != null)
		{
			docNumber = docNumber.replaceAll("/","");
			if(docNumberMap.get("DOCNUMBER")!=null)
			{
				docNumberMap.put("DOCNUMBER",(String)docNumberMap.get("DOCNUMBER")+Constants.AUDELIMITER+docNumber);
			}
			else
			{
				docNumberMap.put("DOCNUMBER",docNumber);
			}
		}


		if(kind != null)
		{
			if(docNumberMap.get("KIND")!=null)
			{
				docNumberMap.put("KIND",(String)docNumberMap.get("KIND")+Constants.AUDELIMITER+kind);
			}
			else
			{
				docNumberMap.put("KIND",kind);
			}
		}

		if(date != null)
		{
			if(docNumberMap.get("DATE")!=null)
			{
				docNumberMap.put("DATE",(String)docNumberMap.get("DATE")+Constants.AUDELIMITER+date);
			}
			else
			{
				docNumberMap.put("DATE",date);
			}
		}

		if(name != null)
		{
			if(docNumberMap.get("NAME") != null)
			{
				docNumberMap.put("NAME",(String)docNumberMap.get("NAME")+Constants.AUDELIMITER+name);
			}
			else
			{
				docNumberMap.put("NAME",name);
			}
		}
	}

	private void setFos_classification_national(HashMap record,List cList) throws Exception
	{
		List cnList = new ArrayList();
		for(int i=0;i<cList.size();i++)
		{
			HashMap cMap = new HashMap();
			Element classification_national = (Element)cList.get(i);
			if(classification_national != null)
			{
				cMap.put("CN_COUNTRY",classification_national.getChildText("country"));
				Element main_classification = classification_national.getChild("main-classification");
				if(main_classification!=null)
				{
					cMap.put("CN_TEXT",main_classification.getChildText("text"));
					cMap.put("CN_CLASS",main_classification.getChildText("class"));
					cMap.put("CN_SUBCLASS",main_classification.getChildText("subclass"));
				}
				cnList.add(cMap);
			}
		}
		record.put("FIELD_OF_SEARCH",cnList);
	}

	private void setTitle(HashMap record,List tList) throws Exception
	{
		List titleList = new ArrayList();
		for(int i=0;i<tList.size();i++)
		{
			HashMap titleMap = new HashMap();
			Element invention_title = (Element)tList.get(i);
			//System.out.println("it_id= "+invention_title.getAttributeValue("id"));
			titleMap.put("IT_ID",invention_title.getAttributeValue("id"));

			//System.out.println("it_lang= "+invention_title.getAttributeValue("lang"));
			titleMap.put("IT_LANG",invention_title.getAttributeValue("lang"));

			//System.out.println("it_format= "+invention_title.getAttributeValue("format"));
			titleMap.put("IT_FORMAT",invention_title.getAttributeValue("format"));

			//System.out.println("it_content= "+invention_title.getTextTrim());
			StringBuffer inventionTitle =  getMixData(invention_title.getContent(),new StringBuffer());
			titleMap.put("IT_CONTENT", inventionTitle.toString());
			titleList.add(titleMap);
		}
		record.put("TITLE",titleList);
	}


	private void setCitation(HashMap record,List cList) throws Exception
	{
		List citationList = new ArrayList();
		for(int i=0;i<cList.size();i++)
		{
			HashMap citationTable = new HashMap();
			Element citation = (Element)cList.get(i);
			if(citation != null)
			{
				String srep_phase = citation.getAttributeValue("srep-phase");
				citationTable.put("srep_phase",srep_phase);
				Element patcit = (Element)citation.getChild("patcit");
				Element nplcit = (Element)citation.getChild("nplcit");
				String num = citation.getAttributeValue("num");
				//System.out.println("num= "+num);
				citationTable.put("number",num);
				if(patcit != null)
				{
					Element document_id = (Element)patcit.getChild("document-id");
					if(document_id != null)
					{
						String di_country = document_id.getChildTextTrim("country");
						citationTable.put("DI_COUNTRY",di_country);
						//System.out.println("di_country= "+di_country);
						String doc_number = document_id.getChildTextTrim("doc-number");
						//System.out.println("citation= "+doc_number);
						citationTable.put("DOC_NUMBER",doc_number);
						String kind = document_id.getChildTextTrim("kind");
						//System.out.println("kind= "+kind);
						citationTable.put("KIND",kind);

						if(document_id.getChild("name") != null)
						{
							StringBuffer name = getMixData(document_id.getChild("name").getContent(),new StringBuffer());
							//System.out.println("name= "+name);
							citationTable.put("NAME", name.toString()); //
						}
					}
				}

				if(nplcit != null)
				{
					if(nplcit.getChild("text") != null)
					{
						StringBuffer npcBuf = getMixData(nplcit.getChild("text").getContent(),new StringBuffer());
						String non_patent_citation = npcBuf.toString();
						if(non_patent_citation != null)
						{
							//System.out.println("non_patent_citation= "+non_patent_citation);
							citationTable.put("NON_PATENT_CITATION",non_patent_citation);
						}
					}
				}

				Element classification_national = (Element)citation.getChild("classification-national");
				if(classification_national != null)
				{
					String cn_country = classification_national.getChildText("country");
					citationTable.put("cn_country",cn_country);
					Element  main_classification = (Element)classification_national.getChild("main-classification");
					if(main_classification != null)
					{
						String text = main_classification.getChildText("text");
						citationTable.put("text",text);
						String cn_class = main_classification.getChildText("class");
						citationTable.put("cn_class",cn_class);
						String subclass = main_classification.getChildText("subclass");
						citationTable.put("subclass",subclass);
					}
				}
			}

			citationList.add(citationTable);

		}
		record.put("CITATION",citationList);
	}

	private void setParties(HashMap record,List parties, String name) throws Exception
	{
		List partiesList = new ArrayList();
		for(int i=0;i<parties.size();i++)
		{
			HashMap partiesTable = new HashMap();
			String sequence = ((Element)parties.get(i)).getAttributeValue("sequence");
			//System.out.println("party_sequence= "+sequence);
			String app_type = ((Element)parties.get(i)).getAttributeValue("app-type");
			//System.out.println("party_app_type= "+app_type);
			String designation = ((Element)parties.get(i)).getAttributeValue("designation");
			String rep_type = ((Element)parties.get(i)).getAttributeValue("rep-type");
			//System.out.println("party_designation= "+designation);
			partiesTable.put("SEQUENCE",sequence);
			partiesTable.put("APP_TYPE",app_type);
			partiesTable.put("REP_TYPE",rep_type);
			partiesTable.put("DESIGNATION",designation);
			Element addressbook = ((Element)parties.get(i)).getChild("addressbook");
			String orgname = addressbook.getChildTextTrim("orgname");
			//System.out.println("party_orgname= "+orgname);
			String last_name = addressbook.getChildTextTrim("last-name");
			//System.out.println("party_last_name= "+last_name);
			String first_name = addressbook.getChildTextTrim("first-name");
			StringBuffer full_name = null;
			if(addressbook.getChild("name") != null)
			{
				full_name = getMixData(addressbook.getChild("name").getContent(),new StringBuffer());
			}

			//System.out.println("party_first_name= "+first_name);
			partiesTable.put("ORGNAME",orgname);
			partiesTable.put("LAST_NAME",last_name);
			partiesTable.put("FIRST_NAME",first_name);
			if(full_name != null)
			{
				partiesTable.put("NAME",full_name.toString());
			}

			Element address = addressbook.getChild("address");
			if(address != null)
			{
				String city = address.getChildTextTrim("city");
				//System.out.println("party_city= "+city);
				String state = address.getChildTextTrim("state");
				//System.out.println("party_state= "+state);
				String country = address.getChildTextTrim("country");
				//System.out.println("party_country= "+country);
				String postcode = address.getChildTextTrim("postcode");
				//System.out.println("party_postcode= "+postcode);
				String address_1 = address.getChildTextTrim("address-1");
				//System.out.println("party_address_1= "+address_1);
				partiesTable.put("CITY",city);
				partiesTable.put("STATE",state);
				partiesTable.put("POSTCODE",postcode);
				partiesTable.put("COUNTRY",country);
				partiesTable.put("ADDRESS1",address_1);
			}
			partiesList.add(partiesTable);
		}
		record.put(name,partiesList);
	}

	private void setClassification_ecla(HashMap record,List classifications_ecla) throws Exception
	{
		List ceList = new ArrayList();
		for(int i=0;i<classifications_ecla.size();i++)
		{
			HashMap ceTable = new HashMap();
			Element classification_ecla = (Element)classifications_ecla.get(i);
			if(classification_ecla != null)
			{
				String sequence = classification_ecla.getAttributeValue("sequence");
				//System.out.println("sequence= "+sequence);
				ceTable.put("SEQUENCE",sequence);
				String text = classification_ecla.getChildTextTrim("text");
				ceTable.put("TEXT",text);
				//System.out.println("text= "+text);
				String section = classification_ecla.getChildTextTrim("section");
				if(section==null)
				{
					section="";
				}
				//System.out.println("section= "+section);
				String classText = classification_ecla.getChildTextTrim("class");
				if(classText==null)
				{
					classText="";
				}
				//System.out.println("classText= "+classText);
				String subClass = classification_ecla.getChildTextTrim("subclass");
				if(subClass==null)
				{
					subClass="";
				}
				//System.out.println("subClass= "+subClass);
				ceTable.put("MAINCLASS",section+classText+subClass);
				String main_group = classification_ecla.getChildTextTrim("main-group");
				if(main_group==null)
				{
					main_group="";
				}
				//System.out.println("main_group= "+main_group);

				String subgroup = classification_ecla.getChildTextTrim("subgroup");
				if(subgroup==null)
				{
					subgroup="";
				}
				//System.out.println("subgroup= "+subgroup);

				Element additional_subgroups = classification_ecla.getChild("additional-subgroups");
				if(additional_subgroups != null)
				{
					List additional_subgroup = additional_subgroups.getChildren("additional-subgroup");
					StringBuffer asBuffer = new StringBuffer();
					if(additional_subgroup != null)
					{

						for(int j=0;j<additional_subgroup.size();j++)
						{
							Element additional_subgroup_value = (Element)additional_subgroup.get(j);
							if(additional_subgroup != null)
							{
								String as_sequence = additional_subgroup_value.getAttributeValue("sequence");
								String as_content = additional_subgroup_value.getTextTrim();
								//System.out.println("as_sequence= "+as_sequence);
								//System.out.println("as_content= "+as_content);
								if(as_content!=null)
								{
									asBuffer.append(as_content);
								}
							}
						}

						//System.out.println("asBuffer= "+asBuffer);

					}
					ceTable.put("SUBCLASS",main_group+"/"+subgroup+asBuffer.toString());
				}
				else
				{
					ceTable.put("SUBCLASS",main_group+"/"+subgroup);
				}
			}
			ceList.add(ceTable);
		}
		record.put("CLASSIFICATION_ECLA",ceList);
	}

	private void setClassifications_ipcr(HashMap record,List classifications_ipcr,String type) throws Exception
	{
		List ciList = new ArrayList();
		for(int i=0;i<classifications_ipcr.size();i++)
		{
			HashMap ciTable = new HashMap();
			Element classification_ipcr = (Element)classifications_ipcr.get(i);
			if(classification_ipcr != null)
			{
				String sequence = classification_ipcr.getAttributeValue("sequence");
				//System.out.println("sequence "+sequence);
				ciTable.put("SEQUENCE",sequence);
				String text = classification_ipcr.getChildText("text");
				//System.out.println("text "+text);
				ciTable.put("TEXT",text);
				String dateType = "ipc-version-indicator";
				if(type.equals("cpc"))
				{
					dateType = "cpc-version-indicator";
				}
				String date = classification_ipcr.getChild(dateType).getChildTextTrim("date");
				//System.out.println("date "+date);
				ciTable.put("DATE",date);
				String classification_level = classification_ipcr.getChildTextTrim("classification-level");
				//System.out.println("classification_level "+classification_level);
				ciTable.put("CLASSIFICATION_LEVEL",classification_level);
				String section = classification_ipcr.getChildTextTrim("section");
				ciTable.put("SECTION",section);
				String classText = classification_ipcr.getChildTextTrim("class");
				ciTable.put("CLASS",classText);
				String subClass = classification_ipcr.getChildTextTrim("subclass");
				ciTable.put("SUBCLASS",subClass);
				String main_group = classification_ipcr.getChildTextTrim("main-group");
				ciTable.put("MAIN_GROUP",main_group);
				String subgroup = classification_ipcr.getChildTextTrim("subgroup");
				ciTable.put("SUBGROUP",subgroup);
				String symbol_position = classification_ipcr.getChildTextTrim("symbol-position");
				ciTable.put("SYMBOL_POSITION",symbol_position);
				String classification_value = classification_ipcr.getChildTextTrim("classification-value");
				ciTable.put("CLASSIFICATION_VALUE",classification_value);
				if(classification_ipcr.getChild("action-date")!=null)
				{
					String action_date = classification_ipcr.getChild("action-date").getChildTextTrim("date");;
					ciTable.put("ACTION_DATE",action_date);
				}

				if(classification_ipcr.getChild("generating-office")!=null)
				{
					String country = classification_ipcr.getChild("generating-office").getChildTextTrim("country");;
					ciTable.put("COUNTRY",country);
				}

				String classification_status = classification_ipcr.getChildTextTrim("classification-status");
				ciTable.put("CLASSIFICATION_STATUS",classification_status);

				String classification_data_source = classification_ipcr.getChildTextTrim("classification-data-source");
				ciTable.put("CLASSIFICATION_DATA_SOURCE",classification_data_source);
			}
			ciList.add(ciTable);
		}
		
		if(type.equals("cpc"))
		{
			record.put("CLASSIFICATION_CPC",ciList);
		}
		else
		{
			record.put("CLASSIFICATION_IPCR",ciList);
		}

	}

	private void setPriority_Claims(HashMap record,List priority_claims) throws Exception
	{
		List pcList = new ArrayList();
		for(int i=0;i<priority_claims.size();i++)
		{
			HashMap pcTable = new HashMap();
			Element priority_claim = (Element)priority_claims.get(i);
			if(priority_claim != null)
			{
				String sequence = priority_claim.getAttributeValue("sequence");
				//System.out.println("sequence "+sequence);
				pcTable.put("SEQUENCE",sequence);
				String kind = priority_claim.getAttributeValue("kind");
				//System.out.println("kind "+kind);
				pcTable.put("KIND",kind);
				String country = priority_claim.getChildTextTrim("country");
				//System.out.println("country "+country);
				pcTable.put("COUNTRY",country);
				String doc_number = priority_claim.getChildTextTrim("doc-number");
				//System.out.println("doc_number "+doc_number);
				pcTable.put("DOC_NUMBER",doc_number);
				String date = priority_claim.getChildTextTrim("date");
				//System.out.println("date "+date);
				pcTable.put("DATE",date);
				pcList.add(pcTable);
			}

		}
		record.put("PRIORITY_CLAIMS",pcList);
	}

	public String[] getCitMID(String pNum,
						    String authCode)
		throws Exception
	{
		return dbMap.getMID_KC(authCode+pNum, "A");
	}

	private String pnNormalization(String pn,
								   String kind)
		throws Exception
	{
		if(kind != null &&
		   kind.equalsIgnoreCase("P1") &&
		   pn.indexOf("PP") == 0)
		{
			pn = pn.substring(2);
		}

		if(pn != null)
		{
			pn=pn.replaceFirst("^(D|PP|P|RE|T|H|X|RX|AI)[0]+","$1");
			pn=pn.replaceFirst("^[0]+","");
		}

		return pn;
	}

	private boolean checkAvailable(String pNum,
								   String authCode,
								   String kind)
		throws Exception
	{
		if(pNum.indexOf("PP") == 0)
		{
			return checkAvailablePP(pNum, authCode);
		}
		String authPnum = authCode+pNum;
		String authPnumKind = authCode+pNum+kind.charAt(0);
		if(dupMap.containsKey(authPnumKind))
		{
			return true;
		}
		else
		{
			dupMap.put(authPnumKind, authPnumKind);
		}

		if(authCode.equalsIgnoreCase("US"))
		{
			return dbMap.contains(authPnum);
		}
		else
		{
			return dbMap.contains(authPnum,kind);
		}
	}

	private boolean checkAvailablePP(String pNum,
									 String authCode)
		throws Exception
	{
		String authPnum = authCode+pNum;

		if(dupMap.containsKey(authPnum))
		{
			return true;
		}
		else
		{
			dupMap.put(authPnum, authPnum);
		}

		if(dbMap.contains(authPnum))
		{
			return true;
		}
		else
		{
			authPnum = "PP0"+authPnum.substring(2);
			return dbMap.contains(authPnum);
		}
	}

	private  StringBuffer getMixData(List l, StringBuffer b)
	{
		Iterator it = l.iterator();
		while(it.hasNext())
		{
			Object o = it.next();

			if(o instanceof Text )
			{
				String text=((Text)o).getText();
				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				b.append(text);
			}
			else if(o instanceof EntityRef)
			{
				b.append("&").append(((EntityRef)o).getName()).append(";");
			}
			else if(o instanceof Element)
			{
				Element e = (Element)o;
				b.append("<").append(e.getName());
				List ats = e.getAttributes();
				if(!ats.isEmpty() && !e.getName().equals("p"))
				{	Iterator at = ats.iterator();
					while(at.hasNext())
					{
						Attribute a = (Attribute)at.next();
						b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
				b.append(">");
				getMixData(e.getContent(), b);
				b.append("</").append(e.getName()).append(">");
			}
		}

		return b;
	}

	private static String substituteChars(String xml)
	{
		int len = xml.length();
		StringBuffer sb = new StringBuffer();
		char c;

		for (int i = 0; i < len; i++)
		{
			c = xml.charAt(i);
			if((int) c >= 32 && (int) c <= 127)
			{
				sb.append(c);
			}
			else if(((int) c >= 128 || (int) c < 32))
			{
				//System.out.println("special char "+(int)c);
				switch ((int) c)
				{
					case 30 :sb.append(c);break;
					case 31 :sb.append(c);break;
					case 128 :sb.append("&Ccedil;");break;
					case 129 :sb.append("&uuml;");break;
					case 130 :sb.append("&eacute;");break;
					case 131 :sb.append("&acirc;");break;
					case 132 :sb.append("&auml;");break;
					case 133 :sb.append("&agrave;");break;
					case 134 :sb.append("&aring;");break;
					case 135 :sb.append("&ccedil;");break;
					case 136 :sb.append("&ecirc;");break;
					case 137 :sb.append("&euml;");break;
					case 138 :sb.append("&egrave;");break;
					case 139 :sb.append("&iuml;");break;
					case 140 :sb.append("&icirc;");break;
					case 141 :sb.append("&igrave;");break;
					case 142 :sb.append("&Auml;");break;
					case 143 :sb.append("&Aring;");break;
					case 144 :sb.append("&Eacute;");break;
					case 145 :sb.append("&aelig;");break;
					case 146 :sb.append("&AElig;");break;
					case 147 :sb.append("&ocirc;");break;
					case 148 :sb.append("&ouml;");break;
					case 149 :sb.append("&ograve;");break;
					case 150 :sb.append("&ucirc;");break;
					case 151 :sb.append("&ugrave;");break;
					case 152 :sb.append("&yuml;");break;
					case 153 :sb.append("&Ouml;");break;
					case 154 :sb.append("&Uuml;");break;
					case 156 :sb.append("&pound;");break;
					case 157 :sb.append("&yen;");break;
					case 160 :sb.append("&nbsp;");break;	//space
					case 161 :sb.append("&iexcl;");break; 	//Inverted exclamation mark
					case 162 :sb.append("&cent;");break; 	//Cent sign
					case 163 :sb.append("&pound;");break; 	//Pound sign
					case 164 :sb.append("&curren;");break; 	//Currency sign
					case 165 :sb.append("&yen;");break; 	//Yen sign
					case 166 :sb.append("&brvbar;");break; 	//broken bar,
					case 167 :sb.append("&sect;");break; 	//scetion sign
					case 168 :sb.append("&uml;");break; 	//diaeresis
					case 169 :sb.append("&copy;");break; 	//copyright
					case 170 :sb.append("&ordf;");break; 	//feminine ordinal indicator
					case 171 :sb.append("&laquo;");break; 	//left-pointing double angle
					case 172 :sb.append("&not;");break; 	//not sign
					case 173 :sb.append("&shy;");break; 	//soft hyphen
					case 174 :sb.append("&reg;");break; 		//registered sign
					case 175 :sb.append("&macr;");break; 	//macron
					case 176 :sb.append("&deg;");break; 	//degree sign
					case 177 :sb.append("&plusmn;");break; 	//plus-minus sign
					case 178 :sb.append("&sup2;");break; 	//superscript two
					case 179 :sb.append("&sup3;");break; 	//superscript 3
					case 180 :sb.append("&acute;");break; 	//acute accent
					case 181 :sb.append("&micro;");break; 	//micro sign
					case 182 :sb.append("&para;");break; 	//pilcrow sign
					case 183 :sb.append("&middot;");break; 	//middle dot
					case 184 :sb.append("&cedi1;");break; 	//cedilla
					case 185 :sb.append("&supl;");break; 	//superscript one
					case 186 :sb.append("&ordm;");break; 	//masculine ordinal indicator
					case 187 :sb.append("&raquo;");break; 	//right-pointing double angle
					case 188 :sb.append("&frac14;");break; 	//vulgar fraction one quarter
					case 189 :sb.append("&frac12;");break; 	//vulgar fraction one half
					case 190 :sb.append("&frac12;");break; 	//vulgar fraction three quarters
					case 191 :sb.append("&iquest;");break; 	//Invert question mark
					case 192 :sb.append("&Agrave;");break; 	//Capital A grave
					case 193 :sb.append("&Aacute;");break; 	//Capital A acute
					case 194 :sb.append("&Acire;");break; 	//Capital A circumflex
					case 195 :sb.append("&Atilde;");break; 	//Capital A tilde
					case 196 :sb.append("&Auml;");break; 	//Capital A diaeresis
					case 197 :sb.append("&Aring;");break; 	//Capital A ring above
					case 198 :sb.append("&AElig;");break; 	//Capital AE
					case 199 :sb.append("&Ccedil;");break; 	//Capital C cedilla
					case 200 :sb.append("&Egrave;");break; 	//Capital E grave;
					case 201 :sb.append("&Eacute;");break; 	//Capital E acute
					case 202 :sb.append("&Ecire;");break; 	//Capital E circumflex
					case 203 :sb.append("&Euml;");break; 	//Capital E diaeresis
					case 204 :sb.append("&Igrave;");break; 	//Capital I acute
					case 205 :sb.append("&Iacute;");break; 	//Capital E diaeresis
					case 206 :sb.append("&Icire;");break; 	//Capital I circumflex
					case 207 :sb.append("&Iuml;");break; 	//Capital I diaeresis
					case 208 :sb.append("&ETH;");break; 	//Capital Eth, Edh, crossed D
					case 209 :sb.append("&Ntilde;");break; 	//Capital Ntilde
					case 210 :sb.append("&Ograve;");break; 	//Capital O grave
					case 211 :sb.append("&Oacute;");break; 	//Capital O acute
					case 212 :sb.append("&Ocire;");break; 	//Capital O circumflex
					case 213 :sb.append("&Otilde;");break; 	//Capital O tilde
					case 214 :sb.append("&Ouml;");break; 	//Capital O diaeresis
					case 215 :sb.append("&times;");break; 	//Multiplication sign
					case 225 :sb.append("&aacute;");break; 	//a acute
					case 226 :sb.append("&acire;");break; 	//a circumflex
					case 227 :sb.append("&atilde;");break; 	//a tilde
					case 228 :sb.append("&auml;");break; 	//a diaeresis
					case 229 :sb.append("&aring;");break; 	//a a ring above
					case 230 :sb.append("&aelig;");break; 	//a ae
					case 231 :sb.append("&ccedil;");break; 	//a c cedilla
					case 232 :sb.append("&egrave;");break; 	//a e grave
					case 233 :sb.append("&eacute;");break; 	//a e acute
					case 234 :sb.append("&ecire;");break; 	//a circumflex
					case 235 :sb.append("&euml;");break; 	//a e diaeresis
					case 236 :sb.append("&igrave;");break; 	//a i grave
					case 237 :sb.append("&iacute;");break; 	//a i acute
					case 238 :sb.append("&icire;");break; 	//a circumflex
					case 239 :sb.append("&iuml;");break; 	//a diaeresis
					case 240 :sb.append("&eth;");break; 	//a eth
					case 241 :sb.append("&ntilde;");break; 	//a n tilde
					case 242 :sb.append("&ograve;");break; 	//a o grave
					case 243 :sb.append("&oacute;");break; 	//a o acute
					case 244 :sb.append("&ocire;");break; 	//o circumflex
					case 245 :sb.append("&otilde;");break; 	//a tilde
					case 246 :sb.append("&ouml;");break; 	//o diaeresis
					case 247 :sb.append("&divide;");break; 	//division sign
					case 248 :sb.append("&oslash;");break; 	//o stroke
					case 249 :sb.append("&ugrave;");break; 	//u grave
					case 250 :sb.append("&uacute;");break; 	//u acute
					case 251 :sb.append("&ucire;");break; 	//o circumflex
					case 252 :sb.append("&uuml;");break; 	//u diaeresis
					case 253 :sb.append("&yacute;");break; 	//y acute
					case 254 :sb.append("&thorn;");break; 	//thorn
					case 255 :sb.append("&uyuml;");break; 	//y diaeresis
					case 338 :sb.append("&OElig;");break; 	//Capital ligaturen OE
					case 339 :sb.append("&oelig;");break; 	//Ligature oe
					case 352 :sb.append("&Scaron;");break; 	//Capital S caron
					case 353 :sb.append("&scaron;");break; 	//s caron
					case 376 :sb.append("&Yuml;");break; 	//Capital Y diaeresis
					case 402 :sb.append("&fnof;");break; 	//F hook
					case 710 :sb.append("&circ;");break; 	//modifier letter circumflex accent
					case 732 :sb.append("&tilde;");break; 	//small tilde
					case 913 :sb.append("&Alpha;");break; 	//Alpha
					case 914 :sb.append("&Beta;");break; 	//Beta
					case 915 :sb.append("&Gamma;");break; 	//Capital gamma
					case 916 :sb.append("&Delta;");break; 	//Capital delta
					case 917 :sb.append("&Epsilon;");break; //Capital epsilon
					case 918 :sb.append("&Zeta;");break; 	//Capital zeta
					case 919 :sb.append("&Eta;");break; 	//Capital eta
					case 920 :sb.append("&Theta;");break; 	//Capital theta
					case 921 :sb.append("&Iota;");break; 	//Capital iota
					case 922 :sb.append("&Kappa;");break; 	//Capital Kappa
					case 923 :sb.append("&Lambda;");break; 	//Capital lambda
					case 924 :sb.append("&Mu;");break; 		//Capital mu
					case 925 :sb.append("&Nu;");break; 		//Capital nu
					case 926 :sb.append("&Xi;");break; 		//Capital xi
					case 927 :sb.append("&Omicron;");break; //Capital omicron
					case 928 :sb.append("&Pi;");break; 		//Capital pi
					case 929 :sb.append("&Rho;");break; 	//Capital rho
					case 931 :sb.append("&Sigma;");break; 	//Capital sigma
					case 932 :sb.append("&Tau;");break; 	//Capital tau
					case 933 :sb.append("&Upsilon;");break; //Capital Upsilon
					case 934 :sb.append("&Phi;");break; 	//Capital Phi
					case 935 :sb.append("&Chi;");break; 	//Capital chi
					case 936 :sb.append("&Psi;");break; 	//Capital psi
					case 937 :sb.append("&Omega;");break; 	//Capital omega
					case 945 :sb.append("&alpha;");break; 	//Alpha
					case 946 :sb.append("&beta;");break; 	//Beta
					case 947 :sb.append("&gamma;");break; 	//Gamma
					case 948 :sb.append("&delta;");break; 	//Delta
					case 949 :sb.append("&Epsilon;");break; //Epsilon
					case 950 :sb.append("&zeta;");break; 	//Zeta
					case 951 :sb.append("&eta;");break; 	//Eta
					case 952 :sb.append("&theta;");break; 	//Theta
					case 953 :sb.append("&iota;");break; 	//Iota
					case 954 :sb.append("&kappa;");break; 	//Kappa
					case 955 :sb.append("&lambda;");break; 	//Lambda
					case 956 :sb.append("&mu;");break; 		//Mu
					case 957 :sb.append("&nu;");break; 		//Nu
					case 958 :sb.append("&xi;");break; 		//Xi
					case 959 :sb.append("&omicron;");break; //omicron
					case 960 :sb.append("&pi;");break; 		//pi
					case 961 :sb.append("&rho;");break; 	//rho
					case 962 :sb.append("&sigmaf;");break; 	//final sigma
					case 963 :sb.append("&sigma;");break; 	//sigma
					case 964 :sb.append("&tau;");break; 	//Tau
					case 965 :sb.append("&upsilon;");break; //Upsilon
					case 966 :sb.append("&phi;");break; 	//Phi
					case 967 :sb.append("&chi;");break; 	//chi
					case 968 :sb.append("&psi;");break; 	//psi
					case 969 :sb.append("&omega;");break; 	//Omega
					case 977 :sb.append("&thetasym;");break;//theta symbol
					case 978 :sb.append("&upsih;");break; 	//greek upsilon with hook symbol
					case 982 :sb.append("&piv;");break; 	//greek pi symbol
					case 8194 :sb.append("&ensp;");break; 	//en space
					case 8195 :sb.append("&emsp;");break; 	//em space
					case 8201 :sb.append("&thinsp;");break; //thin space
					case 8204 :sb.append("&zwnj;");break; 	//zero width non-joiner
					case 8205 :sb.append("&zwj;");break; 	//zero width joiner
					case 8206 :sb.append("&lrm;");break; 	//left to right mark
					case 8207 :sb.append("&rlm;");break; 	//right to left mark
					case 8211 :sb.append("&ndash;");break; 	//En dash
					case 8212 :sb.append("&mdash;");break; 	//Em dash
					case 8216 :sb.append("&lsquo;");break; 	//left single quotation
					case 8217 :sb.append("&rsquo");break; 	//right single quotation
					case 8218 :sb.append("&sbquo");break; 	//single low 9 quation mark
					case 8220 :sb.append("&ldquo;");break; 	//left double quotation
					case 8221 :sb.append("&rdquo;");break; 	//right double quotation
					case 8222 :sb.append("&bdquo;");break; 	//Double low-9 quotation mark
					case 8224 :sb.append("&dagger;");break; //Dagger
					case 8225 :sb.append("&Dagger;");break; //Double Dagger
					case 8226 :sb.append("&bull;");break; 	//Bullet, black small circle
					case 8230 :sb.append("&hellip;");break; 	//Horizontal ellipsis
					case 8240 :sb.append("&permil;");break; //per mille sign
					case 8242 :sb.append("&prime;");break; 	//prime, minutes,feet
					case 8243 :sb.append("&Prime;");break; 	//double prime,
					case 8249 :sb.append("&Isaquo;");break; //single left-pointing angle quotation mark
					case 8250 :sb.append("&rsaquo;");break; //single right-pointing angle quotation mark
					case 8254 :sb.append("&oline;");break;  //Overline, spacing overscore
					case 8260 :sb.append("&frasl;");break; 	//Fraction slash
					case 8364 :sb.append("&euro;");break; 	//Euro sign
					case 8465 :sb.append("&image;");break;  //blackletter capital I
					case 8472 :sb.append("&weierp;");break; //script capital P, power set, Weierstrass p
					case 8476 :sb.append("&real;");break; 	//Blackletter capital R, real part symbol,
					case 8482 :sb.append("&trade;");break; 	//trademark sign
					case 8501 :sb.append("&alefsym;");break;//Alef symbol, first transfinite cardinal
					case 8592 :sb.append("&larr;");break; 	//Leftward arrow
					case 8593 :sb.append("&uarr;");break;   //Upward arrow
					case 8594 :sb.append("&rarr;");break; 	//Righteard arrow
					case 8595 :sb.append("&darr;");break; 	//Downward arrow
					case 8596 :sb.append("&harr;");break;   //left right arrow
					case 8629 :sb.append("&crarr;");break;  //downward arrow with corner leftward, carriage return
					case 8656 :sb.append("&lArr;");break; 	//leftward double arrow
					case 8657 :sb.append("&uArr;");break; 	//upward double arrow
					case 8658 :sb.append("&rArr;");break;	//rightward double arrow
					case 8659 :sb.append("&dArr;");break;   //Downward double arrow
					case 8660 :sb.append("&hArr;");break;  	//left-right double
					case 8704 :sb.append("&forall;");break; //for all
					case 8706 :sb.append("&part;");break; 	//Partial differential
					case 8707 :sb.append("&exist;");break;  //there exist
					case 8709 :sb.append("&empty;");break; 	//empty set
					case 8711 :sb.append("&nabla;");break; 	//Nabla, backward difference
					case 8712 :sb.append("&isin;");break; 	//Element of
					case 8713 :sb.append("&notin;");break;	//not an element of
					case 8715 :sb.append("&ni;");break;   	//Contains as member
					case 8719 :sb.append("&prod;");break;  	//n_ary product, product sign
					case 8721 :sb.append("&sum;");break; 	//n-ary sumation
					case 8722 :sb.append("&minus;");break; 	//Minus sign
					case 8727 :sb.append("&lowast;");break; //Asterisk operation
					case 8730 :sb.append("&radic;");break; 	//Square root, radical sign
					case 8733 :sb.append("&prop;");break; 	//proportional to
					case 8734 :sb.append("&infin;");break; 	//infinity
					case 8736 :sb.append("&ang;");break;	//angle
					case 8743 :sb.append("&and;");break; 	//logical and wedge
					case 8744 :sb.append("&or;");break;		//logical or, vee
					case 8745 :sb.append("&cap;");break;	//intersection,cap
					case 8746 :sb.append("&cup;");break; 	//union cup
					case 8747 :sb.append("&int;");break;	//integral
					case 8756 :sb.append("&there4;");break; //therefore
					case 8764 :sb.append("&sim;");break; 	//tilde operator, varies with, similar to
					case 8773 :sb.append("&cong;");break;	//approximaterly equal to
					case 8776 :sb.append("&asyum;");break;  //almost equal to, asymptotic to
					case 8800 :sb.append("&ne;");break;  	//not equal
					case 8801 :sb.append("&equiv;");break; 	//identical to
					case 8804 :sb.append("&le;");break; 		//less - than or equal to
					case 8805 :sb.append("&ge;");break; 	//greater than or equal to
					case 8834 :sb.append("&sub;");break; 	//subset of
					case 8835 :sb.append("&sup;");break; 	//superset of
					case 8836 :sb.append("&nsub;");break; 	//not a subset
					case 8838 :sb.append("&sube;");break;	//subset of or equal to
					case 8839 :sb.append("&supe;");break; 	//superset of equal to
					case 8853 :sb.append("&oplus;");break;	//circled plus, direct sum
					case 8855 :sb.append("&otimes;");break;	//circled times, vector product
					case 8869 :sb.append("&perp;");break; 	//up tack, orthogonal to, perpendicular
					case 8901 :sb.append("&sdot;");break;	//dot operator
					case 8968 :sb.append("&lceil;");break; 	//left ceiling, APL downstile
					case 8969 :sb.append("&rceil;");break; 	//Right ceiling
					case 8970 :sb.append("&lfloor;");break;	//left floor, APL downstile
					case 8971 :sb.append("&rfloor;");break; //right floor
					case 9001 :sb.append("&lang;");break;  	//left pointing angle bracket, bra
					case 9002 :sb.append("&rang;");break; 	//right pointing angle bracket, ket
					case 9674 :sb.append("&loz;");break; 	//lozenge
					case 9824 :sb.append("&spades;");break; //black spade suit
					case 9827 :sb.append("&clubs;");break; 	//black club suit, shamrock
					case 9829 :sb.append("&hearts;");break; //black heart suit, valentine
					case 9830 :sb.append("&diams;");break;	//Black diamond suit

					default:sb.append(' ');
					break;
				}
			}
		}
		return sb.toString();
	}
}

