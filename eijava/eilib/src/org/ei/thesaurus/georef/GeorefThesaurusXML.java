package org.ei.thesaurus.georef;

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
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import org.ei.thesaurus.cpx.Concept;
import org.ei.data.DataLoadDictionary;


public class GeorefThesaurusXML {
	public static final String DESCRIPTOR = "TM";
	public static final String NON_DESCRIPTOR = "NON-DESCRIPTOR";
	public static final String STATUS = "status";
	public static final String SCOPE_NOTES = "SN";
	public static final String USE_TERMS = "U";
	public static final String COORDINATES = "CO";
	public static final String LEADIN_TERMS = "UF";
	public static final String NARROWER_TERMS = "NT";
	public static final String BROADER_TERMS = "BT";
	public static final String RELATED_TERMS = "SA";
	public static final String TYPE = "TY";
	public static final String tab = "\t";
	public static final String comma = ",";
	public static String database="GRF";
	public static String infile=null;
	private DataLoadDictionary dictionary = new DataLoadDictionary();

	public static void main(String args[]){
			String inFile = "THES2008.XML";
			String outFile = "GRF-THESAURUS.out";
			if(args[0]!=null)
			{
				inFile = args[0];
				infile = inFile;
				if(inFile.indexOf(".xml")>0 || inFile.indexOf(".XML")>0)
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
			GeorefThesaurusXML georefThes = new GeorefThesaurusXML();
		    List concepts= georefThes.getConcepts(inFile);
		    georefThes.createSQLLoadFile(concepts,sqlLoadFile);
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
				writer.write(checkString(concept.getCoordinates()));
				//System.out.println("RelatedTerms "+concept.getCoordinates();
				writer.write(checkString(concept.getType()));
				//System.out.println("Type "+concept.getType();
				writer.newLine();
				i++;
			}
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
			  NodeList nodeLst = doc.getElementsByTagName("Term");
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
				  				concept.setMainTermDisplay(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(STATUS)){
				  				concept.setStatus(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(USE_TERMS)){
				  				concept.setUseTerms(cNode.getTextContent().trim());
				  				concept.setStatus("L");
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

				  			if(nodeName.equalsIgnoreCase(TYPE)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
								concept.setType(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(COORDINATES)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
								concept.setCoordinates(getAttributes(cNode.getAttributes()));
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
				//outString = outString+tab;
			}
			else{
				outString = dictionary.mapThesEntity(string)+tab;
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
				childNodeText.append(dictionary.mapEntity(cNode.getTextContent().trim()));
				if(j<childNodes.getLength()-1){
					childNodeText.append(";");
				}
			}

			System.out.println(cNode.getNodeName()+"  "+childNodeText.toString());
		}
		return childNodeText.toString();
	}

	private String getAttributes(NamedNodeMap childNodes)
	{
		StringBuffer childNodeText = new StringBuffer();
		for(int j=0;j<childNodes.getLength();j++){
			Node cNode = childNodes.item(j);
			//System.out.println("ATTRIBUTE= "+cNode.getNodeName()+" ELEMENT= "+cNode.getNodeType());
			if(cNode != null)
			{
				String attributeMap = cNode.getTextContent();
				childNodeText.append(dictionary.mapEntity(attributeMap.trim()));

			}
		}
		//System.out.println("Attribute=  "+childNodeText.toString());
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
