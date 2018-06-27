 package org.ei.thesaurus.cpx;

 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.List;
 import java.util.Iterator;
 import java.util.ArrayList;
 import java.util.ListIterator;

 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;

 import org.w3c.dom.Document;
 import org.w3c.dom.Text;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;


 public class CPXClassification extends CPXThesaurusXML2{
 	public static final String DESCRIPTOR = "DESCRIPTOR";
 	public static final String NON_DESCRIPTOR = "NON-DESCRIPTOR";
 	public static final String STATUS = "STA";
 	public static final String HISTORY_SCOPE_NOTES = "TT2";
 	public static final String USE_TERMS = "USE";
 	public static final String USE_TERMS_OR = "UO";
 	public static final String USE_TERMS_AND = "UA";
 	public static final String DATE_OF_INTRO = "DT";
 	public static final String SCOPE_NOTES = "SN";
 	public static final String LEADIN_TERMS = "UF";
 	public static final String NARROWER_TERMS = "NT";
 	public static final String BROADER_TERMS = "BT";
 	public static final String RELATED_TERMS = "RT";
 	public static final String OCC = "OCC";
 	public static final String MCC = "MCC";
 	public static final String PRIOR_TERMS = "PT";
 	public static final String CPX = "cpx";
 	public static final String GEO = "geo";
 	public static final String tab = "\t";
 	public static final String comma = ",";
 	public static final String GEOGRAPHIC_TYPE = "GT";
 	public static String database="CPX";
 	public static String infile=null;

 	public static void main(String args[]){
 		String inFile = "cpxtree2011terms.xml";
 		String outFile = "thes_012411_test.out";
 		if(args[0]!=null)
 		{
 			inFile = args[0];
 			infile = inFile;
 			if(inFile.indexOf(".xml")>0)
 			{
 				outFile = inFile.substring(0,inFile.indexOf("."))+".out";
 				System.out.println("OUTPUT-FILE "+outFile);
 			}
 		}

 		if(args.length>1 && args[1]!=null)
 		{
 			database = args[1];
 		}


 		File sqlLoadFile = new File(outFile);
 		//CPXThesaurusXML2 cpxThes = new CPXThesaurusXML2();
		CPXClassification cpxClassification = new  CPXClassification();
 		List concepts= cpxClassification.getConcepts(inFile);
 		cpxClassification.createSQLLoadFile(concepts,sqlLoadFile);
 	}
 	public void createSQLLoadFile(List concepts,File sqlLoadFile) {
 		int i=1;
 		try {
 			BufferedWriter writer = new BufferedWriter(new FileWriter(sqlLoadFile));
 			Iterator lit = concepts.iterator();
 			writer.write("T_ID"+tab+"DATABASE"+tab+"CODE"+tab+"Short_Description"+tab+"Long_Description");
 			writer.newLine();
 			while(lit.hasNext()){
 				Concept concept = (Concept)lit.next();
 				//our PK, the T_ID field
 				writer.write(i+tab);
 				writer.write(checkString(database));
 				writer.write(checkString(concept.getMainTermDisplay()));
 				writer.write(checkString(concept.getCDE().toLowerCase()));
 				writer.write(checkString(concept.getCSS()));

 				writer.newLine();
 				i++;
 			}
 			writer.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
	}

}
