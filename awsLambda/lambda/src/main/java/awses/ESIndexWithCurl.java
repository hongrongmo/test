package awses;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/*** 
 * 
 * @author TELEBH
 * @Date: 07/31/2016
 * @Description: Index AU/AF ES Profile document in ES using Curl 
 */
public class ESIndexWithCurl {

	public static void main(String [] args) throws IOException
	{
		String esDocument = "{\n\"docproperties\":\n"+
				"{\n"+
		"\"doc_type\": \"apr\",\n"+
		"\"status\": \"update\",\n"+
		"\"loaddate\": \"20160601\",\n"+
		"\"itemtransactionid\": \"2015-09-01T04:32:52.537345Z\",\n"+
		"\"indexeddate\": \"1441081972\",\n"+
		"\"esindextime\": \"2016-07-19T17:52:43.404Z\",\n"+
		"\"loadnumber\": \"401600\"\n"+
	"},\n"+
	"\"audoc\":\n"+ 
	"{\n"+
		"\"docid\": \"aut_M22aaa18f155dfa29a2bM7f9b10178163171\",\n"+
		"\"eid\": \"9-s2.0-56798528800\",\n"+
		"\"auid\": \"56798528800\",\n"+
		"\"orcid\": \"null\",\n"+
		"\"author_name\":\n"+ 
			"{\n"+
				"\"variant_name\":\n"+ 
				"{\n"+
					"\"variant_first\": [  ],\n"+
					"\"variant_ini\": [  ],\n"+
					"\"variant_last\": [  ]\n"+
				"},\n"+
				"\"preferred_name\":\n"+ 
				"{\n"+
					"\"preferred_first\": \"Iv&aacute;n J.\",\n"+
					"\"preferred_ini\": \"I.J.\",\n"+
					"\"preferred_last\": \"Bazany-Rodr&iacute;guez\"\n"+
				"}\n"+
			"},\n"+
		"\"subjabbr\":\n"+ 
		"[\n"+
			"{ \"frequency\": \"3\" , \"code\": \"PHYS\" },\n"+
			"{ \"frequency\": \"5\" , \"code\": \"MATE\" },\n"+
			"{ \"frequency\": \"1\" , \"code\": \"CHEM\" },\n"+
			"{ \"frequency\": \"1\" , \"code\": \"ENGI\" }\n"+
		"],\n"+
		"\"subjclus\": [ \"PHYS\" , \"MATE\" , \"CHEM\" , \"ENGI\" ],\n"+
		"\"pubrangefirst\": \"2015\",\n"+
		"\"pubrangelast\": \"2016\",\n"+
		"\"srctitle\": [ \"Acta Crystallographica Section E: Crystallographic Communications\" , \"Sensors and Actuators, B: Chemical\" ],\n"+
		"\"issn\": [ \"20569890\" , \"09254005\" ],\n"+
		"\"email\": \"\",\n"+
		"\"author_affiliations\":\n"+ 
		"{\n"+
			"\"current\":\n"+ 
			"{\n"+
				"\"afid\": \"60032442\",\n"+
				"\"display_name\": \"Universidad Nacional Autonoma de Mexico\",\n"+
				"\"display_city\": \"Mexico City\",\n"+
				"\"display_country\": \"Mexico\",\n"+
				"\"sortname\": \"National Autonomous University of Mexico\"\n"+
			"},\n"+
			"\"history\":\n"+ 
			"{\n"+
				"\"afhistid\": [  ],\n"+
				"\"history_display_name\": [  ],\n"+
				"\"history_city\": [  ],\n"+
				"\"history_country\": [  ]\n"+
			"},\n"+
			"\"parafid\": [ \"60032442\" ],\n"+
			"\"affiliation_name\":\n"+ 
			"{\n"+
				"\"affilprefname\": [ \"Universidad Nacional Autonoma de Mexico\" ],\n"+
				"\"affilnamevar\": [ \"UNAM\" , \"Universidad Nacional Aut&oacute;noma de M&eacute;xico\" ]\n"+
			"},\n"+
			"\"city\": [ \"Mexico City\" ],\n"+
			"\"country\": [ \"Mexico\" ],\n"+
			"\"nameid\": [ \"Universidad Nacional Autonoma de Mexico#60032442\" ],\n"+
			"\"deptid\": \"104652099\",\n"+
			"\"dept_display_name\": \"Universidad Nacional Autonoma de Mexico, Institute of Chemistry\",\n"+
			"\"dept_city\": \"Mexico City\",\n"+
			"\"dept_country\": \"Mexico\"\n"+
		"}\n"+
	"}\n"+
"}";

		File esdoc = new File("C:/NYC_dataload_split/EngineeringVillage/dataloading-core/es/ipr/101600_1/aff_ec3b4a3155e03b3d6eM7c7b10178163171.json");
		
		ProcessBuilder pb = new ProcessBuilder("curl","-XPOST","http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com/cafe/affiliation/aff_ec3b4a3155e03b3d6eM7c7b10178163171","-d   rg=--data-binary","file=" + esdoc);
		Process p = pb.start();
		OutputStream stream = p.getOutputStream();
		System.out.println(stream.toString());
	}
}
