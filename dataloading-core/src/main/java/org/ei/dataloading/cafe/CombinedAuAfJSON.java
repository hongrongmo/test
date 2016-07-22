package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.ei.common.Constants;



public class CombinedAuAfJSON {

	String docid = null;
	String doc_type;
	int loadNumber;
	String root;
	File ESdir;    		 // to hold Extracted JSON AU/AF files
	String currDir;     // to hold current working dir
	String fileName;   // JSON File


	PrintWriter out;

	public CombinedAuAfJSON() {}
	public CombinedAuAfJSON(String doctype, int load_number)
	{
		this.doc_type = doctype;
		this.loadNumber = load_number;
	}

	public void init()
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
		
		root = root + "/" + this.loadNumber;
		ESdir = new File (root);
		if(!ESdir.exists())
		{
			ESdir.mkdir();
		}
		
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
		
		begin();
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
		out.println("\t\t\"docid\": " + "\"" + rec.getString(AuAfCombinedRec.DOCID) + "\",") ;
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
		out.println("\t\t\"quality\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.QUALITY)) + "\"");
		out.println("\t}");
		out.println("}");

		end();
	}
	
	//Author Profile
	public void writeAuRec(AuAfCombinedRec rec) throws Exception
	{
		this.docid = rec.getString(AuAfCombinedRec.DOCID);
		// take off Entity.prepareString, as we need to extract AU/AF content to ES exact same as in DB, then EV web need to use same mapping to do search
		
		begin();
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
		out.println("\t\t\"docid\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.DOCID)) + "\",") ;
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
		
		out.println("\t\t\t\"parafid\": " + "[ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.PARENT_AFFILIATION_ID))) + " ],");
		
		
		out.println("\t\t\t\"affiliation_name\": ");
		out.println("\t\t\t{");
		out.println("\t\t\t\t\"affilprefname\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME))) + " ],");
		out.println("\t\t\t\t\"affilnamevar\": [ " + prepareMultiValue(notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME))) + " ]");
		out.println("\t\t\t},");
		
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

		end();
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
					frequency_code_pairs.append(",\n\t\t\t");
				}
			}
		}
		return frequency_code_pairs.toString();

	}
	
	
}
