package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.ei.common.Constants;
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
		
		StringBuffer profile_contents = new StringBuffer();
		
		/*begin();
		out.println("{");
		out.println("\t\"docproperties\":");
		out.println("\t{");
		out.println("\t\t\"doc_type\": " + "\"" + rec.getString(AuAfCombinedRec.DOC_TYPE) + "\",");
		out.println("\t\t\"status\": " + "\"" + rec.getString(AuAfCombinedRec.STATUS) + "\",");
		out.println("\t\t\"loaddate\": " + "\"" + rec.getString(AuAfCombinedRec.LOADDATE) + "\",");
		out.println("\t\t\"itemtransactionid\": " + "\"" + rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID) + "\",");
		out.println("\t\t\"indexeddate\": " + "\"" + rec.getString(AuAfCombinedRec.INDEXEDDATE) + "\",");
		out.println("\t\t\"esindextime\": " + "\"" + rec.getString(AuAfCombinedRec.ESINDEXTIME) + "\",");
		out.println("\t\t\"loadnumber\": " + "\"" + rec.getString(AuAfCombinedRec.LOAD_NUMBER) + "\"");
		out.println("\t},");
		             	 
		out.println("\t\"afdoc\": ");
		out.println("\t{");             	
		out.println("\t\t\"doc_id\": " + "\"" + rec.getString(AuAfCombinedRec.DOCID) + "\",") ;
		out.println("\t\t\"eid\": " + "\"" + rec.getString(AuAfCombinedRec.EID) + "\",");
		out.println("\t\t\"afid\": "+ "\"" + rec.getString(AuAfCombinedRec.AFID) + "\",");
		out.println("\t\t\"sortname\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)) + "\",");
		out.println("\t\t\"affiliation_name\":");
		out.println("\t\t {");
		out.println("\t\t\t\"preferred\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME)) + "\",");
		out.println("\t\t\t\"variant\": " + "[ " + prepareMultiValue(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME)) + " ]");
		out.println("\t\t },");		
		out.println("\t\t\"address\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ADDRESS)) + "\",");
		out.println("\t\t\"city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CITY)) + "\",");
		out.println("\t\t\"state\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.STATE)) + "\",");
		out.println("\t\t\"zip\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ZIP)) + "\",");
		out.println("\t\t\"country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.COUNTRY)) + "\",");
		out.println("\t\t\"quality\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.QUALITY)) + "\",");
		out.println("\t\t\"certscore\": [" + prepareSubjabbr_Value(notNull(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES)))+ "]");
		out.println("\t}");
		out.println("}");

		end();
		*/
		
		
		
		/*profile_contents.append("{\n");
		profile_contents.append("\t\"docproperties\":\n");
		profile_contents.append("\t{\n");
		profile_contents.append("\t\t\"doc_type\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)) + "\",\n");
		profile_contents.append("\t\t\"status\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.STATUS)) + "\",\n");
		profile_contents.append("\t\t\"loaddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOADDATE)) + "\",\n");
		profile_contents.append("\t\t\"itemtransactionid\": " + "\"" + replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))) + "\",\n");
		profile_contents.append("\t\t\"indexeddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)) + "\",\n");
		profile_contents.append("\t\t\"esindextime\": " + "\"" + replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))) + "\",\n");
		profile_contents.append("\t\t\"loadnumber\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)) + "\"\n");
		profile_contents.append("\t},\n");
		             	 
		profile_contents.append("\t\"afdoc\": \n");
		profile_contents.append("\t{\n");             	
		profile_contents.append("\t\t\"doc_id\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOCID)) + "\",\n") ;
		profile_contents.append("\t\t\"eid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.EID)) + "\",\n");
		profile_contents.append("\t\t\"afid\": "+ "\"" + notNull(rec.getString(AuAfCombinedRec.AFID)) + "\",\n");
		profile_contents.append("\t\t\"sortname\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)) + "\",\n");
		profile_contents.append("\t\t\"affiliation_name\":\n");
		profile_contents.append("\t\t {\n");
		profile_contents.append("\t\t\t\"preferred\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME)) + "\",\n");
		profile_contents.append("\t\t\t\"variant\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))) + " ]\n");
		profile_contents.append("\t\t },\n");		
		profile_contents.append("\t\t\"address\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ADDRESS)) + "\",\n");
		profile_contents.append("\t\t\"city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CITY)) + "\",\n");
		profile_contents.append("\t\t\"state\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.STATE)) + "\",\n");
		profile_contents.append("\t\t\"zip\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ZIP)) + "\",\n");
		profile_contents.append("\t\t\"country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.COUNTRY)) + "\",\n");
		profile_contents.append("\t\t\"quality\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.QUALITY)) + "\",\n");
		
		profile_contents.append("\t\t\"certscore\": [" + prepareCertainity_Scores(notNull(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES)))+ "]\n");
		
		profile_contents.append("\t}\n");
		profile_contents.append("}\n");
		*/
		
		
		JsonObject esDocument = factory.createObjectBuilder()
				.add("docproperties", factory.createObjectBuilder()
						.add("doc_type", notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)))
						.add("status",notNull(rec.getString(AuAfCombinedRec.STATUS)))
						.add("loaddate",notNull(rec.getString(AuAfCombinedRec.LOADDATE)))
						.add("itemtransactionid",replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))))
						.add("indexeddate",notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)))
						.add("esindextime", replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))))
						.add("loadnumber",notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)))
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
										.add("certscore",prepareCertainityScores(notNull(rec.getString(AuAfCombinedRec.CERTAINITY_SCORES))))
								)
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
		StringBuffer profile_contents = new StringBuffer();
		
		/*begin();
		out.println("{");
		out.println("\t\"docproperties\":");
		out.println("\t{");
		out.println("\t\t\"doc_type\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)) + "\",");
		out.println("\t\t\"status\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.STATUS)) + "\",");
		out.println("\t\t\"loaddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOADDATE)) + "\",");
		out.println("\t\t\"itemtransactionid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID)) + "\",");
		out.println("\t\t\"indexeddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)) + "\",");
		out.println("\t\t\"esindextime\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME)) + "\",");
		out.println("\t\t\"loadnumber\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)) + "\"");
		out.println("\t},");
		             	 
		out.println("\t\"audoc\": ");
		out.println("\t{");             	
		out.println("\t\t\"doc_id\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOCID)) + "\",") ;
		out.println("\t\t\"eid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.EID)) + "\",");
		out.println("\t\t\"auid\": "+ "\"" + notNull(rec.getString(AuAfCombinedRec.AUID)) + "\",");
		out.println("\t\t\"orcid\": "+ "\"" + notNull(rec.getString(AuAfCombinedRec.ORCID)) + "\",");
		out.println("\t\t\"author_name\": ");
		out.println("\t\t\t{");
		out.println("\t\t\t\t\"variant_name\": ");
		out.println("\t\t\t\t{");
		out.println("\t\t\t\t\t\"variant_first\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_FIRST))) + " ],");
		out.println("\t\t\t\t\t\"variant_ini\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_INI))) + " ],");
		out.println("\t\t\t\t\t\"variant_last\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_LAST))) + " ]");
		out.println("\t\t\t\t},");
		
		
		out.println("\t\t\t\t\"preferred_name\": ");
		out.println("\t\t\t\t{");
		out.println("\t\t\t\t\t\"preferred_first\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PREFERRED_FIRST)) + "\",");
		out.println("\t\t\t\t\t\"preferred_ini\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PREFERRED_INI)) + "\",");
		out.println("\t\t\t\t\t\"preferred_last\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PREFERRED_LAST)) + "\"");
		out.println("\t\t\t\t}");
		out.println("\t\t\t},");
		
		
		out.println("\t\t\"subjabbr\": ");
		out.println("\t\t[");
		out.println("\t\t\t" + prepareSubjabbr_Value(notNull(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR))));
		out.println("\t\t],");
		
		out.println("\t\t\"subjclus\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER))) + " ],");
		out.println("\t\t\"pubrangefirst\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST)) + "\",");
		out.println("\t\t\"pubrangelast\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST)) + "\",");
		
		out.println("\t\t\"srctitle\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE))) + " ],");
		
		out.println("\t\t\"issn\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.ISSN))) + " ],");
		
		out.println("\t\t\"email\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS)) + "\",");
		
		out.println("\t\t\"author_affiliations\": ");
		out.println("\t\t{");
		out.println("\t\t\t\"current\": ");
		out.println("\t\t\t{");
		out.println("\t\t\t\t\"afid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFID)) + "\",");
		out.println("\t\t\t\t\"display_name\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_NAME)) + "\",");
		out.println("\t\t\t\t\"display_city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_CITY)) + "\",");
		out.println("\t\t\t\t\"display_country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY)) + "\",");
		out.println("\t\t\t\t\"sortname\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)) + "\"");
		out.println("\t\t\t},");
		
		out.println("\t\t\t\"history\": ");
		out.println("\t\t\t{");
		out.println("\t\t\t\t\"afhistid\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID))) + " ],");
		out.println("\t\t\t\t\"history_display_name\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME))) + " ],");
		out.println("\t\t\t\t\"history_city\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_CITY))) + " ],");
		out.println("\t\t\t\t\"history_country\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY))) + " ]");
		out.println("\t\t\t},");
		
		//out.println("\t\t\t\"parafid\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.PARENT_AFFILIATION_ID))) + " ],");
		
		
		out.println("\t\t\t\"affiliation_name\": ");
		out.println("\t\t\t{");
		out.println("\t\t\t\t\"affilprefname\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME))) + " ],");
		out.println("\t\t\t\t\"affilnamevar\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))) + " ]");
		out.println("\t\t\t},");
		
		//HH 07/25/2016 Copied @ ES Profile level
		out.println("\t\t\t\"city\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.CITY))) + " ],");
		out.println("\t\t\t\"country\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.COUNTRY))) + " ],");
		out.println("\t\t\t\"nameid\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.NAME_ID))) + " ],");
		
		
		out.println("\t\t\t\"deptid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID)) + "\",");
		out.println("\t\t\t\"dept_display_name\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME)) + "\",");
		out.println("\t\t\t\"dept_city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY)) + "\",");
		out.println("\t\t\t\"dept_country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY)) + "\"");
		
		out.println("\t\t}");
		out.println("\t}");
		
		out.println("}");

		end();*/
		/*profile_contents.append("{");
		profile_contents.append("\"docproperties\":");
		profile_contents.append("{");
		profile_contents.append("\"doc_type\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOC_TYPE)) + "\",");
		profile_contents.append("\"status\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.STATUS)) + "\",");
		profile_contents.append("\"loaddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOADDATE)) + "\",");
		profile_contents.append("\"itemtransactionid\": " + "\"" + replaceDot(notNull(rec.getString(AuAfCombinedRec.ITEMTRANSACTIONID))) + "\",");
		profile_contents.append("\"indexeddate\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.INDEXEDDATE)) + "\",\n");
		profile_contents.append("\"esindextime\": " + "\"" + replaceDot(notNull(rec.getString(AuAfCombinedRec.ESINDEXTIME))) + "\",");
		profile_contents.append("\"loadnumber\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.LOAD_NUMBER)) + "\"");
		profile_contents.append("},");
		             	 
		profile_contents.append("\"audoc\": ");
		profile_contents.append("{");             	
		profile_contents.append("\"doc_id\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOCID)) + "\",") ;
		profile_contents.append("\"eid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.EID)) + "\",");
		profile_contents.append("\"auid\": "+ "\"" + notNull(rec.getString(AuAfCombinedRec.AUID)) + "\",");
		profile_contents.append("\"orcid\": "+ "\"" + notNull(rec.getString(AuAfCombinedRec.ORCID)) + "\",");
		profile_contents.append("\"author_name\": ");
		profile_contents.append("{");
		profile_contents.append("\"variant_name\": ");
		profile_contents.append("{");
		profile_contents.append("\"variant_first\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_FIRST))) + " ],");
		profile_contents.append("\"variant_ini\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_INI))) + " ],");
		profile_contents.append("\"variant_last\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.VARIANT_LAST))) + " ]");
		profile_contents.append("},");
		
		
		profile_contents.append("\"preferred_name\": ");
		profile_contents.append("{");
		profile_contents.append("\"preferred_first\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PREFERRED_FIRST)) + "\",");
		profile_contents.append("\"preferred_ini\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PREFERRED_INI)) + "\",");
		profile_contents.append("\"preferred_last\": " + "\"" +notNull(rec.getString(AuAfCombinedRec.PREFERRED_LAST)) + "\"");
		profile_contents.append("}");
		profile_contents.append("},");
		
		
		profile_contents.append("\"subjabbr\": [" +  prepareSubjabbr_Value(notNull(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR)))+ "],");

		
		profile_contents.append("\"subjclus\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER))) + " ],");
		profile_contents.append("\"pubrangefirst\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST)) + "\",");
		profile_contents.append("\"pubrangelast\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST)) + "\",");
		
		profile_contents.append("\"srctitle\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE))) + " ],");
		
		profile_contents.append("\"issn\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.ISSN))) + " ],");
		
		profile_contents.append("\"email\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.EMAIL_ADDRESS)) + "\",");
		
		profile_contents.append("\"author_affiliations\": ");
		profile_contents.append("{");
		profile_contents.append("\"current\": ");
		profile_contents.append("{");
		profile_contents.append("\"afid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFID)) + "\",");
		profile_contents.append("\"display_name\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_NAME)) + "\",");
		profile_contents.append("\"display_city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_CITY)) + "\",");
		profile_contents.append("\"display_country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DISPLAY_COUNTRY)) + "\",");
		profile_contents.append("\"sortname\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_SORT_NAME)) + "\"");
		profile_contents.append("},");
		
		profile_contents.append("\"history\": ");
		profile_contents.append("{");
		profile_contents.append("\"afhistid\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_HISTORY_ID))) + " ],");
		profile_contents.append("\"history_display_name\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_DISPLAY_NAME))) + " ],");
		profile_contents.append("\"history_city\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_CITY))) + " ],");
		profile_contents.append("\"history_country\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.HISTORY_COUNTRY))) + " ]");
		profile_contents.append("},");
		
		//out.println("\t\t\t\"parafid\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.PARENT_AFFILIATION_ID))) + " ],");
		
		
		profile_contents.append("\"affiliation_name\": ");
		profile_contents.append("{");
		profile_contents.append("\"affilprefname\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME))) + " ],");
		profile_contents.append("\"affilnamevar\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))) + " ]");
		profile_contents.append("},");
		
		//HH 07/25/2016 Copied @ ES Profile level
		//out.println("\t\t\t\"city\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.CITY))) + " ],");
		//out.println("\t\t\t\"country\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.COUNTRY))) + " ],");
		profile_contents.append("\"nameid\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.NAME_ID))) + " ],");
		
		
		profile_contents.append("\"deptid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID)) + "\",");
		profile_contents.append("\"dept_display_name\": " + "\"" + DataLoadDictionary.mapUnicodeEntity(notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME))) + "\",");
		profile_contents.append("\"dept_city\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY)) + "\",");
		profile_contents.append("\"dept_country\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY)) + "\"");
		
		profile_contents.append("}");
		profile_contents.append("}");
		
		profile_contents.append("}");
		*/
		
		
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
														.add("subjabbr", prepareSubjabbr_Values(notNull(rec.getString(AuAfCombinedRec.CLASSIFICATION_SUBJABBR))))
														.add("subjclus", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SUBJECT_CLUSTER))))
														.add("pubrangefirst", notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_FIRST)))
														.add("pubrangelast", notNull(rec.getString(AuAfCombinedRec.PUBLICATION_RANGE_LAST)))
														.add("srctitle", prepareMultiValues(notNull(rec.getString(AuAfCombinedRec.SOURCE_TITLE))))
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
		JsonArrayBuilder builder = Json.createArrayBuilder();
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
	
	private String prepareSubjabbr_Value(String subjabbr)
	{
		StringBuffer frequency_code_pairs = new StringBuffer();
		String[] single_subjabbr;
		
		if(!(subjabbr.isEmpty()))
		{
			String[] subjabbrs = subjabbr.split(Constants.AUDELIMITER);
			for(int i=0;i<subjabbrs.length;i++)
			{
				single_subjabbr = subjabbrs[i].split(Constants.IDDELIMITER);
				frequency_code_pairs.append("{ \"frequency\": \"" + single_subjabbr[0] + "\" , \"code\": \"" + single_subjabbr[1] + "\" }");
				if(i<subjabbrs.length-1)
				{
					//frequency_code_pairs.append(",\n\t\t\t");
					frequency_code_pairs.append(",");
				}
			}
		}
		return frequency_code_pairs.toString();

	}
	
	private JsonArray prepareSubjabbr_Values(String subjabbr)
	{
		String[] single_subjabbr;
		
		JsonArrayBuilder frequency_code_pairs = factory.createArrayBuilder();
				
		if(!(subjabbr.isEmpty()))
		{
			String[] subjabbrs = subjabbr.split(Constants.AUDELIMITER);
			for(int i=0;i<subjabbrs.length;i++)
			{
				single_subjabbr = subjabbrs[i].split(Constants.IDDELIMITER);
				
				frequency_code_pairs
						.add(factory.createObjectBuilder()
								.add("frequency", single_subjabbr[0])
								.add("code", single_subjabbr[1])
								);
				
			}
		}
		return frequency_code_pairs.build();

	}
	
	
	private String prepareCertainity_Scores(String certainity_scores)
	{
		StringBuffer frequency_code_pairs = new StringBuffer();
		String[] single_subjabbr;
		
		if(!(certainity_scores.isEmpty()))
		{
			String[] subjabbrs = certainity_scores.split(Constants.AUDELIMITER);
			for(int i=0;i<subjabbrs.length;i++)
			{
				single_subjabbr = subjabbrs[i].split(Constants.IDDELIMITER);
				frequency_code_pairs.append("{ \"org-id\": \"" + single_subjabbr[0] + "\" , \"score\": \"" + single_subjabbr[1] + "\" }");
				if(i<subjabbrs.length-1)
				{
					//frequency_code_pairs.append(",\n\t\t\t");
					frequency_code_pairs.append(",");
				}
			}
		}
		return frequency_code_pairs.toString();

	}
	
	
	private JsonArray prepareCertainityScores(String certainity_scores)
	{		
		String[] single_score;
		JsonArrayBuilder certainity_score_pairs = factory.createArrayBuilder();
		
		if(!(certainity_scores.isEmpty()))
		{
			String[] scores = certainity_scores.split(Constants.AUDELIMITER);
			for(int i=0;i<scores.length;i++)
			{
				single_score = scores[i].split(Constants.IDDELIMITER);
				
				certainity_score_pairs
				.add(factory.createObjectBuilder()
						.add("org-id", single_score[0])
						.add("score", single_score[1])
						);
			}
		}
		return certainity_score_pairs.build();

	}
	
}
