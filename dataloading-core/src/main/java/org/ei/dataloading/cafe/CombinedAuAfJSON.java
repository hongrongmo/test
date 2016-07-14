package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



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

	public void writeRec(AuAfCombinedRec rec) throws Exception
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
		out.println("\t\t\t\"variant\": " + "\"" + notNull(rec.getString(AuAfCombinedRec.AFFILIATION_VARIANT_NAME)) + "\"");
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
}
