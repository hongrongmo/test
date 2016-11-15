package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ei.common.Constants;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.domain.DataDictionary;
import org.ei.xml.Entity;

import javax.json.*;


public class CombinedAuAfJSON {

	String docid = null;
	String doc_type;
	int loadNumber;
	String root;
	File ESdir;    		 // to hold Extracted JSON AU/AF files
	String currDir;     // to hold current working dir
	String fileName;   // JSON File
	private DataLoadDictionary dictionary = new DataLoadDictionary();

	PrintWriter out;
	
	Map<String,Object> config;   //JsonBuilder config
	JsonBuilderFactory factory; //JsonBuilder Factory

	public CombinedAuAfJSON() {}
	public CombinedAuAfJSON(String doctype, int load_number)
	{
		this.doc_type = doctype;
		this.loadNumber = load_number;
	}

	public void init(int count)
	{
		currDir = System.getProperty("user.dir");
		//ESdir=new File(currDir+"/es/" + this.doc_type + "/" +  this.loadNumber);
		root= currDir+"/es";
		ESdir = new File (root);
		if(!ESdir.exists())
		{
			ESdir.mkdir();
		}
		
		root = root + "/" + this.doc_type;
		ESdir = new File (root);
		if(!ESdir.exists())
		{
			ESdir.mkdir();
		}
		
		root = root + "/" + this.loadNumber + "_" + count;
		ESdir = new File (root);
		if(!ESdir.exists())
		{
			ESdir.mkdir();
		}
		
		config = new HashMap<String,Object>();
		config.put("javax.json.stream.jsonGeneratory.prettyPrinting", Boolean.valueOf(true));
		factory = Json.createBuilderFactory(config);
		
	}
	public void begin()
	{
		try
		{
			fileName = this.ESdir + "/" + this.docid + ".json";
			out = new PrintWriter(new FileWriter(fileName));
			//out = new PrintWriter(new FileWriter(this.docid+".json"));
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void end()
	{
		try
		{
			out.close();
			out.flush();
			out = null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String getEsDirName()
	{
		return this.ESdir.getAbsolutePath();
	}
	// Institution Profile
	public void writeAfRec(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
		
		// if having special characters in ES it need to search for exact special character, so 
		JsonObject esDocument = factory.createObjectBuilder()
				.add("docproperties", factory.createObjectBuilder()
						.add("doc_type", notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)))
						.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)))
						.add("loaddate",notNull(rec.getString(AuAfCombinedRec.LOADDATE)))
						.add("itemtransactionid",replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))))
						.add("indexeddate",notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)))
						.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))))
						.add("loadnumber",notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)))
						)
						.add("afdoc",factory.createObjectBuilder()
								.add("doc_id",notNull(rec.getString(AuAfCombinedRec.DOCID)))
								.add("eid",notNull(rec.getString(AuAfCombinedRec.EID)))
								.add("afid",notNull(rec.getString(AuAfCombinedRec.AFID)))
								.add("sortname",notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)))
								.add("affiliation_name", factory.createObjectBuilder()
										.add("preferred", notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME)))
										.add("variant",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))))
										)
										.add("address", notNull(rec.getString(AuAfCombinedRec.ADDRESS)))
										.add("city",notNull(rec.getString(AuAfCombinedRec.CITY)))
										.add("state",notNull(rec.getString(AuAfCombinedRec.STATE)))
										.add("zip",notNull(rec.getString(AuAfCombinedRec.ZIP)))
										.add("country",notNull(rec.getString(AuAfCombinedRec.COUNTRY)))
										.add("quality",notNull(rec.getString(AuAfCombinedRec.QUALITY)))
										.add("certscore",prepareComposit_field(notNull(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES))))
								)
				.build();
				
				

		//InstitutionCombiner.s3upload.EsDocumentIndex("affiliation", this.docid, profile_contents.toString(), getEsDirName());   // for ES
		//InstitutionCombiner.s3upload.EsBulkIndex("ipr", this.docid, profile_contents.toString(),getEsDirName());

		InstitutionCombiner.esIndex.createBulkIndex("affiliation", this.docid, esDocument);
		
	}
	
	//Author Profile
	public void writeAuRec(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
				
		
		JsonObject esDocument = factory.createObjectBuilder()
				.add("docproperties", factory.createObjectBuilder()
						.add("doc_type", notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)))
						.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)))
						.add("loaddate", notNull(rec.getString(AuAfCombinedRec.LOADDATE)))
						.add("itemtransactionid", replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))))
						.add("indexeddate", notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)))
						.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))))
						.add("loadnumber", notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)))
						)
						.add("audoc", factory.createObjectBuilder()
								.add("doc_id", notNull(rec.getString(AuAfCombinedRec.DOCID)))
								.add("eid", notNull(rec.getString(AuAfCombinedRec.EID)))
								.add("auid",notNull(rec.getString(AuAfCombinedRec.AUID)))
								.add("orcid",notNull(rec.getString(AuAfCombinedRec.ORCID)))
								.add("author_name", factory.createObjectBuilder()
										.add("variant_name", factory.createObjectBuilder()
											.add("variant_first", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_FIRST))))	
											.add("variant_ini",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_INI))))
											.add("variant_last", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_LAST))))
												)
												.add("preferred_name", factory.createObjectBuilder()
														.add("preferred_first", notNull(rec.getString(AuAfCombinedRec.PREFERRED_FIRST)))
														.add("preferred_ini", notNull(rec.getString(AuAfCombinedRec.PREFERRED_INI)))
														.add("preferred_last", notNull(rec.getString(AuAfCombinedRec.PREFERRED_LAST)))
														)
													)
														.add("subjabbr", prepareComposit_field(notNull(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR))))
														.add("subjclus", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER))))
														.add("pubrangefirst", notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST)))
														.add("pubrangelast", notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST)))
														.add("srctitle", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE))))
														//.add("srctitle", normalize(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE)))) // when get srctitle from JOURNAL Col
														.add("issn",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.ISSN))))
														.add("email",notNull(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS)))
														.add("author_affiliations", factory.createObjectBuilder()
																.add("current", factory.createObjectBuilder()
																		.add("afid", notNull(rec.getString(AuAfCombinedRec.AFID)))
																		.add("display_name", notNull(rec.getString(AuAfCombinedRec.DISPLAY_NAME)))
																		.add("display_city", notNull(rec.getString(AuAfCombinedRec.DISPLAY_CITY)))
																		.add("display_country", notNull(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY)))
																		.add("sortname",notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)))
																		)
																		.add("history", factory.createObjectBuilder()
																			.add("afhistid", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID))))
																			.add("history_display_name",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME))))
																			.add("history_city",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_CITY))))
																			.add("history_country", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY))))
																				)
																				.add("affiliation_name", factory.createObjectBuilder()
																						.add("affilprefname",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME))))
																						.add("affilnamevar",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))))
																						)
																						.add("nameid", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.NAME_ID))))
																						.add("deptid",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID)))
																						.add("dept_display_name",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME)))
																						.add("dept_city", notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY)))
																						.add("dept_country", notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY)))
																)
										)
						.build();
		
		//AuthorCombiner.s3upload.UploadFileToS3("apr", "evcafe", this.docid, profile_contents.toString());   // for S3 & Lambda
		//AuthorCombiner.s3upload.EsDocumentIndex("author", this.docid, profile_contents.toString(), getEsDirName());   // for ES using Jest
		AuthorCombiner.esIndex.createBulkIndex("author", this.docid, esDocument);
		
		
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
	
	private String prepareString(String s)
    {
       
        if (!(s.isEmpty()))
        {
        	if(s.contains("\""))
        	{
        		 //s = s.replace("\"", "&quot;");   
        		s = s.replaceAll("\"", "&#x00022;");   //unicode double quotes
        	}
           if(s.contains(","))
           {
        	  // s = s.replace(",", "&#8218;");    //unicode ' or comma
        	   s = s.replace(",", "&#x0002C;");    //unicode  comma
        	   
           }
            
        }
        
        return s;
    }
	
	private String replaceDot(String str)
	{
		//Jest does not accept "." in Field Value, so replace "." with ":" for DateTime Format Fields
		
		if(!(str.isEmpty()))
		{
			str = str.replace(".", ":");
		}
		return str;
	}
	private String prepareMultiValue(String str)
	{
		StringBuffer multiValue = new StringBuffer();
		if(str !=null && !(str.isEmpty()))
		{
			String[] values = str.split(Constants.IDDELIMITER);
			for(int i=0;i<values.length;i++)
			{
				multiValue.append("\"" + values[i] + "\"");
				if(i<values.length -1)
					multiValue.append(" , ");
			}
		}	
		return multiValue.toString();
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
	
	
	private JsonArray prepareComposit_field(String compositField)
	{
		String[] single_pair;
		
		JsonArrayBuilder pairs = factory.createArrayBuilder();
				
		if(!(compositField.isEmpty()))
		{
			String[] composit_pairs = compositField.split(Constants.AUDELIMITER);
			for(int i=0;i<composit_pairs.length;i++)
			{
				single_pair = composit_pairs[i].split(Constants.IDDELIMITER);
				
				pairs.add(single_pair[0] + " " + single_pair[1]);
							
			}
		}
		return pairs.build();

	}
	
	
	/*HH 08/29/2016 toke off this func i created in CombinedXMLWriter away, as i didn ot use it
	in case i need to re-use it then i need to add it back, it is almost same as getStems just added two entries 

	private JsonArray normalize(String str)
	{
		JsonArrayBuilder normallizedStrBuilder = factory.createArrayBuilder();
		Set <String> uniqueSrc_titles = new HashSet<String>(); 
		
		String normalized_scrctitle = "";
		
		if(str !=null && !(str.isEmpty()))
		{
			String[] values = str.split(Constants.IDDELIMITER);
			for(int i=0;i<values.length;i++)
			{
				normalized_scrctitle = CombinedXMLWriter.cafeGetStems(Entity.prepareString(values[i]));
				if(!(uniqueSrc_titles.contains(normalized_scrctitle)))
				{
					uniqueSrc_titles.add(normalized_scrctitle);
				}
			}
		}	
		
		for(String srctitle: uniqueSrc_titles)
		{
			normallizedStrBuilder.add(srctitle);
		}
	
		return normallizedStrBuilder.build();
	}*/
	
}