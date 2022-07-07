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

import org.ei.thesaurus.cpx.Concept;
import org.ei.dataloading.DataLoadDictionary;


public class InsThesaurusXML {
	public static final String DESCRIPTOR = "term";
	public static final String NON_DESCRIPTOR = "NON-DESCRIPTOR";
	public static final String STATUS = "status";
	public static final String HISTORY_SCOPE_NOTES = "hist";
	public static final String USE_TERMS = "use";
	public static final String USE_TERM = "uset";
	public static final String DATE_OF_INTRO = "di";
	public static final String SCOPE_NOTES = "scope";
	public static final String LEADIN_TERMS = "uf";
	public static final String NARROWER_TERMS = "nt";
	public static final String BROADER_TERMS = "bt";
	public static final String RELATED_TERMS = "rt";
	public static final String TOP_TERMS = "tt";
	public static final String OCC = "cc";
	public static final String MCC = "MCC";
	public static final String PRIOR_TERMS = "pt";
	public static final String CPX = "cpx";
	public static final String GEO = "geo";
	public static final String tab = "\t";
	public static final String comma = ",";
	public static final String GEOGRAPHIC_TYPE = "GT";
	public static String database="INSPEC";
	public static String infile=null;
	private DataLoadDictionary dictionary = new DataLoadDictionary();

	public static void main(String args[]){
			String inFile = "Inspec_thes_2012-001.xml";
			String outFile = "inspecThes2012.out";
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
			InsThesaurusXML insThes = new InsThesaurusXML();
		    List concepts= insThes.getConcepts(inFile);
		    insThes.createSQLLoadFile(concepts,sqlLoadFile);
	}
	public void createSQLLoadFile(List concepts,File sqlLoadFile) {
		int i=1;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(sqlLoadFile));
			Iterator lit = concepts.iterator();
			/*
			writer.write("T_ID"+tab+"DATABASE"+tab+"MAIN_TERM_DISPLAY"+tab+"MAIN_TERM_SEARCH"+tab+"STATUS"+tab+"SCOPE_NOTES"+tab+"HISTORY_SCOPE_NOTES"+
					tab+"USE_TERMS"+tab+"LEADIN_TERMS"+tab+"NARROWER_TERMS"+tab+"BROADER_TERMS"+tab+"RELATED_TERMS"+tab+"TOP_TERMS"+tab+"CLASS_CODES"+
					tab+"PRIOR_TERMS"+tab+"DATE_OF_INTRO");
			writer.newLine();
			*/
			while(lit.hasNext()){
				Concept concept = (Concept)lit.next();
				//our PK, the T_ID field
				writer.write(i+tab);
				writer.write(checkString(database));
				writer.write(checkString(concept.getMainTermDisplay()));
				//System.out.println("MainTermDisplay "+concept.getMainTermDisplay().toString());
				//Set the main term search display
				writer.write(checkString(concept.getMainTermDisplay().toLowerCase()));
				writer.write(checkString(concept.getStatus()));
				writer.write(checkString(concept.getScopeNotes()));
				writer.write(checkString(concept.getHistoryScopeNotes()));
				//writer.write(checkString(concept.getUseTerms().toString()));   // original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getUseTerms().toString()!=null && concept.getUseTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getUseTerms().toString().substring(0, concept.getUseTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
				writer.write(checkString(concept.getUseTerms().toString()));   // original
				}
				// END 
				
				//System.out.println("UseTerms "+concept.getUseTerms().toString());
				
				
				//writer.write(checkString(concept.getLeadinTerms().toString()));  // original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getLeadinTerms().toString()!=null && concept.getLeadinTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getLeadinTerms().toString().substring(0, concept.getLeadinTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getLeadinTerms().toString()));  // original
				}
				// END 
				
				
				//System.out.println("LeadinTerms "+concept.getLeadinTerms().toString());
				//writer.write(checkString(concept.getNarrowerTerms().toString()));     //original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getNarrowerTerms().toString()!=null && concept.getNarrowerTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getNarrowerTerms().toString().substring(0, concept.getNarrowerTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getNarrowerTerms().toString()));  // original
				}
				// END 
				
				
				//System.out.println("NarrowerTerms "+concept.getNarrowerTerms().toString());
				//writer.write(checkString(concept.getBroaderTerms().toString()));  //original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getBroaderTerms().toString()!=null && concept.getBroaderTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getBroaderTerms().toString().substring(0, concept.getBroaderTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getBroaderTerms().toString()));  //original
				}
				// END 
				
				
				//System.out.println("BroaderTerms "+concept.getBroaderTerms().toString());
				//writer.write(checkString(concept.getRelatedTerms().toString()));  //original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getRelatedTerms().toString()!=null && concept.getRelatedTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getRelatedTerms().toString().substring(0, concept.getRelatedTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getRelatedTerms().toString()));  //original
				}
				// END 
				
				
				
				//System.out.println("RelatedTerms "+concept.getRelatedTerms().toString());
				//writer.write(checkString(concept.getTopTerms().toString()));   //original
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getTopTerms().toString()!=null && concept.getTopTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getTopTerms().toString().substring(0, concept.getTopTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getTopTerms().toString()));   //original
				}
				// END 
				
				
				
				//System.out.println("TopTerms "+concept.getTopTerms().toString());
				//writer.write(tab);
				writer.write(checkString(concept.getClassCodes().toString()));
				//writer.write(checkString(concept.getPriorTerms().toString()));   //original
				
				
				//Hanan: fix extra " when use_terms contains single quote
				if(concept.getPriorTerms().toString()!=null && concept.getPriorTerms().toString().indexOf("\'")>-1){
					String noTrailingSemi = concept.getPriorTerms().toString().substring(0, concept.getPriorTerms().toString().length()-1);
					writer.write(noTrailingSemi+tab);
				}
				else
				{
					writer.write(checkString(concept.getPriorTerms().toString()));   //original
				}
				// END 
				//System.out.println("PriorTerms "+concept.getPriorTerms().toString());
				//writer.write(tab);
				if(concept.getDateOfIntro()!=null){
					writer.write(concept.getDateOfIntro().trim());
				}


				writer.newLine();
				i++;
			}
			// Hanan, Total processed terms
			System.out.println("Total Processed items: "+ i);
			
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
			  NodeList nodeLst = doc.getElementsByTagName("rec");
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
				  			if(nodeName.equalsIgnoreCase(DESCRIPTOR))
				  			{
				  				concept.setMainTermDisplay(getMixedValue(cNode));  // original
				  				
				  				//Hanan to fix problem of <sub> that comes in new line of out file-- fixed in getMixedValue instead
				  				//concept.setMainTermDisplay(getMixedValue(cNode).toString().trim());
				  				
				  				
				  				
				  				//System.out.println("MainTermDisplay= "+getMixedValue(cNode));
				  			}

				  			if(nodeName.equalsIgnoreCase(STATUS)){
				  				concept.setStatus(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(HISTORY_SCOPE_NOTES)){
				  				concept.setHistoryScopeNotes(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(USE_TERMS)){
				  				concept.setUseTerms(getChildNode(cNode.getChildNodes()));
				  				concept.setStatus("L");
				  			}

				  			if(nodeName.equalsIgnoreCase(DATE_OF_INTRO)){
				  				concept.setDateOfIntro((cNode.getTextContent()).trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(SCOPE_NOTES)){
				  				concept.setScopeNotes(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(LEADIN_TERMS)){
				  				concept.setLeadinTerm(getChildNode(cNode.getChildNodes()));  
				  				
				  				//Hanan, Fix <sub> from coming to new line
				  				//concept.setLeadinTerm(getChildNode(cNode.getChildNodes()).toString().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(NARROWER_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setNarrowerTerms(getChildNode(cNode.getChildNodes()));
				  			}
				  			if(nodeName.equalsIgnoreCase(BROADER_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setBroaderTerms(getChildNode(cNode.getChildNodes()));
				  			}
				  			if(nodeName.equalsIgnoreCase(RELATED_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setRelatedTerms(getChildNode(cNode.getChildNodes()));
				  			}
				  			if(nodeName.equalsIgnoreCase(OCC)){
				  				concept.setClassCodes(getChildNode(cNode.getChildNodes()));
				  			}

				  			if(nodeName.equalsIgnoreCase(PRIOR_TERMS)){
				  				concept.setPriorTerms(getChildNode(cNode.getChildNodes()));
				  			}

				  			if(nodeName.equalsIgnoreCase(TOP_TERMS)){
								concept.setTopTerms(getChildNode(cNode.getChildNodes()));
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

	private String getMixedValue(Node inputNode)
	{
		StringBuffer mainTermStringBuffer = new StringBuffer();
		if(inputNode.hasChildNodes())
		{
			NodeList tNode = inputNode.getChildNodes();

			for(int k=0;k<tNode.getLength();k++)
			{
				if(tNode.item(k).getNodeType()==Node.ELEMENT_NODE)
				{
					//mainTermStringBuffer.append("<"+tNode.item(k).getNodeName()+">"+dictionary.mapEntity(tNode.item(k).getTextContent().trim())+"</"+tNode.item(k).getNodeName()+">");
					mainTermStringBuffer.append("<"+tNode.item(k).getNodeName()+">"+tNode.item(k).getTextContent().trim()+"</"+tNode.item(k).getNodeName()+">");
					
					//Hanan - for testing <Sub> issue
					//System.out.println("<"+tNode.item(k).getNodeName()+">"+tNode.item(k).getTextContent()+"</"+tNode.item(k).getNodeName()+">");
					
				}
				else
				{
					mainTermStringBuffer.append(tNode.item(k).getTextContent());
				}
			}
		}
		//return mainTermStringBuffer.toString();  // original
		
		// Hanan: fixed <sub> issue
		return mainTermStringBuffer.toString().trim();
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
				//outString = outString+tab;
			}
			else{
				outString = string+tab;
			}
		}
		return outString;
	}

	private String getChildNode(NodeList childNodes)
	{
		StringBuffer childNodeText = new StringBuffer();
		for(int j=0;j<childNodes.getLength();j++){
			Node cNode = childNodes.item(j);
			if(cNode.getNodeType()==Node.ELEMENT_NODE)
			{
				//childNodeText.append(getMixedValue(cNode));  // original
				
				//Hanan: fixed <sub> from coming to new line
				childNodeText.append(getMixedValue(cNode).toString().trim());

				//childNodeText.append(dictionary.mapEntity(cNode.getTextContent().trim()));


				if(j<childNodes.getLength()-1){
					childNodeText.append(";");
				}
			}
		}
		return childNodeText.toString();
		
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
