package thesaurus.org;

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


public class CPXThesaurusXML2 {
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
		CPXThesaurusXML2 cpxThes = new CPXThesaurusXML2();
		List concepts= cpxThes.getConcepts(inFile);
		cpxThes.createSQLLoadFile(concepts,sqlLoadFile);
	}
	public void createSQLLoadFile(List concepts,File sqlLoadFile) {
		int i=1;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(sqlLoadFile));
			Iterator lit = concepts.iterator();
			writer.write("T_ID"+tab+"DATABASE"+tab+"MAIN_TERM_DISPLAY"+tab+"MAIN_TERM_SEARCH"+tab+"STATUS"+tab+"SCOPE_NOTES"+tab+"HISTORY_SCOPE_NOTES"+
					tab+"USE_TERMS"+tab+"LEADIN_TERMS"+tab+"NARROWER_TERMS"+tab+"BROADER_TERMS"+tab+"RELATED_TERMS"+tab+"TOP_TERMS"+tab+"CLASS_CODES"+
					tab+"PRIOR_TERMS"+tab+"DATE_OF_INTRO");
			writer.newLine();
			while(lit.hasNext()){
				Concept concept = (Concept)lit.next();
				//our PK, the T_ID field
				writer.write(i+tab);
				writer.write(checkString(database));
				writer.write(checkString(concept.getMainTermDisplay()));
				//System.out.println("MainTermDisplay "+concept.getMainTermDisplay().toString());
				//Set the main term search display
				writer.write(checkString(concept.getMainTermDisplay().toLowerCase()));
				//Hanan - testing SearchTerm Display output
				//System.out.println(concept.getMainTermDisplay().toLowerCase());
				writer.write(checkString(concept.getStatus()));
				writer.write(checkString(concept.getScopeNotes()));
				if(database.equalsIgnoreCase("CPX"))
				{
					writer.write(checkString(concept.getHistoryScopeNotes()));
				}
				writer.write(checkStringNoTab(concept.getUseAndTerms().toString()));
				writer.write(checkStringNoTab(concept.getUseOrTerms().toString()));
				writer.write(checkString(concept.getUseTerms().toString()));

				//System.out.println("UseTerms "+concept.getUseTerms().toString());
				writer.write(checkString(concept.getLeadinTerms().toString()));
				//System.out.println("LeadinTerms "+concept.getLeadinTerms().toString());
				writer.write(checkString(concept.getNarrowerTerms().toString()));
				//System.out.println("NarrowerTerms "+concept.getNarrowerTerms().toString());
				writer.write(checkString(concept.getBroaderTerms().toString()));
				//System.out.println("BroaderTerms "+concept.getBroaderTerms().toString());
				writer.write(checkString(concept.getRelatedTerms().toString()));
				//System.out.println("RelatedTerms "+concept.getRelatedTerms().toString());
				if(database.equalsIgnoreCase("CPX"))
				{
					//there are no Top Terms, so null
					writer.write(tab);
					writer.write(checkString(concept.getClassCodes().toString()));
					//there are no Prior Terms, so null
					writer.write(tab);
					if(concept.getDateOfIntro()!=null)
					{
					writer.write(concept.getDateOfIntro());
					
					// Hanan - for testing output
					//System.out.println("Record:" + concept.getDateOfIntro().toString());
					}
					
					// Hanan: fix Empty Date_of_Intro field in Table after loading, add one more tab in case it is null
					/*else
					{
						writer.write(tab);
						writer.write(tab);
						
						System.out.println("Record with DateofIntro Null is :" + concept.getDateOfIntro().toString());
					}*/
				}
				else
				{
					if(concept.getGeographicType()!=null){
						writer.write(checkString(concept.getGeographicType()));
					}
					if(infile!=null && infile.length()>6)
					{
						writer.write(infile.substring(0,6));
					}
				}
				writer.newLine();
				i++;
			}
			//Hanan
			System.out.println("Total Records Processed: "+i);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List getConcepts(String inFile) {
		List concepts = new ArrayList();
		try {
			File file =new File(inFile);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			  doc.getDocumentElement().normalize();
			  NodeList nodeLst = doc.getElementsByTagName("CONCEPT");
			  //System.out.println("Root element " + doc.getDocumentElement().getNodeName());

			  for(int i=0;i<nodeLst.getLength();i++){
				  Concept concept = new Concept();
				  Node node = nodeLst.item(i);
				  NodeList childNodes = node.getChildNodes();
				  	for(int j=0;j<childNodes.getLength();j++){
				  		Node cNode = childNodes.item(j);
				  		if(cNode.getNodeType()==Node.ELEMENT_NODE)
				  		{
				  			String nodeName = cNode.getNodeName().trim();
				  			if(nodeName.equalsIgnoreCase(DESCRIPTOR)){
								concept.setStatus("C");
				  				concept.setMainTermDisplay(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(NON_DESCRIPTOR)){

				  				concept.setStatus("L");
				  				concept.setMainTermDisplay(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(STATUS)){
				  				concept.setStatus(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(HISTORY_SCOPE_NOTES)){
				  				concept.setHistoryScopeNotes(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(USE_TERMS_AND)){
								concept.setUseAndTerms(cNode.getTextContent().trim());
							}
							if(nodeName.equalsIgnoreCase(USE_TERMS_OR)){
								concept.setUseOrTerms(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(USE_TERMS)){
				  				concept.setUseTerms(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(DATE_OF_INTRO)){
				  				concept.setDateOfIntro(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(SCOPE_NOTES)){
				  				concept.setScopeNotes(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(LEADIN_TERMS)){
				  				concept.setLeadinTerm(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(NARROWER_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setNarrowerTerms(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(BROADER_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setBroaderTerms(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(RELATED_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setRelatedTerms(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(OCC)){
				  				concept.setClassCodes(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(MCC)){
				  				concept.setClassCodes(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(PRIOR_TERMS)){
				  				concept.setPriorTerms(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(GEOGRAPHIC_TYPE)){
								concept.setGeographicType(cNode.getTextContent().trim());
				  			}

				  		}
				  }
				  	concepts.add(concept);
			  }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return concepts;
	}

	private String checkString(String string){
		String outString="";
		if(string==null){
			outString = tab;
		}
		if(string!=null){
			if(string.endsWith(";")){
				String noTrailingSemi = string.substring(0, string.length()-1);
				outString = noTrailingSemi+tab;
				if(outString.indexOf("\'")>-1){
					outString = outString.replaceAll("\'", "\"");
					outString = "\'"+outString+"\'";
				}
				outString = outString+tab;
			}
			else{
				outString = string+tab;
			}
		}
		return outString;
	}

	private String checkStringNoTab(String string){
		String outString="";

		if(string!=null){
			if(string.endsWith(";")){
				String noTrailingSemi = string.substring(0, string.length()-1);
				outString = noTrailingSemi;
				if(outString.indexOf("\'")>-1){
					outString = outString.replaceAll("\'", "\"");
					outString = "\'"+outString+"\'";
				}
				outString = outString;
			}
			else{
				outString = string;
			}
		}
		if(outString.length()>0)
		{
			outString = outString+";";
		}
		return outString;
	}
}
