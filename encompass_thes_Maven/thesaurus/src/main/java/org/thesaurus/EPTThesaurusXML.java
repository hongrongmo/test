package org.thesaurus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import org.ei.thesaurus.cpx.*;

/*
//HH 12/15/2014 for new line issue of <SRH> , <INF>

import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;*/



public class EPTThesaurusXML {
	public static final String DESCRIPTOR = "DESCRIPTOR";
	public static final String NON_DESCRIPTOR = "NON-DESCRIPTOR";
	public static final String TYPE = "TYPE";
	public static final String YEAR = "YEAR";
	public static final String CAS_NUMBER = "CAS"; 				//CAS number
	public static final String CAS_NUMBER_BROADER_TERM = "CBT"; //CAS number for the broader term
	public static final String CHEMICAL_ASPECTS = "CA";   		//Chemical Aspects
	public static final String BROADER_TERMS = "BT";			//Broader terms
	public static final String NARROWER_TERMS = "NT";			//Narrower_terms
	public static final String SA = "SA";						//SA
	public static final String SEE = "SEE";						//SEE
	public static final String LABEL = "LABEL";					//LABEL
	public static final String PLUS = "PLUS";					//PLUS
	public static final String STRING = "STRING";				//STRING
	public static final String USE_FOR = "UF";					//USE FOR
	public static final String USE_TERMS = "USE";				//USE
	public static final String SEARCHING_INFORMATION = "SRH";	//SEARCHING INFORMATION
	public static final String SCOPE_NOTES = "INF";				//SCOPE NOTE;
	public static final String tab = "\t";


	public static String database="CPX";
	public static String infile=null;
	
	/*
	//HH 12/15/2014 to fix issue of new line of tags <SRH> , <INF>
	private Perl5Util perl = new Perl5Util();
	String extraspace = "            ";  
	String text = null;
	int reccount = 0;
	*/

	public static void main(String args[]){
			String inFile = "encompassTEST2014.xml";
			String outFile = "encompassTEST2014.out";
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
			EPTThesaurusXML eptThes = new EPTThesaurusXML();
		    List concepts= eptThes.getConcepts(inFile);
		    eptThes.createSQLLoadFile(concepts,sqlLoadFile);
	}
	public void createSQLLoadFile(List concepts,File sqlLoadFile) {
		int i=1;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(sqlLoadFile));
			Iterator lit = concepts.iterator();
			writer.write("T_ID"+tab+"DATABASE"+tab+"MAIN_TERM_DISPLAY"+tab+"MAIN_TERM_SEARCH"+tab+"TYPE"+tab+"CAS_NUMBER"+
					tab+"CASE_NUMBER_BROADER_TERM"+tab+"BROADER_TERMS"+tab+"NARROWER_TERMS"+tab+"SA"+tab+"SEE"+tab+"LEADIN_TERMS"+
					tab+"USE_TERMS"+tab+"SEARCHING_INFORMATION"+tab+"SCOPE_NOTES"+tab+"USE_FOR");
			writer.newLine();
			while(lit.hasNext()){
				Concept concept = (Concept)lit.next();
				//our PK, the T_ID field
				writer.write(i+tab);
				writer.write(checkString(database));
				writer.write(checkString(concept.getMainTermDisplay().trim()));
				//System.out.println("MainTermDisplay "+concept.getMainTermDisplay().toString());
				//Set the main term search display
				writer.write(checkString(concept.getMainTermDisplay().toLowerCase().trim()));
				writer.write(checkString(concept.getType()));
				writer.write(checkString(concept.getYear()));
				writer.write(checkString(concept.getCasNumber()));
				writer.write(checkString(concept.getCasNumberBroadTerm()));
				writer.write(checkString(concept.getBroaderTerms().toString()));
				writer.write(checkString(concept.getNarrowerTerms().toString()));
				if(concept.getNarrowerTerms().toString().length()>4000)
				{
					System.out.println("NarrowerTerms over 4000"+i);
				}
				writer.write(checkString(concept.getSA().toString()));
				if(concept.getSA().toString().length()>4000)
				{
					System.out.println("SA over 4000"+i);
				}
				writer.write(checkString(concept.getSEE().toString()));
				if(concept.getSEE().toString().length()>4000)
				{
					System.out.println("SEE over 4000"+i);
				}
				writer.write(checkString(concept.getLeadinTerms().toString()));
				writer.write(checkString(concept.getUseTerms().toString()));
				writer.write(checkString(concept.getSearchingInformation().toString()));   
				writer.write(checkString(concept.getScopeNotes()));   
				/*
				// HH 12/15/2014 to fixe new line and extra space of sinfo and scope notes
				//--
				writer.write(checkSearchInfoAndNotes (checkString(concept.getSearchingInformation().toString())));
				writer.write(checkSearchInfoAndNotes (checkString(concept.getScopeNotes())));
				//--
				*/
				writer.write(checkString(concept.getUseFor()));
				writer.write(checkString(concept.getStatus()));
				writer.write(checkString(concept.getChemicalAspects()));
				writer.newLine();
				i++;
			}
			writer.close();
			
			//HH 12/17/2014
			System.out.println("Total number of records processed: " + i);
			
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
				  StringBuffer useForString = new StringBuffer();
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
				  				concept.setMainTermDisplay((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(NON_DESCRIPTOR)){
								concept.setStatus("L");
				  				concept.setMainTermDisplay((cNode.getTextContent().trim()));
				  			}

				  			if(nodeName.equalsIgnoreCase(TYPE)){
								concept.setType(cNode.getTextContent().trim());
							}
							if(nodeName.equalsIgnoreCase(YEAR)){
								concept.setYear(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(CAS_NUMBER)){
				  				concept.setCasNumber(cNode.getTextContent().trim());
				  			}

				  			if(nodeName.equalsIgnoreCase(CAS_NUMBER_BROADER_TERM)){
				  				concept.setCasNumberBroadTerm((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(CHEMICAL_ASPECTS)){
				  				concept.setChemicalAspects((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(BROADER_TERMS)){
				  				concept.setBroaderTerms((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(NARROWER_TERMS)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setNarrowerTerms((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(SA)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setSA((cNode.getTextContent().trim()));
				  			}
				  			if(nodeName.equalsIgnoreCase(SEE)){
								//System.out.println("NODE-TYPE "+cNode.getNodeType()+" NODE-NAME "+cNode.getNodeName()+" NODE-VALUE "+(cNode).getTextContent());
				  				concept.setSEE((cNode.getTextContent().trim()));
				  			}

				  			if(nodeName.equalsIgnoreCase(USE_FOR)){
								appendUseFor(cNode.getChildNodes(),useForString);
				  			}
				  			if(nodeName.equalsIgnoreCase(USE_TERMS)){
								concept.setUseTerms(getUseTerms(cNode.getChildNodes()));
							}
							if(nodeName.equalsIgnoreCase(SEARCHING_INFORMATION)){
								concept.setSearchingInformation(cNode.getTextContent().trim());
				  			}
				  			if(nodeName.equalsIgnoreCase(SCOPE_NOTES)){
								concept.setScopeNotes(cNode.getTextContent().trim());
							}
				  		}

				  }
				  	
				 // HH: 01/25/2017 bug fix, as per Deborah request, need to append "<YEAR>" value to main term ONLY for "cross references" records
				  	if(concept.getType() !=null && concept.getType().equals("XRF Cross-Reference"))
				  	{
				  		if(concept.getYear() !=null && ! (concept.getYear().isEmpty()))
				  		{
				  			concept.setMainTermDisplay(concept.getYear());
				  		}
				  	}
				  //System.out.println("USEFORSTRING= "+useForString.toString());   //HH 12/15/2014: temp comment out when processed Thes ept 2015, uncomment for debug
				  concept.setUseFor(useForString.toString());
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

	private String getUseTerms(NodeList useTermsList)
	{
		String useTermString = "";
		for(int i=0;i<useTermsList.getLength();i++){
			Node cNode = useTermsList.item(i);
			if(cNode.getNodeType()==Node.ELEMENT_NODE){
				String nodeName = cNode.getNodeName().trim();
				if(nodeName.equalsIgnoreCase(LABEL)){
					useTermString = cNode.getTextContent().replaceAll("\\.","").trim();
				}
				else if(nodeName.equalsIgnoreCase(STRING)){
					useTermString = "<STRING>"+cNode.getTextContent()+"</STRING>";
				}
				else if(nodeName.equalsIgnoreCase(PLUS)){
					NodeList plusNodesList = cNode.getChildNodes();
					String plusString = "";

					for(int j=0;j<plusNodesList.getLength();j++){
						Node plusNode = plusNodesList.item(j);
						String plusNodeName = plusNode.getNodeName().trim();
						if(plusNodeName.equalsIgnoreCase(LABEL)){
							if(plusString.length() >0){
								plusString = plusString+";"+plusNode.getTextContent().trim();
							}
							else{
								plusString = plusNode.getTextContent().trim();
							}
						}
					}
					useTermString = (useTermString) + " plus "+(plusString);
				}
			}
		}
		// System.out.println("USETERMSSTRING= "+useTermString);    //HH 12/15/2014: temp comment out when processed Thes ept 2015, uncomment for debug
		return useTermString;

	}

	private void appendUseFor(NodeList useForList, StringBuffer useForString)
	{

		for(int i=0;i<useForList.getLength();i++){
			Node cNode = useForList.item(i);
			if(cNode.getNodeType()==Node.ELEMENT_NODE){
				String nodeName = cNode.getNodeName().trim();
				//System.out.println("NODENAME= "+nodeName);                            //HH 12/15/2014: temp comment out when processed Thes ept 2015, uncomment for debug
				if(nodeName.equalsIgnoreCase(LABEL)){
					//System.out.println("LABEL CONTENT= "+cNode.getTextContent());   //HH 12/15/2014: temp comment out when processed Thes ept 2015, uncomment for debug
					useForString.append((cNode.getTextContent().trim()));
				}


				if(nodeName.equalsIgnoreCase(PLUS)){

					NodeList plusNodeList = cNode.getChildNodes();
					String plusString = "";

					for(int j=0;j<plusNodeList.getLength();j++){
						Node plusNode = plusNodeList.item(j);
						String plusNodeName = plusNode.getNodeName().trim();

						if(plusNodeName.equalsIgnoreCase(LABEL)){
							if(plusString.length() >0){
								plusString = plusString+", "+plusNode.getTextContent().trim();
							}
							else{
								plusString = plusNode.getTextContent().trim();
							}

						}
					}
					useForString.append(" plus "+(plusString));
				}
			}
		}
		useForString.append(";");
		

	}

	private String checkString(String string){
		String outString="";
		if(string==null){
			outString = tab;
		}
		if(string!=null){
			if(string.endsWith(";")){
				String noTrailingSemi = string.substring(0, string.length()-1);
				outString = noTrailingSemi;
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

	private String initialCapitalize(String line)
	{
	  if(line.charAt(0)=='-')
	  {
		  return "-"+Character.toUpperCase(line.charAt(1)) + line.substring(2).toLowerCase();
	  }

	  return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
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
	
	/*
	//HH 12/15/2014 to resolve new lines issue of source file for <INF> and <SRH>
private String checkSearchInfoAndNotes(String searchinfo)
{
	String newline = "\n";
	int lastindex = 0;
	int count = 0;
	
	
	
	if(searchinfo != null && !(searchinfo.isEmpty()))
	{
		text = searchinfo;
		
		if(searchinfo.contains("\n"))
		{
			reccount ++;
		}
		// HH 12/15/2014 replace New line and extra spaces of <SRH> , <INF>
		
		while (lastindex !=-1)
		{
			lastindex = searchinfo.indexOf(newline,lastindex);
			
			if(lastindex !=-1)
			{
				count ++;
				
				lastindex= lastindex+ newline.length();
				
				//System.out.println("count:"+count);
				
			}
		}
		
		for(int i=0; i<count;i++)
		{
		
			if(searchinfo.contains("\n"))
			{
			
				text = perl.substitute("s/\n//",text);
			
				if(text.contains(extraspace))
				{
				  //System.out.println(text.replace(extraspace, " "));
					text = text.replace(extraspace, " ");
				
				}
			
			}
		}
		
	}
	return text;
			
}
*/

}
