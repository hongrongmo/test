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

import org.ei.dataloading.cafe.WriteEsDocToFile;

import javax.json.*;

/**
 * 
 * @author TELEBH
 *
 */

public class CombinedAuAfJSON {

	String docid = null;
	String doc_type;
	int loadNumber;
	
	String esIndexType = null;
	
	
	String root;
	File ESdir;    		 // to hold Extracted JSON AU/AF files
	String currDir;     // to hold current working dir
	String fileName;   // JSON File
	private DataLoadDictionary dictionary = new DataLoadDictionary();
	WriteEsDocToFile docWrite;

	PrintWriter out;
	
	Map<String,Object> config;   //JsonBuilder config
	JsonBuilderFactory factory; //JsonBuilder Factory

	
	
	
	public CombinedAuAfJSON() {}
	public CombinedAuAfJSON(String doctype, int load_number)
	{
		this.doc_type = doctype;
		this.loadNumber = load_number;
	}
	public CombinedAuAfJSON(String doctype, int load_number, String esIndxtype)
	{
		this.doc_type = doctype;
		this.loadNumber = load_number;
		this.esIndexType = esIndxtype;
	}
	public CombinedAuAfJSON(String doctype, int load_number, WriteEsDocToFile w, String esIndxtype)
	{
		this.doc_type = doctype;
		this.loadNumber = load_number;
		this.docWrite = w;
		this.esIndexType = esIndxtype;
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
	
	/**
	 * Institution Profile
	 * this was the original version of constructing esdocument, but this included fields even that does not have value, i.e. empty variant[], and this caused these record still shows even when search for "isexist",
	 * these records should of been exludeded from result  hit if field not exist, fix for this done in 2nd version of writeAuRec below
	 * to resolve the issue of isexist in ES Search, so those records with no variant_name.first wont in result hit
	 */
	
	public void writeAfRecOld(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
		
		// if having special characters in ES it need to search for exact special character, so 
		JsonObject esDocument = factory.createObjectBuilder()
				.add("docproperties", factory.createObjectBuilder()
						.add("doc_type", notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)))
						.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)))
						.add("updateepoch",notNull(rec.getString(AuAfCombinedRec.UPDATEEPOCH)))
						.add("loaddate",notNull(rec.getString(AuAfCombinedRec.LOADDATE)))
						.add("itemtransactionid",replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))))
						.add("indexeddate",notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)))
						.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))))
						.add("loadnumber",notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)))
						.add("updatenumber",notNull(rec.getString(AuAfCombinedRec.UPDATE_NUMBER)))
						)
						.add("afdoc",factory.createObjectBuilder()
								.add("doc_id",notNull(rec.getString(AuAfCombinedRec.DOCID)))
								.add("eid",notNull(rec.getString(AuAfCombinedRec.EID)))
								.add("afid",notNull(rec.getString(AuAfCombinedRec.AFID)))
								.add("parafid",notNull(rec.getString(AuAfCombinedRec.PARAFID)))
								.add("aftype", notNull(rec.getString(AuAfCombinedRec.AFTYPE)))
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
										.add("doc_count",notNull(rec.getString(AuAfCombinedRec.DOC_COUNT)))
								)
				.build();
				
				

		//InstitutionCombiner.s3upload.EsDocumentIndex("affiliation", this.docid, profile_contents.toString(), getEsDirName());   // for ES
		//InstitutionCombiner.s3upload.EsBulkIndex("ipr", this.docid, profile_contents.toString(),getEsDirName());

		if(esIndexType !=null && esIndexType.equalsIgnoreCase("direct"))
			InstitutionCombiner.esIndex.createBulkIndex("affiliation", this.docid, esDocument);
		else if(esIndexType !=null && esIndexType.equalsIgnoreCase("file"))
			docWrite.createBulkIndexFile("affiliation", this.docid, esDocument);
			
		
	}
	
	
	/**
	 *  Institution Profile
	 * HH: Author Profile, updated 05/12/2020 to exclude fields having no value from being added to ES Doc, i.e. in previous version if empty field like: variant[], still sent to ES, but in this version it wont even show in ES DOC
	 * to resolve the issue of isexist in ES Search, so those records with no variant_name.first wont in result hit
	 */
	
	public void writeAfRec(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
		
		// if having special characters in ES it need to search for exact special character, so
		
		JsonObject esDocument;
		
		  JsonObjectBuilder AffDocument = factory.createObjectBuilder();
		  JsonObjectBuilder docproperties = factory.createObjectBuilder();
		  JsonObjectBuilder afdoc = factory.createObjectBuilder();
		  JsonObjectBuilder affiliationName = factory.createObjectBuilder();
		  
		  
		  if(rec.getString(AuAfCombinedRec.DOC_TYPE) !=null)
			  docproperties.add("doc_type",notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)));
		  
		if(rec.getString(AuAfCombinedRec.STATUS) != null)
			docproperties.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)));
		if(rec.getString(AuAfCombinedRec.UPDATEEPOCH) != null)
			docproperties.add("updateepoch",notNull(rec.getString(AuAfCombinedRec.UPDATEEPOCH)));
		if(rec.getString(AuAfCombinedRec.LOADDATE) != null)
			docproperties.add("loaddate",notNull(rec.getString(AuAfCombinedRec.LOADDATE)));
		if(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID) != null)
			docproperties.add("itemtransactionid",replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))));
		if(rec.getString(AuAfCombinedRec.INDEXEDDATE) != null)
			docproperties.add("indexeddate",notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)));
		if(rec.getString(AuAfCombinedRec.ESINDEXTIME) !=null)
			docproperties.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))));
		if(rec.getString(AuAfCombinedRec.LOAD_NUMBER) != null)
			docproperties.add("loadnumber",notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)));
		if(rec.getString(AuAfCombinedRec.UPDATE_NUMBER) != null)
			docproperties.add("updatenumber",notNull(rec.getString(AuAfCombinedRec.UPDATE_NUMBER)));
						
		 JsonObject temp = docproperties.build();
		  if(!(temp.isEmpty()))
				  //authorDocument.add("docproperties", docproperties);
			  AffDocument.add("docproperties", temp);
		  
		  if(rec.getString(AuAfCombinedRec.DOCID) != null)
			  afdoc.add("doc_id",notNull(rec.getString(AuAfCombinedRec.DOCID)));
		  if(rec.getString(AuAfCombinedRec.EID) != null)
			  afdoc.add("eid",notNull(rec.getString(AuAfCombinedRec.EID)));
		  if(rec.getString(AuAfCombinedRec.AFID) != null)
			  afdoc.add("afid",notNull(rec.getString(AuAfCombinedRec.AFID)));
		  if(rec.getString(AuAfCombinedRec.PARAFID) != null)
			  afdoc.add("parafid",notNull(rec.getString(AuAfCombinedRec.PARAFID)));
		  if(rec.getString(AuAfCombinedRec.AFTYPE) != null)
			  afdoc.add("aftype", notNull(rec.getString(AuAfCombinedRec.AFTYPE)));
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME) != null)
			  afdoc.add("sortname",notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)));
		  
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME) != null)
			  affiliationName.add("preferred", notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME)));
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME) != null)
			  affiliationName.add("variant",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))));
		  
		  temp = affiliationName.build();
		  if(!(temp.isEmpty()))
			  afdoc.add("affiliation_name", temp);
		  
		  if(rec.getString(AuAfCombinedRec.ADDRESS) != null)
			  afdoc.add("address", notNull(rec.getString(AuAfCombinedRec.ADDRESS)));
		  
		  if(rec.getString(AuAfCombinedRec.CITY) != null)
			  afdoc.add("city",notNull(rec.getString(AuAfCombinedRec.CITY)));
		  if(rec.getString(AuAfCombinedRec.STATE) != null)
			  afdoc.add("state",notNull(rec.getString(AuAfCombinedRec.STATE)));
		  if(rec.getString(AuAfCombinedRec.ZIP) != null)
			  afdoc.add("zip",notNull(rec.getString(AuAfCombinedRec.ZIP)));
		  if(rec.getString(AuAfCombinedRec.COUNTRY) != null)
			  afdoc.add("country",notNull(rec.getString(AuAfCombinedRec.COUNTRY)));
		  if(rec.getString(AuAfCombinedRec.QUALITY) != null)
			  afdoc.add("quality",notNull(rec.getString(AuAfCombinedRec.QUALITY)));
		  if(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES) != null)
			  afdoc.add("certscore",prepareComposit_field(notNull(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES))));
		  if(rec.getString(AuAfCombinedRec.DOC_COUNT) != null)
			  afdoc.add("doc_count",notNull(rec.getString(AuAfCombinedRec.DOC_COUNT)));
							
		 temp = afdoc.build();
		 if(!(temp.isEmpty()))
			 AffDocument.add("afdoc", temp);

		 esDocument = AffDocument.build();
		 
		if(esIndexType !=null && esIndexType.equalsIgnoreCase("direct"))
			InstitutionCombiner.esIndex.createBulkIndex("affiliation", this.docid, esDocument);
		else if(esIndexType !=null && esIndexType.equalsIgnoreCase("file"))
			docWrite.createBulkIndexFile("affiliation", this.docid, esDocument);
			
		
	}
	
	
	/**
	 * Author Profile
	 * this was the original version of constructing esdocument, but this included fields even that does not have value, i.e. empty variant_ini[], and this caused these record still shows even when search for "isexist",
	 * these records should of been exludeded from result  hit if field not exist, fix for this done in 2nd version of writeAuRec below
	 * to resolve the issue of isexist in ES Search, so those records with no variant_name.first wont in result hit
	 */
	
	public void writeAuRecOld(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
				
		
		JsonObject esDocument = factory.createObjectBuilder()
				.add("docproperties", factory.createObjectBuilder()
						.add("doc_type", notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)))
						.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)))
						.add("updateepoch",notNull(rec.getString(AuAfCombinedRec.UPDATEEPOCH)))
						.add("loaddate", notNull(rec.getString(AuAfCombinedRec.LOADDATE)))
						.add("itemtransactionid", replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))))
						.add("indexeddate", notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)))
						.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))))
						.add("loadnumber", notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)))
						.add("updatenumber", notNull(rec.getString(AuAfCombinedRec.UPDATE_NUMBER)))
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
														.add("doc_count",notNull(rec.getString(AuAfCombinedRec.DOC_COUNT)))
										)
						.build();
		
		//AuthorCombiner.s3upload.UploadFileToS3("apr", "evcafe", this.docid, profile_contents.toString());   // for S3 & Lambda
		//AuthorCombiner.s3upload.EsDocumentIndex("author", this.docid, profile_contents.toString(), getEsDirName());   // for ES using Jest
		
		
		if(esIndexType !=null && esIndexType.equalsIgnoreCase("direct"))
			AuthorCombiner.esIndex.createBulkIndex("author", this.docid, esDocument);
		
		else if(esIndexType !=null && esIndexType.equalsIgnoreCase("file"))
			docWrite.createBulkIndexFile("author", this.docid, esDocument);
		
	}
	
	
	/**
	 *  Author Profile
	 * HH: Author Profile, updated 05/12/2020 to exclude fields having no value from being added to ES Doc, i.e. in previous version if empty field like: variant_name.first [], still sent to ES, but in this version it wont even show in ES DOC
	 * to resolve the issue of isexist in ES Search, so those records with no variant_name.first wont in result hit
	 */

	public void writeAuRec(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
				
		
		JsonObject esDocument;
		
		  JsonObjectBuilder authorDocument = factory.createObjectBuilder();
		  JsonObjectBuilder docproperties = factory.createObjectBuilder();
		  JsonObjectBuilder audoc = factory.createObjectBuilder();
		  JsonObjectBuilder authorName = factory.createObjectBuilder();
		  JsonObjectBuilder variantName = factory.createObjectBuilder();
		  JsonObjectBuilder preferedName = factory.createObjectBuilder();
		  JsonObjectBuilder authorAffiliation = factory.createObjectBuilder();
		  JsonObjectBuilder current = factory.createObjectBuilder();
		  JsonObjectBuilder history = factory.createObjectBuilder();
		  JsonObjectBuilder affName = factory.createObjectBuilder();
		  
		  if(rec.getString(AuAfCombinedRec.DOC_TYPE) !=null)
			  docproperties.add("doc_type",notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)));
		  if(rec.getString(AuAfCombinedRec.STATUS) !=null) 
			  docproperties.add("status", notNull(rec.getString(AuAfCombinedRec.STATUS)));
		  if(rec.getString(AuAfCombinedRec.UPDATEEPOCH) !=null && !(rec.getString(AuAfCombinedRec.UPDATEEPOCH).isEmpty()))
			  docproperties.add("updateepoch", notNull(rec.getString(AuAfCombinedRec.UPDATEEPOCH)));
		  
		  if(rec.getString(AuAfCombinedRec.LOADDATE) != null)
			  docproperties.add("loaddate", notNull(rec.getString(AuAfCombinedRec.LOADDATE)));
		  if(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID) != null)
			  docproperties.add("itemtransactionid", replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))));
		  if(rec.getString(AuAfCombinedRec.INDEXEDDATE) != null)
			  docproperties.add("indexeddate", notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)));
		  if(rec.getString(AuAfCombinedRec.ESINDEXTIME) != null)
			  docproperties.add("esindextime",replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))));
		  if(rec.getString(AuAfCombinedRec.LOAD_NUMBER) != null)
			  docproperties.add("loadnumber", notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)));
		  if(rec.getString(AuAfCombinedRec.UPDATE_NUMBER) != null)
			  docproperties.add("updatenumber", notNull(rec.getString(AuAfCombinedRec.UPDATE_NUMBER)));
		  
		  JsonObject temp = docproperties.build();
		  if(!(temp.isEmpty()))
				  //authorDocument.add("docproperties", docproperties);
			  authorDocument.add("docproperties", temp);
		  
		  temp = null;
		  
		  if(rec.getString(AuAfCombinedRec.DOCID) != null)
			  audoc.add("doc_id",notNull(rec.getString(AuAfCombinedRec.DOCID)));
		  if(rec.getString(AuAfCombinedRec.EID) != null)
			  audoc.add("eid",notNull(rec.getString(AuAfCombinedRec.EID)));
		  if(rec.getString(AuAfCombinedRec.AUID) != null)
			  audoc.add("auid",notNull(rec.getString(AuAfCombinedRec.AUID)));
		  if(rec.getString(AuAfCombinedRec.ORCID) != null)
			  audoc.add("orcid",notNull(rec.getString(AuAfCombinedRec.ORCID)));
		  if(rec.getString(AuAfCombinedRec.VARIANT_FIRST) != null && !(rec.getString(AuAfCombinedRec.VARIANT_FIRST).isEmpty()))
			  variantName.add("variant_first",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_FIRST))));
		  if(rec.getString(AuAfCombinedRec.VARIANT_INI) != null && !(rec.getString(AuAfCombinedRec.VARIANT_INI).isEmpty()))
			  variantName.add("variant_ini",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_INI)))) ;
		  if(rec.getString(AuAfCombinedRec.VARIANT_LAST) != null && !(rec.getString(AuAfCombinedRec.VARIANT_LAST).isEmpty()))
			  variantName.add("variant_last",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.VARIANT_LAST))));
		  
		  temp = variantName.build();
		  if(!(temp.isEmpty()))
			  authorName.add("variant_name", temp);
		  temp = null;
		  
		  if(rec.getString(AuAfCombinedRec.PREFERRED_FIRST) != null && !(rec.getString(AuAfCombinedRec.PREFERRED_FIRST).isEmpty()))
			  preferedName.add("preferred_first",notNull(rec.getString(AuAfCombinedRec.PREFERRED_FIRST)));
		  if(rec.getString(AuAfCombinedRec.PREFERRED_INI) != null && !(rec.getString(AuAfCombinedRec.PREFERRED_INI).isEmpty()))
			  preferedName.add("preferred_ini", notNull(rec.getString(AuAfCombinedRec.PREFERRED_INI)));
		  if(rec.getString(AuAfCombinedRec.PREFERRED_LAST) != null && !(rec.getString(AuAfCombinedRec.PREFERRED_LAST).isEmpty()))
			  preferedName.add("preferred_last", notNull(rec.getString(AuAfCombinedRec.PREFERRED_LAST))); 
		  
		  temp = preferedName.build();
		  if(!(temp.isEmpty()))
			  authorName.add("preferred_name", temp);
		 
		  
		 temp = authorName.build();
		  if(!(temp.isEmpty()))
			  audoc.add("author_name", temp);

		  if(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR) != null && !(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR).isEmpty()))
			  audoc.add("subjabbr",prepareComposit_field(notNull(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR))));
		  if(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER) != null && !(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER).isEmpty()))
			  audoc.add("subjclus",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER))));
		  if(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST) != null && !(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST).isEmpty()))
			  audoc.add("pubrangefirst",notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST)));
		  if(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST) != null && !(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST).isEmpty()))
			  audoc.add("pubrangelast",notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST)));
		  if(rec.getString(AuAfCombinedRec.SOURCE_TITLE) != null && !(rec.getString(AuAfCombinedRec.SOURCE_TITLE).isEmpty()))
			  audoc.add("srctitle",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE))));
		  if(rec.getString(AuAfCombinedRec.ISSN) != null && !(rec.getString(AuAfCombinedRec.ISSN).isEmpty()))
			  audoc.add("issn",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.ISSN))));
		  if(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS) != null && !(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS).isEmpty()))
			  audoc.add("email",notNull(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS)));
		  
		  
		  if(rec.getString(AuAfCombinedRec.AFID) != null && !(rec.getString(AuAfCombinedRec.AFID).isEmpty()))
			  current.add("afid",notNull(rec.getString(AuAfCombinedRec.AFID)));
		  if(rec.getString(AuAfCombinedRec.DISPLAY_NAME) != null && !(rec.getString(AuAfCombinedRec.DISPLAY_NAME).isEmpty()))
			  current.add("display_name",notNull(rec.getString(AuAfCombinedRec.DISPLAY_NAME)));
		  if(rec.getString(AuAfCombinedRec.DISPLAY_CITY) != null && !(rec.getString(AuAfCombinedRec.DISPLAY_CITY).isEmpty()))
			  current.add("display_city",notNull(rec.getString(AuAfCombinedRec.DISPLAY_CITY)));
		  if(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY) != null && !(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY).isEmpty()))
			  current.add("display_country",notNull(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY)));
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME) != null && !(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME).isEmpty()))
			  current.add("sortname",notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)));
		  
		  temp = current.build();
		  if(!(temp.isEmpty()))
		  {
			  authorAffiliation.add("current", temp);
		  }
		  
		  
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID) != null && !(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID).isEmpty()))
			  history.add("afhistid",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID))));
		  if(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME) != null && !(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME).isEmpty()))
			  history.add("history_display_name",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME))));
		  if(rec.getString(AuAfCombinedRec.HISTORY_CITY) != null && !(rec.getString(AuAfCombinedRec.HISTORY_CITY).isEmpty()))
			  history.add("history_city",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_CITY)))); 
		  if(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY) != null && !(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY).isEmpty()))
			  history.add("history_country",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY))));
		  
		  temp = history.build();
		  if(!(temp.isEmpty()))
			  authorAffiliation.add("history", temp);
		  
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME) != null && !(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME).isEmpty()))
			  affName.add("affilprefname",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME))));
		  if(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME) != null && !(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME).isEmpty()))
			  affName.add("affilnamevar",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))));
		  
		  temp = affName.build();
		  if(!(temp.isEmpty()))
			  authorAffiliation.add("affiliation_name", temp);
		  
		  if(rec.getString(AuAfCombinedRec.NAME_ID) != null && !(rec.getString(AuAfCombinedRec.NAME_ID).isEmpty()))
			  authorAffiliation.add("nameid",prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.NAME_ID))));
		  if(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID) != null && !(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID).isEmpty()))
			  authorAffiliation.add("deptid",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID)));
		  if(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME) != null && !(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME).isEmpty()))
			  authorAffiliation.add("dept_display_name",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME)));
		  
		  if(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY) != null && !(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY).isEmpty()))
			  authorAffiliation.add("dept_city",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY)));
		  if(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY) != null && !(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY).isEmpty()))
			  authorAffiliation.add("dept_country",notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY)));
		  
		  temp = authorAffiliation.build();
		  if(!(temp.isEmpty()))
			  audoc.add("author_affiliations", temp);
		  
		  if(rec.getString(AuAfCombinedRec.DOC_COUNT) != null && !(rec.getString(AuAfCombinedRec.DOC_COUNT).isEmpty()))
			  audoc.add("doc_count",notNull(rec.getString(AuAfCombinedRec.DOC_COUNT)));
		  
		  temp = audoc.build();
		  if(!(temp.isEmpty()))
			  authorDocument.add("audoc", temp);
		  
		
		  esDocument = authorDocument.build();
		  
		  if(esIndexType !=null && esIndexType.equalsIgnoreCase("direct"))
		  AuthorCombiner.esIndex.createBulkIndex("author", this.docid, esDocument);
		  
		  else if(esIndexType !=null && esIndexType.equalsIgnoreCase("file"))
		  docWrite.createBulkIndexFile("author", this.docid, esDocument);
		 
			
		
		
		
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
				
				if(single_pair.length >1)
				{
					pairs.add(single_pair[0] + " " + single_pair[1]);
				}
				else
				{
					pairs.add(single_pair[0]);
				}
						
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
